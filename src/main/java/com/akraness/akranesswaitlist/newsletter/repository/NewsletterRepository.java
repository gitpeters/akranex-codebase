package com.akraness.akranesswaitlist.newsletter.repository;

import com.akraness.akranesswaitlist.newsletter.entity.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {
    Optional<Newsletter> getNewsletterByEmail(String email);
}
