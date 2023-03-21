create table if not exists data_supported_country
(
    id                  bigint primary key auto_increment,
    country_code        varchar(100),
    payload             TEXT
    );