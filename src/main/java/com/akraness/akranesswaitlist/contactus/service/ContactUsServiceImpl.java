package com.akraness.akranesswaitlist.contactus.service;

import com.akraness.akranesswaitlist.barter.service.OfferServiceImpl;
import com.akraness.akranesswaitlist.contactus.dto.ContactUsRequest;
import com.akraness.akranesswaitlist.contactus.dto.ContactUsResponse;
import com.akraness.akranesswaitlist.contactus.entity.ContactUs;
import com.akraness.akranesswaitlist.contactus.repository.ContactUsRepository;
import com.akraness.akranesswaitlist.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactUsServiceImpl implements ContactUsService{


    private final ContactUsRepository repository;
    private final Utility utility;

    @Value("${regex.email}")
    private String emailRegexPattern;
    @Override
    public ResponseEntity<ContactUsResponse> createContactUs(ContactUsRequest request) {
        if(utility.isNullOrEmpty(request.getFullName())){
            return ResponseEntity.badRequest().body(new ContactUsResponse(String.valueOf(HttpStatus.BAD_REQUEST), "Full name is required"));
        }
        if(utility.isNullOrEmpty(request.getEmail())){
            return ResponseEntity.badRequest().body(new ContactUsResponse(String.valueOf(HttpStatus.BAD_REQUEST), "Email is required"));
        }
        if(!utility.isInputValid(request.getEmail(),emailRegexPattern)){
            return ResponseEntity.badRequest().body(new ContactUsResponse(String.valueOf(HttpStatus.BAD_REQUEST), "Provide valid email address"));
        }
        if(utility.isNullOrEmpty(request.getSubject())){
            return ResponseEntity.badRequest().body(new ContactUsResponse(String.valueOf(HttpStatus.BAD_REQUEST), "Subject is required"));
        }

        if(utility.isNullOrEmpty(request.getMessage())){
            return ResponseEntity.badRequest().body(new ContactUsResponse(String.valueOf(HttpStatus.BAD_REQUEST), "Message body is required"));
        }

        ContactUs contactUs = ContactUs.builder()
                .fullName(request.getFullName())
                .subject(request.getSubject())
                .email(request.getEmail())
                .message(request.getMessage())
                .build();

        repository.save(contactUs);

        return ResponseEntity.ok().body(new ContactUsResponse(String.valueOf(HttpStatus.CREATED), "You message has been received. We will get back to you shortly"));
    }

    @Override
    public ResponseEntity<ContactUsResponse> getContactUsMessageByEmail(String email) {
        Optional<ContactUs> contactUsOpt = repository.getContactMessageByEmail(email);
        if(!contactUsOpt.isPresent()){
            return ResponseEntity.badRequest().body(new ContactUsResponse(String.valueOf(HttpStatus.BAD_REQUEST), "No contact us message with this email found"));
        }
        ContactUs contactUs = contactUsOpt.get();

        return ResponseEntity.ok().body(
                ContactUsResponse.builder()
                        .fullName(contactUs.getFullName())
                        .subject(contactUs.getSubject())
                        .email(contactUs.getEmail())
                        .message(contactUs.getMessage())
                        .status(String.valueOf(HttpStatus.FOUND))
                        .build()
        );
    }

    @Override
    public List<ContactUsResponse> getAllContactMessage() {
        List<ContactUs> contactUsList = repository.findAll();
        return contactUsList.stream().map(this::mapToContactUsResponse).collect(Collectors.toList());
    }

    private ContactUsResponse mapToContactUsResponse(ContactUs contactUs) {
        return ContactUsResponse.builder()
                .fullName(contactUs.getFullName())
                .email(contactUs.getEmail())
                .subject(contactUs.getSubject())
                .message(contactUs.getMessage())
                .build();
    }
}
