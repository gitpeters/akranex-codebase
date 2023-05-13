package com.akraness.akranesswaitlist.repository;

import com.akraness.akranesswaitlist.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Optional<Referral> findByNewUserId(Long newUserId);
}
