package com.akraness.akranesswaitlist.contactus.service;

import com.akraness.akranesswaitlist.contactus.dto.ContactUsRequest;
import com.akraness.akranesswaitlist.contactus.dto.ContactUsResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ContactUsService {
    ResponseEntity<ContactUsResponse> createContactUs(ContactUsRequest request);
    ResponseEntity<ContactUsResponse> getContactUsMessageByEmail(String email);
    List<ContactUsResponse> getAllContactMessage();
}
