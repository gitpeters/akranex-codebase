package com.akraness.akranesswaitlist.serviceimpl;

import java.util.ArrayList;
import java.util.Optional;

import com.akraness.akranesswaitlist.exception.ApplicationAuthenticationException;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
	private final IUserRepository userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<com.akraness.akranesswaitlist.entity.User> user = userDao.findByUsername(username);
		if (!user.isPresent()) {
			throw new ApplicationAuthenticationException("User not found with username: " + username);
		}
		com.akraness.akranesswaitlist.entity.User user_entity = user.get();
		if (!user_entity.isActive()) {
			throw new ApplicationAuthenticationException("Access denied, your account is currently locked. ");
		}
		return new org.springframework.security.core.userdetails.User(user_entity.getUsername(), user_entity.getPassword(),
				new ArrayList<>());
	}
}