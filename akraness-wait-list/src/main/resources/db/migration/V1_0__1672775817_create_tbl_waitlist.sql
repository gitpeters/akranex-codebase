create table if not exists waitlist
(
    id                            bigint primary key auto_increment,
    email                         varchar(70) not null unique,
    created_date                  DATE not null
)