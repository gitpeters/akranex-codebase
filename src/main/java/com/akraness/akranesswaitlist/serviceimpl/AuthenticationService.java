package com.akraness.akranesswaitlist.serviceimpl;

import com.akraness.akranesswaitlist.chimoney.dto.BalanceDto;
import com.akraness.akranesswaitlist.chimoney.dto.SubAccountDto;
import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import com.akraness.akranesswaitlist.chimoney.service.SubAccountService;
import com.akraness.akranesswaitlist.dto.LoginRequestDto;
import com.akraness.akranesswaitlist.dto.LoginResponseDto;
import com.akraness.akranesswaitlist.dto.Response;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.entity.UserFCMToken;
import com.akraness.akranesswaitlist.exception.ApplicationAuthenticationException;
import com.akraness.akranesswaitlist.identitypass.service.IdentityPassService;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import com.akraness.akranesswaitlist.repository.UserFCMTokenRepository;
import com.akraness.akranesswaitlist.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.memory.UserAttributeEditor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final JwtUserDetailsService userDetailsService;
    private final IUserRepository userDao;
    private final IdentityPassService identityPassService;
    private final SubAccountService subAccountService;
    private final UserFCMTokenRepository fcmTokenRepository;

    public ResponseEntity<Response> createAuthenticationToken(LoginRequestDto authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        boolean isMagicPinAvailable;

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        Optional<User> user_op = userDao.findByUsername(authenticationRequest.getUsername());
        User user = user_op.get();
        isMagicPinAvailable = user.getMagicPin() != null;

        final String token = jwtTokenUtil.generateToken(userDetails);

        //call identitypass service
        List<String> dataVerificationCountries = Arrays.asList("NG", "ZA", "UG", "GH", "SL", "KE");
        Map<String, Object> payload = null;
        if(dataVerificationCountries.contains(user.getCountryCode())) {
            payload = identityPassService.getCountryDataPayload(user.getCountryCode());
        }

        List<SubAccountDto> subAccountList = subAccountService.getUserSubAccountsAndBalance(user.getId());

        LoginResponseDto resp_login = new LoginResponseDto(token,user.getId(), authenticationRequest.getUsername(),
                user.getMobileNumber(),user.getFirstName(),user.getLastName(),user.getCountryCode(),
                user.getDateOfBirth().toString(), user.getGender(),user.isEmailVerified(),user.isMobileVerified(),user.getAkranexTag(), user.getKycStatus(), user.getKycStatusMessage(), payload, subAccountList, user.getImagePath(), isMagicPinAvailable);

        Response resp = new Response();
        resp.setData(resp_login);
        resp.setDescription("Successful");
        resp.setCode("200");
        resp.setFcmToken(authenticationRequest.getFcmToken());

        //Save fcm token to db
        saveUserFCMToken(user.getId(), authenticationRequest.getFcmToken());
        return ResponseEntity.ok(resp);

    }
    private void saveUserFCMToken(Long userId, String fcmToken) {
        UserFCMToken userFCMToken = UserFCMToken.builder()
                .userId(userId)
                .fcmToken(fcmToken)
                .build();
        fcmTokenRepository.save(userFCMToken);
    }
    
    private ResponseEntity<?> authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new ApplicationAuthenticationException("Invalid username or password", e);
        }
        return null;
    }
}
