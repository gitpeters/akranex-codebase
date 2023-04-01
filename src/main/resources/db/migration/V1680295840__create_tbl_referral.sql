create table if not exists referral
(
    id                          bigint primary key AUTO_INCREMENT,
    referral_user_id            bigint not null,
    new_user_id                 bigint not null,
    referral_reward_amount      double NOT NULL,
    new_user_founded_amount     double NOT NULL,
    referral_reward_status      varchar(70) NULL,
    created_on                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);