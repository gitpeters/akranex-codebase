package com.akraness.akranesswaitlist.identitypass.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "payload_verification_country")
public class IdentityPassRequest {
    @Id
    @Column(name = "country_code")
    private String countryCode;

    @Column(columnDefinition = "JSON", nullable = false, name = "payload")
    private String payload;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
