package com.akraness.akranesswaitlist.repository;

import com.akraness.akranesswaitlist.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IUserRepository extends CrudRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    Optional<User> findByUsername(String username);
    Optional<User> findByMobileNumber(String mobileNumber);
}
