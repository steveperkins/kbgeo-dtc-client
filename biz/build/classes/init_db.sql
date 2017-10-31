CREATE DATABASE kbgeo_biz;
\connect kgbeo_biz;

DROP TABLE IF EXISTS contact_us;
CREATE TABLE contact_us (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  phone VARCHAR(255) NULL,
  company VARCHAR(255) NULL,
  message TEXT NOT NULL,
  created_date TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_date TIMESTAMP NULL,
  viewed CHAR(1) NULL DEFAULT 'N',
  assigned CHAR(1) NULL DEFAULT 'N',
  answered CHAR(1) NULL DEFAULT 'N'
);

CREATE USER biz WITH PASSWORD 'biz';
GRANT USAGE ON SEQUENCE contact_us_id_seq TO biz;
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLE contact_us TO biz;