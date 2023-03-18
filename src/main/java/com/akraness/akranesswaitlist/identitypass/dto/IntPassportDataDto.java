package com.akraness.akranesswaitlist.identitypass.dto;

import lombok.Data;

@Data
public class IntPassportDataDto {
    private String first_name;
    private String last_name;
    private String middle_name;
    private String dob;
    private String mobile;
    private String gender;
    private String issued_at;
    private String issued_date;
    private String expiry_date;
    private String number;
}
