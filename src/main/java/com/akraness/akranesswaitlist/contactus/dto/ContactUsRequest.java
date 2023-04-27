package com.akraness.akranesswaitlist.contactus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactUsRequest {
    private String fullName;
    private String email;
    private String subject;
    private String message;
}
