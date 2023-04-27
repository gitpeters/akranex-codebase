package com.akraness.akranesswaitlist.newsletter.entity;

import com.akraness.akranesswaitlist.entity.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name="newsletter")
public class Newsletter extends BaseEntity {
    @Column(name="email")
    private String email;
}
