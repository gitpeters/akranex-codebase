package com.akraness.akranesswaitlist.contactus.controller;

import com.akraness.akranesswaitlist.contactus.dto.ContactUsRequest;
import com.akraness.akranesswaitlist.contactus.dto.ContactUsResponse;
import com.akraness.akranesswaitlist.contactus.service.ContactUsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contact-us")
public class ContactUsController {
    private final ContactUsService contactUsService;

    @PostMapping("/create")
    public ResponseEntity<?> createContactUs(@RequestBody ContactUsRequest request){
        return contactUsService.createContactUs(request);
    }

    @GetMapping("/get-all-contact-us-messages")
    public ResponseEntity<List<ContactUsResponse>> getContactUsMessages(){
        return ResponseEntity.ok().body(contactUsService.getAllContactMessage());
    }

    @GetMapping()
    public ResponseEntity<ContactUsResponse> getContactMessageByEmail(@RequestParam("email") String email){
        return contactUsService.getContactUsMessageByEmail(email);
    }
}
