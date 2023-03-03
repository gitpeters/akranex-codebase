package com.akraness.akranesswaitlist.serviceimpl;

import com.akraness.akranesswaitlist.dto.LoginRequestDto;
import com.akraness.akranesswaitlist.dto.LoginResponseDto;
import com.akraness.akranesswaitlist.dto.Response;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import com.akraness.akranesswaitlist.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.memory.UserAttributeEditor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final JwtUserDetailsService userDetailsService;
    private final IUserRepository userDao;

    public ResponseEntity<Response> createAuthenticationToken(LoginRequestDto authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        Optional<User> user_op = userDao.findByUsername(authenticationRequest.getUsername());
        User user = user_op.get();

        final String token = jwtTokenUtil.generateToken(userDetails);
        LoginResponseDto resp_login = new LoginResponseDto(token,authenticationRequest.getUsername(),
                user.getMobileNumber(),user.getFirstName(),user.getLastName(),user.getCountryCode());

        Response resp = new Response();
        resp.setData(resp_login);
        resp.setDescription("Successful");
        resp.setCode("200");
        return ResponseEntity.ok(resp);
    }


    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
