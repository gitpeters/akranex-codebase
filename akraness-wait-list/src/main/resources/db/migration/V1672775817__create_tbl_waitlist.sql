create table if not exists waitlist
(
    id                          bigint primary key auto_increment,
    email                       varchar(70) not null unique,
    created_on                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)