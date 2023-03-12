create table if not exists sub_account
(
    id                            bigint primary key auto_increment,
    sub_account_id                varchar(100) not null unique,
    uid                           varchar(100) not null unique,
    user_id                       bigint not null unique,
    created_on                    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    )