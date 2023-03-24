package com.akraness.akranesswaitlist.identitypass.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_supported_country")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataSupportedCountry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String countryCode;
    private String payload;
}
