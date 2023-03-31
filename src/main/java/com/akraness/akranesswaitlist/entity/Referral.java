package com.akraness.akranesswaitlist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "referral")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Referral extends BaseEntity{
    private Long referralUserId;
    private Long newUserId;
    private String referralRewardStatus;
    private double referralRewardAmount;
    private double newUserFundedAmount;
}
