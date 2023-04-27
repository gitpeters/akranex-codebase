package com.akraness.akranesswaitlist.newsletter.controller;

import com.akraness.akranesswaitlist.newsletter.dto.NewsletterRequest;
import com.akraness.akranesswaitlist.newsletter.dto.NewsletterResponse;
import com.akraness.akranesswaitlist.newsletter.service.NewsletterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/newsletter")
public class NewsletterController {
    private final NewsletterService newsletterService;

    @PostMapping("/create")
    public ResponseEntity<?> createNewsletter(@RequestBody NewsletterRequest request) throws JsonProcessingException {
        return newsletterService.createNewsletter(request);
    }

    @GetMapping("/get-all-newsletter")
    public ResponseEntity<List<NewsletterResponse>> getNewsletterMessages(){
        return ResponseEntity.ok().body(newsletterService.getAllNewsletterRequest());
    }

    @GetMapping()
    public ResponseEntity<NewsletterResponse> getNewsletterByEmail(@RequestParam("email") String email){
        return newsletterService.getNewsletterRequestByEmail(email);
    }
}
