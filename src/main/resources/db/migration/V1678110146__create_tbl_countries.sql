create table if not exists countries
(
    id                            bigint primary key auto_increment,
    code                          varchar(50) not null,
    name                          varchar(100) not null,
    phone_code                    varchar(50) not null
)