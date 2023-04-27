package com.akraness.akranesswaitlist.newsletter.dto;

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
public class NewsletterResponse {

    private String email;
    private String status;
    private String statusMessage;

    public NewsletterResponse (String status, String statusMessage){
        this.status = status;
        this.statusMessage = statusMessage;
    }
}
