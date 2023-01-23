package com.akraness.akranesswaitlist.repository;

import com.akraness.akranesswaitlist.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface IUserRepository extends CrudRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
}
