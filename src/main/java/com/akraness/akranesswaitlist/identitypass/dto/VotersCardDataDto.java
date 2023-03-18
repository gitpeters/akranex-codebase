package com.akraness.akranesswaitlist.identitypass.dto;

import lombok.Data;

@Data
public class VotersCardDataDto {
    private String fullName;
    private String vin;
    private String gender;
    private String state;
    private String lga;
    private String date_of_birth;
    private String first_name;
    private String last_name;
}
