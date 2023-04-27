create table if not exists contact_us
(
    id                          bigint primary key AUTO_INCREMENT,
    full_name           		varchar(100) NOT NULL,
    email       				varchar(100) NOT NULL,
    subject                     varchar(100) NOT NULL,
    message             		varchar(250) NOT NULL,
    created_on                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);

create table if not exists newsletter
(
    id                          bigint primary key AUTO_INCREMENT,
    email       				varchar(100) NOT NULL,
    created_on                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);