package com.akraness.akranesswaitlist.identitypass.dto;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Lob;

@Data
@Builder
public class IdentityPassDocumentRequestPayload {
    private String doc_country;
    private String doc_type;
    private String doc_image;
}
