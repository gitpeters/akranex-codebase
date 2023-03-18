package com.akraness.akranesswaitlist.identitypass.dto;

import lombok.Data;

@Data
public class NINDataDto {
    private String firstname;
    private String surname;
    private String birthcountry;
    private String birthdate;
    private String gender;
    private String nin;
    private String vnin;
}
