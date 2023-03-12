package com.akraness.akranesswaitlist.plaid.dto;

import lombok.Data;

@Data
public class AddressDto {
    private String street;
    private String street2;
    private String city;
    private String region;
    private String postal_code;
    private String country;
}
