package com.akraness.akranesswaitlist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Table(name = "waitlist")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaitList extends BaseEntity {
    private String email;
    private LocalDate createdDate;
}
