package com.akraness.akranesswaitlist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String access_token;
    private long id;
    private String email;
    private String mobileNumber;
    private String firstName;
    private String lastName;
    private String country;
    private String dateOfBirth;
    private String gender;
    private boolean emailVerified;
    private boolean mobileVerified;
}
