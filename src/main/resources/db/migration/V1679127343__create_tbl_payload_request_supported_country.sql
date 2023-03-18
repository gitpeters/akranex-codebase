create table if not exists payload_verification_country
(
    country_code           varchar(100) primary key,
    payload                JSON,
    created_at             DATETIME NOT NULL DEFAULT NOW()
)