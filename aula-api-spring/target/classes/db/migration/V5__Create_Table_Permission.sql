CREATE TABLE IF NOT EXISTS permission (
  id serial,
  description varchar(255) DEFAULT NULL,
  CONSTRAINT pk_permission PRIMARY KEY (id)
);