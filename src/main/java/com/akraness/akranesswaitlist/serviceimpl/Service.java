package com.akraness.akranesswaitlist.serviceimpl;

import com.akraness.akranesswaitlist.chimoney.dto.SubAccountDto;
import com.akraness.akranesswaitlist.chimoney.dto.SubAccountRequestDto;
import com.akraness.akranesswaitlist.chimoney.service.SubAccountService;
import com.akraness.akranesswaitlist.dto.*;
import com.akraness.akranesswaitlist.entity.*;
import com.akraness.akranesswaitlist.enums.NotificationType;
import com.akraness.akranesswaitlist.enums.PinType;
import com.akraness.akranesswaitlist.enums.ReferralStatus;
import com.akraness.akranesswaitlist.exception.DuplicateException;
import com.akraness.akranesswaitlist.identitypass.service.IdentityPassService;
import com.akraness.akranesswaitlist.repository.*;
import com.akraness.akranesswaitlist.service.INotificationService;
import com.akraness.akranesswaitlist.service.IService;
import com.akraness.akranesswaitlist.service.firebase.PushNotificationService;
import com.akraness.akranesswaitlist.util.Utility;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Service implements IService {
    private final IWaitList waitListRepository;
    private final IUserRepository userRepository;
    private final INotificationService notificationService;
    private final Utility utility;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ICountryRepository countryRepository;
    private final SubAccountService subAccountService;
    private final ReferralRepository referralRepository;
    private final IdentityPassService identityPassService;
    private final PushNotificationService pushNotification;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final BlobContainerClient blobContainerClient;
    private static final String ALLOWED_COUNTRIES = "allowed-countries";
    @Value("${regex.email}")
    private String emailRegexPattern;
    @Value("${regex.mobile-no}")
    private String phoneRegexPattern;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    @Value("${email.otp.verification}")
    private String emailOtpVerificationTemplate;
    @Value("${default.pin.allowed}")
    private boolean defaultPinAllowed;
    @Value("${default.pin.number}")
    private String defaultPinValue;
    @Value("${email.otp.reset-password}")
    private String resetPasswordOtpTemplate;

    private static final String SUPPORTED_COUNTRIES = "supported-countries";

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
        return generateAndSendOtp(requestDto.getEmail(), false);
    }

    @Override
    public ResponseEntity<Response> verifyEmail(EmailVerificationDto requestDto) throws JsonProcessingException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Email exists already.", null));
        }
        String requestObj = redisTemplate.opsForValue().get(requestDto.getEmail());
        if (requestObj != null) {
            EmailVerificationDto savedCopy = new ObjectMapper().readValue(requestObj, EmailVerificationDto.class);
            if (defaultPinAllowed && (!requestDto.getCode().equalsIgnoreCase(savedCopy.getCode()) && !requestDto.getCode().equalsIgnoreCase("007712")))
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
        User referralUser = null;
        // checking referral code
        if(requestDto.getReferralCode()!=null && !requestDto.getReferralCode().isEmpty()){
            Optional<User> referralUserObj = userRepository.findByAkranexTag(requestDto.getReferralCode());
            if(!referralUserObj.isPresent()){
                return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(),"Referral code not valid", null));
            }
            referralUser = referralUserObj.get();
        }

        String otp = utility.generateEmailVeirificationCode();
        PhoneVerificationDto verificationDto = PhoneVerificationDto.builder()
                .phoneNumber(requestDto.getMobileNumber())
                .code(otp)
                .verified(false)
                .build();

        redisTemplate.opsForValue().set(requestDto.getMobileNumber(), new ObjectMapper().writeValueAsString(verificationDto), Duration.ofMinutes(5));
        NotificationDto notificationDto_sms = NotificationDto.builder()
                .message("Akranex verification code is " + otp)
                .recipient(requestDto.getMobileNumber())
                .subject("verification")
                .type(NotificationType.SMS)
                .build();
        notificationService.sendNotification(notificationDto_sms);
//
        User newUser = saveUser(requestDto);
        saveReferralDetailsToDb(newUser.getId(), referralUser);
        redisTemplate.delete(requestDto.getEmail());
        log.info("Successfully deleted user email {} from redis store.",requestDto.getEmail());
        NotificationDto notificationDto = buildSignUpNotificationDto(requestDto);
        notificationService.sendNotification(notificationDto);
        return ResponseEntity.ok().body(new Response("200", "Successfully created account.", null));
    }

//

    private void saveReferralDetailsToDb(Long newUserId, User referralUser) {
//        User newUser = saveUser(requestDto);
//        if (newUser == null) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save user");
//        }
        if(referralUser==null) return;
        Referral referral = Referral.builder()
                .referralUserId(referralUser.getId())
                .newUserId(newUserId)
                .referralRewardAmount(5.0)
                .newUserFundedAmount(0.0)
                .referralRewardStatus(String.valueOf(ReferralStatus.PENDING))
                .build();
        referralRepository.save(referral);
    }

    private User saveUser(SignupRequestDto requestDto) {
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
                .gender(requestDto.getGender())
                .build();
        userRepository.save(user);
        return user;
    }

    @Override
    public ResponseEntity<Response> verifyPhone(PhoneVerificationDto requestDto) throws JsonProcessingException {
        if (!userRepository.existsByMobileNumber(requestDto.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "Phone number does not exists.", null));
        }

        Optional<User> userOp = userRepository.findByMobileNumber(requestDto.getPhoneNumber());
        User user = userOp.get();
        String requestObj = redisTemplate.opsForValue().get(requestDto.getPhoneNumber());

        if (requestObj != null) {
            PhoneVerificationDto savedCopy = new ObjectMapper().readValue(requestObj, PhoneVerificationDto.class);
            if (defaultPinAllowed && (!requestDto.getCode().equalsIgnoreCase(savedCopy.getCode()) && !requestDto.getCode().equals("007712")))
                return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Code does not belongs to the provided phone number", null));
            user.setMobileVerified(true);
            userRepository.save(user);
            redisTemplate.delete(requestDto.getPhoneNumber());
            return ResponseEntity.ok().body(new Response("200", "Phone number verified successfully.", null));
        } else {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Phone number does not exist or code has expired.", null));
        }
    }

    @Override
    public ResponseEntity<Response> createMagicPin(MagicPinRequestDto requestDto) {
        if(!userRepository.existsByEmail(requestDto.getEmail())) {
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "No match found with the provided email.", null));
        }
        Optional<User> userOp = userRepository.findByUsername(requestDto.getEmail());
        User user = userOp.get();
        if(!user.isMobileVerified())
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "Mobile number must be verified first.", null));

        if(!user.isEmailVerified())
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "Email must be verified first.", null));

        user.setMagicPin(passwordEncoder.encode(requestDto.getMagicPin()));
        userRepository.save(user);
        return ResponseEntity.ok(new Response(String.valueOf(HttpStatus.OK),"Successful",null));
    }

    @Override
    public ResponseEntity<Response> createTransactionPin(TransactionPinRequestDto requestDto, Principal principal) {
        return null;
    }

    @Override
    public ResponseEntity<Response> verifyPin(VerifyPinRequestDto requestDto) {

        Optional<User> userOp = userRepository.findByUsername(requestDto.getUsername());
        if(!userOp.isPresent())
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Provided access token is not valid.", null));

        User user = userOp.get();
        if(requestDto.getPinType().equals(PinType.MAGIC)){
            if(defaultPinAllowed && (!requestDto.getPin().equals("007712") && !passwordEncoder.matches(requestDto.getPin(),user.getMagicPin()))){
                return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST.value()),"Invalid pin provided.",null));
            }
            return ResponseEntity.ok(new Response(String.valueOf(HttpStatus.OK.value()),"Successful",null));
        }else if(requestDto.getPinType().equals(PinType.TRANSACTION) && !user.getTransactionPin().equals(requestDto.getPin())){
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.OK),"Invalid pin provided.",null));
        }
        return ResponseEntity.ok(new Response(String.valueOf(HttpStatus.OK.value()), "Successful", null));
    }

    @Override
    public ResponseEntity<Response> resendPhoneOtpCode(ResendPhoneOtpRequest requestDto) throws JsonProcessingException {
        if (!userRepository.existsByMobileNumber(requestDto.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "Mobile number does not exists.", null));
        }

        String otp = utility.generateEmailVeirificationCode();
        PhoneVerificationDto verificationDto = PhoneVerificationDto.builder()
                .phoneNumber(requestDto.getPhoneNumber())
                .code(otp)
                .verified(false)
                .build();

        redisTemplate.opsForValue().getAndSet(requestDto.getPhoneNumber(), new ObjectMapper().writeValueAsString(verificationDto));
        NotificationDto notificationDto_sms = NotificationDto.builder()
                .message("Akranex verification code is " + otp)
                .recipient(requestDto.getPhoneNumber())
                .subject("verification")
                .type(NotificationType.SMS)
                .build();
        notificationService.sendNotification(notificationDto_sms);

        return ResponseEntity.ok(new Response(String.valueOf(HttpStatus.OK),"Successful",null));
    }

    @Override
    public List<Country> getCountries() {
        try {
            List<Country> countries = new ArrayList<>();

            ObjectMapper om = new ObjectMapper();
            String countryData = redisTemplate.opsForValue().get(SUPPORTED_COUNTRIES);
            if (Objects.nonNull(countryData)) {
                return om.readValue(countryData, new TypeReference<List<Country>>(){});
            }

            countries = countryRepository.findAll();
            redisTemplate.opsForValue().set(SUPPORTED_COUNTRIES, om.writeValueAsString(countries));

            return countries;

        } catch (IOException ex) {
            throw new NotFoundException("Error occurred while retrieving countries list, please contact admin.");
        }
    }

    @Override
    public ResponseEntity<?> passwordResetRequest(PasswordResetRequestDto requestDto) throws JsonProcessingException {
        return generateAndSendOtp(requestDto.getEmail(), true);
    }

    @Override
    public ResponseEntity<Response> resetPassword(ResetPasswordDto requestDto) throws JsonProcessingException {
        if(!requestDto.getPassword().equalsIgnoreCase(requestDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Password do not match.", null));
        }

        String requestObj = redisTemplate.opsForValue().get(requestDto.getEmail());
        if(requestObj == null) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Email does not exist or code has expired.", null));
        }

        if (!userRepository.existsByEmail(requestDto.getEmail())) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Record with this email does not exists.", null));
        }

        User user = userRepository.findByEmail(requestDto.getEmail()).get();
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        userRepository.save(user);

        redisTemplate.opsForValue().getAndDelete(requestDto.getEmail());
        return ResponseEntity.ok(new Response(String.valueOf(HttpStatus.OK),"Successful",null));

    }

    @Override
    public ResponseEntity<Response> createAkranexTag(AkranexTagCreationRequestDto requestDto) throws JsonProcessingException {
        Optional<User> userObj = userRepository.findByUsername(requestDto.getUsername());

        if(!userObj.isPresent()) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(),
                    "No user found with the specifed username", null));
        }

        if(userRepository.findByAkranexTag(requestDto.getAkranexTag()).isPresent()) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(),
                    "Tag "+ requestDto.getAkranexTag() +" already exist for another user.", null));
        }

        User user = userObj.get();
        if(user.getAkranexTag() != null) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(),
                    "You cannot recreate your AkranexTag", null));
        }

        user.setAkranexTag(requestDto.getAkranexTag());
        userRepository.save(user);

        SubAccountRequestDto subAccountRequestDto = SubAccountRequestDto.builder()
                .userId(user.getId())
                .akranexTag(user.getAkranexTag())
                .countryCode(user.getCountryCode())
//                .email(user.getEmail())
                .build();
        subAccountService.createSubAccount(subAccountRequestDto);

        return ResponseEntity.ok(new Response(String.valueOf(HttpStatus.OK),"Successful",null));
    }

    @Override
    public ResponseEntity<Response> checkAkranexTag(String akranexTag) throws JsonProcessingException {
        Response response = new Response();
        response.setStatus(false);

        if(userRepository.findByAkranexTag(akranexTag).isPresent()) {
            response.setStatus(true);
        }

        response.setCode(String.valueOf(HttpStatus.OK));
        return ResponseEntity.ok().body(response);
    }

    private ResponseEntity<Response> generateAndSendOtp(String email, boolean forResetPassword) throws JsonProcessingException {
        if (!utility.isInputValid(email, emailRegexPattern)) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Email is invalid.", null));
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(), "Email exists already.", null));
        }
        String otp = utility.generateEmailVeirificationCode();
        EmailVerificationDto verificationDto = EmailVerificationDto.builder()
                .email(email)
                .code(otp)
                .build();

        redisTemplate.opsForValue().set(email, new ObjectMapper().writeValueAsString(verificationDto), Duration.ofMinutes(15));
        NotificationDto notificationDto = NotificationDto.builder()
                .message("Your verification code is " + otp)
                .recipient(email)
                .subject("verification")
                .type(NotificationType.EMAIL)
                .templateId(emailOtpVerificationTemplate)
                .substitutions(Map.of("email",email,"otp",verificationDto.getCode()))
                .build();
        if(forResetPassword) {
            notificationDto.setMessage("Your password reset code is " + otp);
            notificationDto.setSubject("Password reset");
            notificationDto.setTemplateId(resetPasswordOtpTemplate);
        }

        notificationService.sendNotification(notificationDto);
        return ResponseEntity.ok().body(new Response("200", "Please proceed to verifying the code sent to your email.", null));

    }

    private NotificationDto buildSignUpNotificationDto(SignupRequestDto requestDto) {
        return NotificationDto.builder()
                .message("Welcome to Akranex Inc. You can start using our system with ease now.")
                .recipient(requestDto.getEmail())
                .subject("Akranex Signup Notification")
                .type(NotificationType.EMAIL)
                .build();
    }

    @Override
    public ResponseEntity<Response> uploadUserProfilePic(MultipartFile file, Long userId) throws Exception {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(),
                    "You must upload a file", null));
        }

        if (file.getSize() > 1048576) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(),
                    "You cannot upload a file that is more than 1mb", null));
        }

        Optional<User> userObj = userRepository.findById(userId);
        if(!userObj.isPresent()) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(),
                    "User id is not found", null));
        }

        String filename = userId + "_" + file.getOriginalFilename();
        if (!filename.endsWith(".svg") && !filename.endsWith(".jpg") && !filename.endsWith(".png")
                && !filename.endsWith(".jpeg")) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(),
                    "Wrong image extension, image must be svg", null));
        }

        BlobClient blobClient = blobContainerClient.getBlobClient(filename);
        blobClient.upload(file.getInputStream(), file.getSize(), true);
        String path = generateSas(blobClient);
        User user = userObj.get();
        user.setImagePath(path);

        User updatedUser = userRepository.save(user);
        Response response = new Response();
        response.setCode(HttpStatus.OK.name());
        response.setDescription("profile image uploaded successfully");
        response.setData(updatedUser);

        return ResponseEntity.ok().body(response);

    }

    @Override
    public ResponseEntity<Response> getUser(long userId) throws JsonProcessingException {
        Optional<User> userObj = userRepository.findById(userId);

        if (!userObj.isPresent()) {
            return ResponseEntity.badRequest().body(new Response(HttpStatus.BAD_REQUEST.name(),
                    "User not found", null));
        }

        boolean isMagicPinAvailable;




        User user = userObj.get();
        isMagicPinAvailable = user.getMagicPin() != null;

        List<SubAccountDto> subAccountDtos = subAccountService.getUserSubAccountsAndBalance(user.getId());

        //call identitypass service
        List<String> dataVerificationCountries = Arrays.asList("NG", "ZA", "UG", "GH", "SL", "KE");
        Map<String, Object> payload = null;
        if(dataVerificationCountries.contains(user.getCountryCode())) {
            payload = identityPassService.getCountryDataPayload(user.getCountryCode());
        }


        LoginResponseDto getUserResponse = LoginResponseDto
                .builder()
                .id(user.getId())
                .email(user.getUsername())
                .mobileNumber(user.getMobileNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .country(user.getCountryCode())
                .dateOfBirth(user.getDateOfBirth().toString())
                .gender(user.getGender())
                .emailVerified(user.isEmailVerified())
                .mobileVerified(user.isMobileVerified())
                .akranexTag(user.getAkranexTag())
                .kycStatus(user.getKycStatus())
                .kycStatusMessage(user.getKycStatusMessage())
                .subAccountList(subAccountDtos)
                .isMagicPinExist(isMagicPinAvailable)
                .kycDataVerificationPayload(payload)
                .imagePath(user.getImagePath())
                .build();


        Map<String, Object> userData = new HashMap<>();
        userData.put("userDetails", getUserResponse);

        Response response = new Response();
        response.setCode(HttpStatus.OK.name());
        response.setDescription("Retrieve user information");
        response.setData(userData);

        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<Response> editPin(EditPinRequestDto requestDto) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        String email = requestDto.getEmail();

        if (!userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "No match found with the provided email.", null));
        }

        if (!requestDto.getNewPin().equals(requestDto.getNewPinConfirm())) {
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "New pin and confirm pin must be the same.", null));
        }

        Optional<User> userOp = userRepository.findByUsername(email);
        if (!userOp.isPresent()) {
            // Handle the case where the user is not found based on the username/email.
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "User not found.", null));
        }

        User user = userOp.get();

        if (!user.isMobileVerified()) {
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "Mobile number must be verified first.", null));
        }

        if (!user.isEmailVerified()) {
            return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "Email must be verified first.", null));
        }

        if (requestDto.getPinType().equals(PinType.MAGIC)) {
            String magicPin = user.getMagicPin();
            if (magicPin == null || magicPin.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "You don't have a magic pin set yet, please use the set magic pin service.", null));
            }

            if (!passwordEncoder.matches(requestDto.getOldPin(), magicPin)) {
                return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "Wrong old magic pin. Try again", null));
            }

            user.setMagicPin(passwordEncoder.encode(requestDto.getNewPin()));
          ;
            pushNotification.sendPushNotificationToUser(
                   user.getId(), new PushNotificationRequest("Change Magic Pin", "Your magic pin has been changed")
            );
        }

        if (requestDto.getPinType().equals(PinType.TRANSACTION)) {
            String transactionPin = user.getTransactionPin();
            if (transactionPin == null || transactionPin.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "You don't have a transaction pin set yet, please use the set transaction pin service.", null));
            }

            if (!transactionPin.equals(requestDto.getOldPin())) {
                return ResponseEntity.badRequest().body(new Response(String.valueOf(HttpStatus.BAD_REQUEST), "Invalid old transaction pin provided.", null));
            }

            user.setTransactionPin(passwordEncoder.encode(requestDto.getNewPin()));
        }

        // Save the updated user object here.
        userRepository.save(user);

        return ResponseEntity.ok(new Response(String.valueOf(HttpStatus.OK), "Pin updated successfully.", null));
    }


    private String generateSas(BlobClient blobClient) {
        OffsetDateTime expiryTime = OffsetDateTime.now().plusYears(10);
        BlobSasPermission blobSasPermission = new BlobSasPermission().setReadPermission(true);
        BlobServiceSasSignatureValues serviceSasValues = new BlobServiceSasSignatureValues(expiryTime, blobSasPermission);

        String sas = blobClient.generateSas(serviceSasValues);
        String blobUrl = blobClient.getBlobUrl();
        return blobUrl + "?" + sas;
    }
}
