-- V1__initial_schema.sql
-- Backbar V1: venue, department, app_user, product.
--
-- Two rules from the build spec are enforced structurally here, not by convention:
--   1. Every table except venue carries venue_id NOT NULL. venue IS the tenant.
--   2. product stores the three-level unit model. No bare "quantity" column exists
--      anywhere in this schema, and none ever will.


-- ---------------------------------------------------------------------------
-- venue — the tenant root
-- ---------------------------------------------------------------------------
CREATE TABLE venue (
                       id          bigint       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       name        varchar(120) NOT NULL,
                       created_at  timestamptz  NOT NULL DEFAULT now()
);


-- ---------------------------------------------------------------------------
-- department — Bar, Kitchen. Scoped to a venue.
-- ---------------------------------------------------------------------------
CREATE TABLE department (
                            id          bigint       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            venue_id    bigint       NOT NULL REFERENCES venue(id),
                            name        varchar(60)  NOT NULL,
                            created_at  timestamptz  NOT NULL DEFAULT now(),

    -- Two venues may each have a "Bar". One venue may not have two.
                            CONSTRAINT uq_department_venue_name UNIQUE (venue_id, name)
);


-- ---------------------------------------------------------------------------
-- app_user — "user" is a reserved word in Postgres, hence app_user
-- ---------------------------------------------------------------------------
CREATE TABLE app_user (
                          id             bigint       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          venue_id       bigint       NOT NULL REFERENCES venue(id),
                          email          varchar(255) NOT NULL,
                          password_hash  varchar(72)  NOT NULL,  -- BCrypt output is exactly 60 chars
                          role           varchar(20)  NOT NULL,
                          created_at     timestamptz  NOT NULL DEFAULT now(),

    -- DECISION 3: email is unique globally, not per venue. See notes below.
                          CONSTRAINT uq_app_user_email UNIQUE (email),

    -- DECISION 4: role as varchar + CHECK, not a native Postgres enum.
                          CONSTRAINT ck_app_user_role CHECK (role IN ('STAFF', 'MANAGER', 'BUYER', 'ADMIN'))
);

-- The only explicit index in this file. See the index note below for why the
-- other two tables don't need one.
CREATE INDEX idx_app_user_venue ON app_user (venue_id);


-- ---------------------------------------------------------------------------
-- product — the three-level unit model lives here
--
--   purchase_unit  →  each_unit  →  base_unit
--   "case"         →  "bottle"   →  "ml"
--    12 each/case      750 ml/bottle
--
--   9,000 ml per case. That number is DERIVED at read time, never stored.
-- ---------------------------------------------------------------------------
CREATE TABLE product (
                         id                  bigint        GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         venue_id            bigint        NOT NULL REFERENCES venue(id),
                         name                varchar(160)  NOT NULL,
                         category            varchar(60),             -- nullable: Spirits, Beer, Produce
                         supplier            varchar(160),            -- TEXT, not an FK. V1 guardrail.

                         purchase_unit_name  varchar(40)   NOT NULL,  -- "case"
                         each_per_purchase   integer       NOT NULL,  -- 12
                         each_unit_name      varchar(40)   NOT NULL,  -- "bottle"
                         base_per_each       numeric(12,3) NOT NULL,  -- 750.000 — numeric, not integer
                         base_unit_name      varchar(20)   NOT NULL,  -- "ml"

                         created_at          timestamptz   NOT NULL DEFAULT now(),

                         CONSTRAINT uq_product_venue_name UNIQUE (venue_id, name),
                         CONSTRAINT ck_product_each_per_purchase CHECK (each_per_purchase > 0),
                         CONSTRAINT ck_product_base_per_each     CHECK (base_per_each > 0)
);