package com.akraness.akranesswaitlist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="fcm_token")
public class UserFCMToken extends BaseEntity{
    @Column(name="user_id")
    private Long userId;
    @Column(name="fcm_token")
    private String fcmToken;
}
