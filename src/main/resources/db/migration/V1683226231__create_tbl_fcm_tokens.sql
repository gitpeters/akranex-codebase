

create table if not exists fcm_token
(
    id                          bigint primary key AUTO_INCREMENT,
    user_id       				bigint not null unique,
    fcm_token                   varchar(100) NOT NULL,
    created_on                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);