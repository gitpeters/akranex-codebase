package com.akraness.akranesswaitlist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    @NotNull(message = "username is required.")
    private String username;
    @NotNull(message = "password is required.")
    private String password;
}
