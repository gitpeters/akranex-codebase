package com.akraness.akranesswaitlist.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class AkranexTagCreationRequestDto {
    @NotBlank(message = "Username is required.")
    private String username;
    @NotBlank(message = "Akranex tag is required.")
    private String akranexTag;
}
