package com.akraness.akranesswaitlist.identitypass.data.dto;

import lombok.Data;

@Data
public class BVNDataDto {
    private String bvn;
    private String firstName;
    private String lastName;
    private String middleName;
    private String dateOfBirth;
    private String gender;
}
