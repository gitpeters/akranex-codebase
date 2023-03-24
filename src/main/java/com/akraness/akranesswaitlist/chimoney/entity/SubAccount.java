package com.akraness.akranesswaitlist.chimoney.entity;

import com.akraness.akranesswaitlist.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "sub_account")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubAccount extends BaseEntity {
    private String subAccountId;
    private String uid;
    private Integer userId;
    private String countryCode;
}
