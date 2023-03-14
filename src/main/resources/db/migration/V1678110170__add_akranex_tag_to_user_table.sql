alter table user
add column akranex_tag varchar(50) NULL;

CREATE UNIQUE INDEX index_akranex_tag
    ON user (akranex_tag);