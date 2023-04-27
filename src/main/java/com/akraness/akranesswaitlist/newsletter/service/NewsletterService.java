package com.akraness.akranesswaitlist.newsletter.service;

import com.akraness.akranesswaitlist.newsletter.dto.NewsletterRequest;
import com.akraness.akranesswaitlist.newsletter.dto.NewsletterResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface NewsletterService {
    ResponseEntity<NewsletterResponse> createNewsletter(NewsletterRequest request) throws JsonProcessingException;
    ResponseEntity<NewsletterResponse> getNewsletterRequestByEmail(String email);
    List<NewsletterResponse> getAllNewsletterRequest();
}
