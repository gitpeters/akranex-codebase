package com.akraness.akranesswaitlist.plaid.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentityVerificationDto {
    private String client_id;
    private String secret;
    private String identity_verification_id;
    private String template_id;
    private boolean gave_consent;
    private boolean is_shareable;
    private UserDto user;
    private AddressDto address;
    private IdNumberDto id_number;
    private String client_user_id;
    private String strategy;
}
