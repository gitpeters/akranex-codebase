package com.akraness.akranesswaitlist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("waitlist")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaitList {
    @Id
    private Long id;
    private String email;
    private LocalDate createdDate;
}
