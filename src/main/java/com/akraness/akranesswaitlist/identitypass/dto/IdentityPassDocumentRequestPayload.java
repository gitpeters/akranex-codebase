package com.akraness.akranesswaitlist.identitypass.dto;

import lombok.Data;

@Data
public class IdentityPassDocumentRequestPayload {
    private String doc_country;
    private String doc_type;
    private String doc_image;
}
