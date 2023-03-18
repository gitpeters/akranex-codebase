package com.akraness.akranesswaitlist.identitypass.dto;

import lombok.Data;

@Data
public class DLDataDto {
    private boolean verified = false;
    private String message;
}
