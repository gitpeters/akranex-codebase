package com.akraness.akranesswaitlist.serviceimpl;

import java.util.ArrayList;
import java.util.Optional;

import com.akraness.akranesswaitlist.exception.ApplicationAuthenticationException;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
	private final IUserRepository userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.akraness.akranesswaitlist.entity.User user = userDao.findByUsername(username).orElseThrow(() ->
				new ApplicationAuthenticationException("Invalid username and password combination"));

		if (!user.isActive()) {
			throw new ApplicationAuthenticationException("Access denied, your account is currently locked. ");
		}
		if (!user.isMobileVerified()) {
			throw new ApplicationAuthenticationException("Access denied, your mobile number has not been verified. ");
		}
		if (!user.isEmailVerified()) {
			throw new ApplicationAuthenticationException("Access denied, your email address has not been verified. ");
		}
		if (user.getMagicPin() == null || user.getMagicPin().isBlank()) {
			throw new ApplicationAuthenticationException("Access denied, your need to create your magic pin first.");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
	}
}