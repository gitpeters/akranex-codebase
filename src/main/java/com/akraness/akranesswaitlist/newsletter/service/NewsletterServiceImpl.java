package com.akraness.akranesswaitlist.newsletter.service;

import com.akraness.akranesswaitlist.dto.NotificationDto;
import com.akraness.akranesswaitlist.enums.NotificationType;
import com.akraness.akranesswaitlist.newsletter.dto.NewsletterRequest;
import com.akraness.akranesswaitlist.newsletter.dto.NewsletterResponse;
import com.akraness.akranesswaitlist.newsletter.entity.Newsletter;
import com.akraness.akranesswaitlist.newsletter.repository.NewsletterRepository;
import com.akraness.akranesswaitlist.service.INotificationService;
import com.akraness.akranesswaitlist.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsletterServiceImpl implements NewsletterService{

    private final NewsletterRepository repository;
    private final Utility utility;
    private final INotificationService notificationService;

    @Value("${regex.email}")
    private String emailRegexPattern;

    @Override
    public ResponseEntity<NewsletterResponse> createNewsletter(NewsletterRequest request) throws JsonProcessingException {
        if(utility.isNullOrEmpty(request.getEmail())){
            return ResponseEntity.badRequest().body(new NewsletterResponse(String.valueOf(HttpStatus.BAD_REQUEST), "Email is Required"));
        }
        if(!utility.isInputValid(request.getEmail(),emailRegexPattern)){
            return ResponseEntity.badRequest().body(new NewsletterResponse(String.valueOf(HttpStatus.BAD_REQUEST), "Provide valid email address"));
        }

        Newsletter newsletter = Newsletter.builder()
                .email(request.getEmail())
                .build();
        repository.save(newsletter);

        NotificationDto notificationDto = NotificationDto.builder()
                .message("Thank you for subscribing to our newsletter")
                .recipient(request.getEmail())
                .subject("Newsletter Subscription")
                .type(NotificationType.EMAIL)
                .build();

        notificationService.sendNotification(notificationDto);

        return ResponseEntity.ok().body(new NewsletterResponse(String.valueOf(HttpStatus.CREATED), "You've subscribed for our Newsletter."));
    }

    @Override
    public ResponseEntity<NewsletterResponse> getNewsletterRequestByEmail(String email) {
        Optional<Newsletter> newsletterOptional = repository.getNewsletterByEmail(email);
        if(!newsletterOptional.isPresent()){
            return ResponseEntity.badRequest().body(new NewsletterResponse(String.valueOf(HttpStatus.BAD_REQUEST), "No Newsletter request with this email found"));
        }
        Newsletter newsletter = newsletterOptional.get();

        return ResponseEntity.ok().body(
                NewsletterResponse.builder()
                        .email(newsletter.getEmail())
                .build()
        );
    }

    @Override
    public List<NewsletterResponse> getAllNewsletterRequest() {
        List<Newsletter> newsletterList = repository.findAll();
        return newsletterList.stream().map(this::mapToNewsletterResponse).collect(Collectors.toList());
    }

    private NewsletterResponse mapToNewsletterResponse(Newsletter newsletter){
        return NewsletterResponse.builder()
                .email(newsletter.getEmail())
                .build();
    }
}
