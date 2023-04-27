package com.akraness.akranesswaitlist.contactus.entity;

import com.akraness.akranesswaitlist.entity.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name="contact_us")
public class ContactUs extends BaseEntity {
    @Column(name="full_name")
    private String fullName;
    @Column(name = "email")
    private String email;
    @Column(name = "subject")
    private String subject;
    @Column(name = "message")
    private String message;
}
