package com.akraness.akranesswaitlist.plaid.dto;

import lombok.Data;

@Data
public class UserDto {
    private String client_user_id;
    private String email_address;
    private String phone_number;
    private String date_of_birth;
    private NameDto name;
}
