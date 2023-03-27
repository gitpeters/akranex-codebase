DELETE FROM sub_account;

ALTER TABLE sub_account DROP COLUMN user_id;

ALTER TABLE sub_account ADD COLUMN user_id BIGINT NOT NULL;