package com.akraness.akranesswaitlist.serviceimpl;

import com.akraness.akranesswaitlist.dto.*;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.entity.WaitList;
import com.akraness.akranesswaitlist.enums.NotificationType;
import com.akraness.akranesswaitlist.exception.DuplicateException;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import com.akraness.akranesswaitlist.repository.IWaitList;
import com.akraness.akranesswaitlist.service.INotificationService;
import com.akraness.akranesswaitlist.service.IService;
import com.akraness.akranesswaitlist.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Service implements IService {
    private final IWaitList waitListRepository;
    private final IUserRepository userRepository;
    private final INotificationService notificationService;
    private final Utility utility;
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String ALLOWED_COUNTRIES = "allowed-countries";
    @Value("${regex.email}")
    private String emailRegexPattern;
    @Value("${regex.mobile-no}")
    private String phoneRegexPattern;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    @Value("${email.otp.verification}")
    private String emailOtpVerificationTemplate;

    @Override
    public ResponseEntity<Response> joinWaitList(WaitListRequestDto request) {
        if (waitListRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateException(request.getEmail());
        }
        Response response;
        WaitList entity = new WaitList();
        entity.setEmail(request.getEmail());
        waitListRepository.save(entity);
        response = new Response("200", "Successful", null);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Response> getAllWaitingUsers() {
        List<WaitListRequestDto> dtoList = new ArrayList<>();
        Response response = new Response("200", "Successful", null);
        List<WaitList> all_waiting = (List<WaitList>) waitListRepository.findAll();
        all_waiting.forEach(x -> {
            WaitListRequestDto dto = new WaitListRequestDto();
            dto.setEmail(x.getEmail());
            dtoList.add(dto);
        });
        response.setData(dtoList);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Response> getAllCountries() {
        try {
            ObjectMapper om = new ObjectMapper();
            JSONObject jsonObject = null;
            String countryData = redisTemplate.opsForValue().get(ALLOWED_COUNTRIES);
            if (Objects.nonNull(countryData))
                jsonObject = om.readValue(countryData, JSONObject.class);
            else {
                jsonObject = om.readValue(this.getClass().getClassLoader()
                        .getResourceAsStream("country-details.json"), JSONObject.class);
                redisTemplate.opsForValue().set(ALLOWED_COUNTRIES, jsonObject.toString());
            }
            Response resp = new Response("200", "Successful", null);
            resp.setData(jsonObject);
            return ResponseEntity.ok(resp);
        } catch (IOException ex) {
            throw new NotFoundException("Error occurred while retrieving countries list, please contact admin.");
        }
    }

    @Override
    public ResponseEntity<Response> preSignUp(EmailVerificationRequestDto requestDto) throws JsonProcessingException {
        if (!utility.isInputValid(requestDto.getEmail(),emailRegexPattern)) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Email is invalid.", null));
        }

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Email exists already.", null));
        }
        EmailVerificationDto verificationDto = EmailVerificationDto.builder()
                .email(requestDto.getEmail())
                .code(utility.generateEmailVeirificationCode())
                .build();

        redisTemplate.opsForValue().set(requestDto.getEmail(), new ObjectMapper().writeValueAsString(verificationDto), Duration.ofMinutes(15));
        NotificationDto notificationDto = NotificationDto.builder()
                .message("Your verification code is " + verificationDto.getCode())
                .recipient(requestDto.getEmail())
                .subject("verification")
                .type(NotificationType.EMAIL)
                .templateId(emailOtpVerificationTemplate)
                .substitutions(Map.of("email",requestDto.getEmail(),"otp",verificationDto.getCode()))
                .build();
        notificationService.sendNotification(notificationDto);
        return ResponseEntity.ok().body(new Response("200", "Please proceed to verifying the code sent to your email.", null));
    }

    @Override
    public ResponseEntity<Response> verifyEmail(EmailVerificationDto requestDto) throws JsonProcessingException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Email exists already.", null));
        }
        String requestObj = redisTemplate.opsForValue().get(requestDto.getEmail());
        if (requestObj != null) {
            EmailVerificationDto savedCopy = new ObjectMapper().readValue(requestObj, EmailVerificationDto.class);
            if (!requestDto.getCode().equalsIgnoreCase(savedCopy.getCode()))
                return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Code does not belongs to the provided email", null));
            savedCopy.setVerified(true);
            redisTemplate.opsForValue().getAndSet(requestDto.getEmail(), new ObjectMapper().writeValueAsString(savedCopy));
            log.info("new validity object is: {} ", redisTemplate.opsForValue().get(requestDto.getEmail()));
            return ResponseEntity.ok().body(new Response("200", "Email verified successfully.", null));
        } else {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Email does not exist or code has expired.", null));
        }
    }

    @Override
    public ResponseEntity<Response> signup(SignupRequestDto requestDto) throws JsonProcessingException {
        if (!utility.isInputValid(requestDto.getEmail(),emailRegexPattern))
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "Email is invalid.", null));

        if(userRepository.existsByEmail(requestDto.getEmail())) {
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "Email exists already.", null));
        }

        if (userRepository.existsByMobileNumber(requestDto.getMobileNumber())) {
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "Mobile number exists already.", null));
        }
        String requestObj = redisTemplate.opsForValue().get(requestDto.getEmail());
        if (requestObj != null) {
            EmailVerificationDto savedCopy = new ObjectMapper().readValue(requestObj, EmailVerificationDto.class);
            if(!savedCopy.isVerified()){
                log.info("Email verification is required before you can proceed. User is {}", requestDto.getEmail());
                return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Email has not been verified.", null));
            }
        } else {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Email verification code does not exist or has expired", null));
        }

        var user  = User.builder()
                .countryCode(requestDto.getCountryCode())
                .accountType(requestDto.getAccountType())
                .active(true)
                .emailVerified(true)
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .mobileNumber(requestDto.getMobileNumber())
                .email(requestDto.getEmail())
                .loginAttempts(0)
                .dateOfBirth(requestDto.getDateOfBirth())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .username(requestDto.getEmail())
                .mobileVerified(false)
                .firstLogin(true)
                .build();
        userRepository.save(user);
        redisTemplate.delete(requestDto.getEmail());
        log.info("Successfully deleted user email {} from redis store.",requestDto.getEmail());
        NotificationDto notificationDto = buildSignUpNotificationDto(requestDto);
        notificationService.sendNotification(notificationDto);
        return ResponseEntity.ok().body(new Response("200", "Successfully created account.", null));
    }

    private NotificationDto buildSignUpNotificationDto(SignupRequestDto requestDto) {
        return NotificationDto.builder()
                .message("Welcome to Akranex Inc. You can start using our system with ease now.")
                .recipient(requestDto.getEmail())
                .subject("Akranex Signup Notification")
                .type(NotificationType.EMAIL)
                .build();
    }
}
