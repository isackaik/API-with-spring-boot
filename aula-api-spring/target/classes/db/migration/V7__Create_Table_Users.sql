CREATE TABLE IF NOT EXISTS users (
  id serial,
  user_name varchar(255) DEFAULT NULL,
  full_name varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  account_non_expired BOOLEAN DEFAULT false,
  account_non_locked BOOLEAN DEFAULT false,
  credentials_non_expired BOOLEAN DEFAULT false,
  enabled BOOLEAN DEFAULT false,
  CONSTRAINT pk_users PRIMARY KEY (id),
  CONSTRAINT uk_user_name UNIQUE (user_name)
);