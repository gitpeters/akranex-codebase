package com.akraness.akranesswaitlist.dto;

import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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
    private String akranexTag;
    private String kycStatus;
    private String kycStatusMessage;
    private Map<String, Object> kycDataVerificationPayload;
    private List<SubAccount> subAccountList;
}
