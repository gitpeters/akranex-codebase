create table if not exists waitlist
(
    id                            bigint primary key auto_increment,
    email                         varchar(70) not null unique,
    created_date                  DATE not null
);


create table if not exists users
(
    id                           bigint primary key auto_increment,
    firstname                    varchar(100),
    lastname                     varchar(100),
    gender                       varchar(20),
    dateOfBirth                  DATE not null,
    email                        varchar(70) not null unique,
    phoneNumber                  varchar(70) not null unique,
    password                     varchar(255),
    emailVerificationCode        varchar(40),
    otp                          varchar(40),
    locked                       TINYINT(1) NOT NULL DEFAULT 0,
    enabled                     TINYINT(1) NOT NULL DEFAULT 0,
    phoneNumberVerified         TINYINT(1) NOT NULL DEFAULT 0,
    loginAttempts               TINYINT(1) NOT NULL DEFAULT 0
);
