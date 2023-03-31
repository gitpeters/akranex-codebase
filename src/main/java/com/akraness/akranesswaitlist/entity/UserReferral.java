package com.akraness.akranesswaitlist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserReferral extends BaseEntity{
    private Long referral_user_id;

    @NotBlank(message = "new_user_id is required.")
    private Long new_user_id;

    @NotBlank(message = "referred_reward_status is required.")
    private String referred_reward_status;

    @NotBlank(message = "new_user_funded_amount is required.")
    private double new_user_funded_amount;
}
