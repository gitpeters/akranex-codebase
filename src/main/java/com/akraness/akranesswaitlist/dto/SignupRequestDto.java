package com.akraness.akranesswaitlist.dto;

import com.akraness.akranesswaitlist.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class SignupRequestDto {
    @NotNull(message = "email is compulsory")
    private String email;
    @NotNull(message = "password is compulsory")
    private String password;
    private String firstName;
    @NotNull(message = "lastname is compulsory")
    private String lastName;
    private String mobileNumber;
    @NotNull(message = "country code is compulsory")
    private String countryCode;
    @NotNull(message = "account type is compulsory")
    private AccountType accountType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    @NotNull(message = "gender is compulsory")
    private String gender;
}
