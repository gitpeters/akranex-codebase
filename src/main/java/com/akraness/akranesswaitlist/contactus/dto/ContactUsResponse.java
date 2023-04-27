package com.akraness.akranesswaitlist.contactus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ContactUsResponse {
    private String fullName;
    private String email;
    private String subject;
    private String message;
    private String status;
    private String statusMessage;

    public ContactUsResponse(String status, String statusMessage) {
        this.status = status;
        this.statusMessage = statusMessage;
    }
}
