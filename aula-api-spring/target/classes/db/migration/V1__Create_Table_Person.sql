CREATE TABLE IF NOT EXISTS "public"."person" (
  "id" serial,
  "first_name" varchar(80) not null,
  "last_name" varchar(100) not null,
  "gender" varchar(6) not null,
  "address" varchar(255),
  CONSTRAINT "pk_person" PRIMARY KEY ("id")
);
