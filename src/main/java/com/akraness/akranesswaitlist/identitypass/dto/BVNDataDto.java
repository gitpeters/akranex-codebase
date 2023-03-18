package com.akraness.akranesswaitlist.identitypass.dto;

import lombok.Data;

@Data
public class BVNDataDto {
    private String bvn;
    private String firstName;
    private String lastName;
    private String middleName;
    private String dateOfBirth;
    private String gender;
    private String nationality;
    private String phoneNumber;
    private String stateOfOrigin;
    private String stateOfResidence;
    private String residentialAddress;
}
