package com.akraness.akranesswaitlist.serviceimpl;

import com.akraness.akranesswaitlist.dto.LoginRequestDto;
import com.akraness.akranesswaitlist.dto.LoginResponseDto;
import com.akraness.akranesswaitlist.dto.Response;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.exception.ApplicationAuthenticationException;
import com.akraness.akranesswaitlist.identitypass.service.IdentityPassService;
import com.akraness.akranesswaitlist.repository.IUserRepository;
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

    public ResponseEntity<Response> createAuthenticationToken(LoginRequestDto authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        Optional<User> user_op = userDao.findByUsername(authenticationRequest.getUsername());
        User user = user_op.get();

        final String token = jwtTokenUtil.generateToken(userDetails);

        //call identitypass service
        List<String> dataVerificationCountries = Arrays.asList("NG", "ZA", "UG", "GH", "SL", "KE");
        Map<String, Object> payload = null;
        if(dataVerificationCountries.contains(user.getCountryCode())) {
            payload = identityPassService.getCountryDataPayload(user.getCountryCode());
        }

        LoginResponseDto resp_login = new LoginResponseDto(token,user.getId(), authenticationRequest.getUsername(),
                user.getMobileNumber(),user.getFirstName(),user.getLastName(),user.getCountryCode(),
                user.getDateOfBirth().toString(), user.getGender(),user.isEmailVerified(),user.isMobileVerified(),user.getAkranexTag(), user.getKycStatus(), user.getKycStatusMessage(), payload);

        Response resp = new Response();
        resp.setData(resp_login);
        resp.setDescription("Successful");
        resp.setCode("200");
        return ResponseEntity.ok(resp);
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
