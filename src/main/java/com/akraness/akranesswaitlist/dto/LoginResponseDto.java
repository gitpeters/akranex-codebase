package com.akraness.akranesswaitlist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String access_token;
    private String email;
    private String mobileNumber;
    private String firstName;
    private String lastName;
    private String country;
}
