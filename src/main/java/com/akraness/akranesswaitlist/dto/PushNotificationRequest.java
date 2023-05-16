package com.akraness.akranesswaitlist.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushNotificationRequest {
    private String subject;
    private String message;
    private String topic;

    public PushNotificationRequest(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }
}
