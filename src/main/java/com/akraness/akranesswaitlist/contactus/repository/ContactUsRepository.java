package com.akraness.akranesswaitlist.contactus.repository;

import com.akraness.akranesswaitlist.contactus.dto.ContactUsResponse;
import com.akraness.akranesswaitlist.contactus.entity.ContactUs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ContactUsRepository extends JpaRepository<ContactUs, Long> {
    Optional<ContactUs> getContactMessageByEmail(String email);
}
