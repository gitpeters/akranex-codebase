create table if not exists user
(
    id                            bigint primary key auto_increment,
    username                      varchar(70) not null unique,
    email                         varchar(70) not null unique,
    mobile_number                 varchar(30) not null unique,
    first_name                    varchar(30) null,
    last_name                     varchar(30) null,
    country_code                  varchar(40) null,
    account_type                  varchar(40) not null,
    password                      varchar(500) null,
    login_attempts                int default 0,
    active                        TINYINT(1) DEFAULT 0,
    email_verified                TINYINT(1) DEFAULT 0,
    mobile_verified               TINYINT(1) DEFAULT 0,
    first_login                   TINYINT(1) DEFAULT 0,
    created_date                  DATETIME not null,
    last_login_date               DATETIME null,
    date_of_birth                 DATE null
)