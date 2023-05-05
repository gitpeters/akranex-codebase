package com.akraness.akranesswaitlist.repository;

import com.akraness.akranesswaitlist.entity.UserFCMToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFCMTokenRepository extends JpaRepository<UserFCMToken, Long> {
    List<UserFCMToken> findByUserId(Long userId);
}
