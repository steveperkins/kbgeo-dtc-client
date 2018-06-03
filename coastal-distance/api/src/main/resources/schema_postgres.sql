CREATE USER cd WITH PASSWORD 'cd';
CREATE DATABASE coastal_distance OWNER cd;
GRANT ALL PRIVILEGES ON DATABASE coastal_distance to cd;

DROP TABLE IF EXISTS request_type;
CREATE TABLE request_type (
  id SERIAL PRIMARY KEY,
  name varchar(255) DEFAULT NULL,
  created timestamp with time zone NOT NULL,
  updated timestamp with time zone DEFAULT NULL
);

DROP TABLE IF EXISTS kbs_client;
CREATE TABLE kbs_client (
  id SERIAL PRIMARY KEY,
  name varchar(255) DEFAULT NULL,
  address varchar(255) DEFAULT NULL,
  city varchar(30) DEFAULT NULL,
  state char(2) DEFAULT NULL,
  zip varchar(10) DEFAULT NULL,
  phone varchar(11) DEFAULT NULL,
  -- the client_id to use when querying coastline segments. If not set, 1 will be used.
  coastline_client_id INTEGER NULL,
  created timestamp with time zone NOT NULL,
  updated timestamp with time zone DEFAULT NULL
);

DROP TABLE IF EXISTS client_auth;
CREATE TABLE client_auth (
  id SERIAL PRIMARY KEY,
  client_id INTEGER NOT NULL,
  name varchar(255) DEFAULT NULL,
  token varchar(255) DEFAULT NULL,
  expires timestamp with time zone DEFAULT NULL,
  created timestamp with time zone NOT NULL,
  updated timestamp with time zone DEFAULT NULL,
  CONSTRAINT client_auth_kbs_client_id FOREIGN KEY (client_id) REFERENCES kbs_client (id)
);


DROP TABLE IF EXISTS client_auth_ip;
CREATE TABLE client_auth_ip (
  id serial PRIMARY KEY,
  client_auth_id integer NOT NULL,
  name varchar(255) DEFAULT NULL,
  ip varchar(20) DEFAULT NULL,
  created timestamp with time zone NOT NULL,
  updated timestamp with time zone DEFAULT NULL,
  CONSTRAINT client_auth_ip_client_auth_id FOREIGN KEY (client_auth_id) REFERENCES client_auth (id)
  );

DROP TABLE IF EXISTS client_contact;
CREATE TABLE client_contact (
  id SERIAL PRIMARY KEY,
  client_id INTEGER NOT NULL,
  last_name varchar(255) DEFAULT NULL,
  first_name varchar(255) DEFAULT NULL,
  email varchar(255) DEFAULT NULL,
  address varchar(255) DEFAULT NULL,
  city varchar(30) DEFAULT NULL,
  state char(2) DEFAULT NULL,
  zip varchar(10) DEFAULT NULL,
  phone varchar(11) DEFAULT NULL,
  is_primary BOOLEAN DEFAULT FALSE,
  created timestamp with time zone NOT NULL,
  updated timestamp with time zone DEFAULT NULL,
  CONSTRAINT client_contact_kbs_client_id FOREIGN KEY (client_id) REFERENCES kbs_client (id)
);

DROP TABLE IF EXISTS client_auth_web;
CREATE TABLE client_auth_web (
  id SERIAL PRIMARY KEY,
  client_auth_id INTEGER NOT NULL,
  client_contact_id INTEGER NOT NULL,
  username varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  starts timestamp with time zone DEFAULT NULL,
  expires timestamp with time zone DEFAULT NULL,
  created timestamp with time zone NOT NULL,
  updated timestamp with time zone DEFAULT NULL,
  CONSTRAINT client_auth_web_client_auth_id FOREIGN KEY (client_auth_id) REFERENCES client_auth (id),
  CONSTRAINT client_auth_web_client_contact_id FOREIGN KEY (client_contact_id) REFERENCES client_contact (id)
);

DROP TABLE IF EXISTS client_auth_referer;
CREATE TABLE client_auth_referer (
  id SERIAL PRIMARY KEY,
  client_auth_id INTEGER NOT NULL,
  name varchar(255) DEFAULT NULL,
  referers varchar(255) DEFAULT NULL,
  created timestamp with time zone NOT NULL,
  updated timestamp with time zone DEFAULT NULL,
  CONSTRAINT client_auth_referer_client_auth_id FOREIGN KEY (client_auth_id) REFERENCES client_auth (id)
);

DROP TABLE IF EXISTS client_request;
CREATE TABLE client_request (
  id SERIAL PRIMARY KEY,
  client_auth_id INTEGER DEFAULT NULL,
  source_ip varchar(20) DEFAULT NULL,
  request_url varchar(255) DEFAULT NULL,
  request_type_id INTEGER DEFAULT NULL,
  request_body text,
  response_status INTEGER DEFAULT NULL,
  response_body text,
  request_time timestamp with time zone DEFAULT NULL,
  response_time timestamp with time zone DEFAULT NULL,
  error BOOLEAN DEFAULT FALSE,
  error_message varchar(255) DEFAULT NULL,
  created timestamp with time zone NOT NULL,
  updated timestamp with time zone DEFAULT NULL,
  CONSTRAINT client_request_client_auth_id FOREIGN KEY (client_auth_id) REFERENCES client_auth (id),
  CONSTRAINT client_request_requesty_type_id FOREIGN KEY (request_type_id) REFERENCES request_type (id)
);

DROP TABLE IF EXISTS client_contract;
CREATE TABLE client_contract (
  id SERIAL PRIMARY KEY,
  client_id INTEGER NOT NULL,
  name varchar(255) DEFAULT NULL,
  request_type_id INTEGER NOT NULL,
  max_requests INTEGER NOT NULL,
  cents_per_request SMALLINT NOT NULL,
  starts timestamp with time zone DEFAULT NULL,
  expires timestamp with time zone DEFAULT NULL,
  created timestamp with time zone NOT NULL,
  updated timestamp with time zone DEFAULT NULL,
  CONSTRAINT client_contract_kbs_client_id FOREIGN KEY (client_id) REFERENCES kbs_client (id),
  CONSTRAINT client_contract_request_type_id FOREIGN KEY (request_type_id) REFERENCES request_type (id)
);

DROP TABLE IF EXISTS coastline_segment;
CREATE TABLE coastline_segment (
  id SERIAL PRIMARY KEY,
  coast varchar(20) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  sort_order DECIMAL(7,3) DEFAULT NULL
);


CREATE TABLE client_coastline_segment (
  client_id INTEGER NOT NULL,
  segment_id INTEGER NOT NULL,
  active BOOLEAN DEFAULT FALSE,
  PRIMARY KEY(client_id, segment_id),
  CONSTRAINT client_coastline_segment_client_id FOREIGN KEY (client_id) REFERENCES kbs_client (id),
  CONSTRAINT client_coastline_segment_segment_id FOREIGN KEY (segment_id) REFERENCES coastline_segment (id)
);
CREATE INDEX client_coastline_segment_client_id ON client_coastline_segment (client_id);
CREATE INDEX client_coastline_segment_active ON client_coastline_segment (active);

INSERT INTO client_coastline_segment VALUES(2, 1, true);
INSERT INTO client_coastline_segment VALUES(2, 2, true);
INSERT INTO client_coastline_segment VALUES(2, 3, true);
INSERT INTO client_coastline_segment VALUES(2, 4, true);
INSERT INTO client_coastline_segment VALUES(2, 5, true);
INSERT INTO client_coastline_segment VALUES(2, 6, true);
INSERT INTO client_coastline_segment VALUES(2, 7, true);
INSERT INTO client_coastline_segment VALUES(2, 8, true);
INSERT INTO client_coastline_segment VALUES(2, 9, true);
INSERT INTO client_coastline_segment VALUES(2, 10, true);
INSERT INTO client_coastline_segment VALUES(2, 11, true);
INSERT INTO client_coastline_segment VALUES(2, 12, true);
INSERT INTO client_coastline_segment VALUES(2, 13, true);
INSERT INTO client_coastline_segment VALUES(2, 14, true);
INSERT INTO client_coastline_segment VALUES(2, 15, true);
INSERT INTO client_coastline_segment VALUES(2, 16, true);
INSERT INTO client_coastline_segment VALUES(2, 17, true);
INSERT INTO client_coastline_segment VALUES(2, 18, true);



DROP TABLE IF EXISTS coastline_point;
CREATE TABLE coastline_point (
  id SERIAL PRIMARY KEY,
  client_id INTEGER NOT NULL,
  lat decimal(10,7) DEFAULT NULL,
  lon decimal(10,7) DEFAULT NULL,
  sort_order DECIMAL(10,3) DEFAULT NULL,
  coast varchar(20) DEFAULT NULL,
  segment_id INTEGER NOT NULL
);

CREATE INDEX idx_coastline_point_client_id_segment_id ON coastline_point(client_id, segment_id);
CREATE INDEX idx_coastline_point_client_id_sort_order ON coastline_point(client_id, sort_order);

CREATE OR REPLACE FUNCTION coastline_point_partition_trigger()
RETURNS TRIGGER AS 
$BODY$
BEGIN
  IF (TG_OP = 'INSERT') THEN
  	IF (NEW.id IS NOT NULL) THEN
  		EXECUTE 'INSERT INTO coastline_point_' || NEW.client_id || ' (id,lat, lon, client_id, sort_order, segment_id, coast) VALUES(' || NEW.id || ', '|| NEW.lat || ', ' || NEW.lon || ', ' || NEW.client_id || ', ' || NEW.sort_order || ', ' || NEW.segment_id || ', ''' || NEW.coast || ''');';
  	ELSE
    	EXECUTE 'INSERT INTO coastline_point_' || NEW.client_id || ' (lat, lon, client_id, sort_order, segment_id, coast) VALUES(' || NEW.lat || ', ' || NEW.lon || ', ' || NEW.client_id || ', ' || NEW.sort_order || ', ' || NEW.segment_id || ', ''' || NEW.coast || ''');';
    END IF;
  ELSIF (TG_OP = 'UPDATE') THEN
    EXECUTE 'UPDATE coastline_point_' || NEW.client_id || ' SET id=' || NEW.id || ', client_id=' || NEW.client_id || ', lat=' || NEW.lat || ', lon=' || NEW.lon || ', sort_order=' || NEW.sort_order || ', coast=''' || NEW.coast || ''' WHERE id=' || OLD.id || ';';
  END IF;
  RETURN NULL;
END;
$BODY$
LANGUAGE plpgsql;

DROP TRIGGER coastline_point_partition_trigger ON coastline_point;
CREATE TRIGGER coastline_point_partition_trigger BEFORE INSERT OR UPDATE
ON coastline_point
FOR EACH ROW
EXECUTE PROCEDURE coastline_point_partition_trigger();

-- Default coastline, client ID 1
CREATE TABLE coastline_point_1 (
  CONSTRAINT coastline_point_client_id_1 FOREIGN KEY (client_id) REFERENCES kbs_client (id),
  CONSTRAINT coastline_point_coastline_segment_id_1 FOREIGN KEY (segment_id) REFERENCES coastline_segment(id),
  CHECK ( client_id = 1 )
) INHERITS (coastline_point);
CREATE INDEX coastline_point_sort_order_1 ON coastline_point_1 (sort_order);
CREATE INDEX coastline_point_segment_id_1 ON coastline_point_1 (segment_id);

-- Capitol Indemnity Corp, client ID 2
CREATE TABLE coastline_point_2 (
  CONSTRAINT coastline_point_client_id_2 FOREIGN KEY (client_id) REFERENCES kbs_client (id),
  CONSTRAINT coastline_point_coastline_segment_id_2 FOREIGN KEY (segment_id) REFERENCES coastline_segment(id),
  CHECK ( client_id = 2 )
) INHERITS (coastline_point);
CREATE INDEX coastline_point_sort_order_2 ON coastline_point_2 (sort_order);
CREATE INDEX coastline_point_segment_id_2 ON coastline_point_2 (segment_id);

DROP TABLE IF EXISTS request_error;
CREATE TABLE request_error (
  id SERIAL PRIMARY KEY,
  client_id INTEGER DEFAULT NULL,
  source_ip varchar(20) DEFAULT NULL,
  request_url varchar(255) DEFAULT NULL,
  request_type_id INTEGER DEFAULT NULL,
  request_body text,
  response_status INTEGER DEFAULT NULL,
  response_body text,
  request_time timestamp with time zone DEFAULT NULL,
  response_time timestamp with time zone DEFAULT NULL,
  created timestamp with time zone NOT NULL,
  CONSTRAINT request_error_request_type_id FOREIGN KEY (request_type_id) REFERENCES request_type (id)
);

DROP TABLE IF EXISTS client_statistics;
CREATE TABLE client_statistics (
  id SERIAL PRIMARY KEY,
  client_id INTEGER NOT NULL,
  request_type_id INTEGER DEFAULT NULL,
  year SMALLINT DEFAULT NULL,
  month SMALLINT DEFAULT NULL,
  request_count INTEGER DEFAULT NULL,
  error_count INTEGER DEFAULT NULL,
  created timestamp with time zone NOT NULL,
  updated timestamp with time zone DEFAULT NULL,
  CONSTRAINT client_statistics_kbs_client_id FOREIGN KEY (client_id) REFERENCES kbs_client (id),
  CONSTRAINT client_statistics_request_type_id FOREIGN KEY (request_type_id) REFERENCES request_type (id)
);


DROP TABLE IF EXISTS grid_point;
CREATE TABLE grid_point (
  id serial NOT NULL,
  lat numeric(10,7) DEFAULT NULL,
  lon numeric(10,7) DEFAULT NULL,
  closest_grid_point_north_id integer,
  closest_grid_point_east_id integer,
  closest_grid_point_south_id integer,
  closest_grid_point_west_id integer,
  sort_order numeric(7,3),
);

-- Mapping between client coastline points and official grid points
DROP TABLE IF EXISTS grid_point_client_coastline_point;
CREATE TABLE grid_point_client_coastline_point (
  client_id INTEGER NOT NULL,
  grid_point_id INTEGER,
  coastline_point_id INTEGER,
  distance_in_miles numeric(11,6),
  PRIMARY KEY(client_id, grid_point_id)
);

DROP TABLE IF EXISTS grid_point_client_coastline_point_1;
CREATE TABLE grid_point_client_coastline_point_1 (
  CONSTRAINT grid_point_client_coastline_point_client_id_1 FOREIGN KEY (client_id) REFERENCES kbs_client (id),
  CONSTRAINT grid_point_client_coastline_point_grid_point_id_1 FOREIGN KEY (grid_point_id) REFERENCES grid_point (id),
  CONSTRAINT grid_point_client_coastline_point_coastline_point_id_1 FOREIGN KEY (coastline_point_id) REFERENCES coastline_point(id),
  CHECK ( client_id = 1 )
) INHERITS (grid_point_client_coastline_point);


CREATE OR REPLACE FUNCTION grid_point_client_coastline_point_partition_trigger()
RETURNS TRIGGER AS 
$BODY$
DECLARE 
	tableCheckResult INTEGER;
BEGIN
  IF TG_OP = 'INSERT' THEN
    EXECUTE 'SELECT 1 FROM information_schema.tables WHERE  table_schema = $1 AND table_name = $2'
	INTO tableCheckResult
	USING 'public', 'grid_point_client_coastline_point_' || NEW.client_id;
    IF tableCheckResult IS NULL OR tableCheckResult <> 1 THEN
	-- Create the table
	EXECUTE 'CREATE TABLE grid_point_client_coastline_point_' || NEW.client_id || ' ('
	  || 'CONSTRAINT grid_point_client_coastline_point_client_id_' || NEW.client_id || ' FOREIGN KEY (client_id) REFERENCES kbs_client (id),'
	  || 'CONSTRAINT grid_point_client_coastline_point_grid_point_id_' || NEW.client_id || ' FOREIGN KEY (grid_point_id) REFERENCES grid_point (id),'
	  --|| 'CONSTRAINT grid_point_client_coastline_point_coastline_point_id_' || NEW.client_id || ' FOREIGN KEY (coastline_point_id) REFERENCES coastline_point(id),'
	  || 'CHECK ( client_id = ' || NEW.client_id || ' )'
	|| ') INHERITS (grid_point_client_coastline_point);';
    END IF;
    
    EXECUTE 'INSERT INTO grid_point_client_coastline_point_' || NEW.client_id || ' (client_id, grid_point_id, coastline_point_id, distance_in_miles) VALUES($1, $2, $3, $4);' 
      USING NEW.client_id, NEW.grid_point_id, NEW.coastline_point_id, NEW.distance_in_miles;
  ELSIF TG_OP = 'UPDATE' THEN
    EXECUTE 'UPDATE grid_point_client_coastline_point_' || NEW.client_id || ' SET client_id=' || NEW.client_id || ', grid_point_id=' || NEW.grid_point_id || ', coastline_point_id=' || NEW.coastline_point_id || ', distance_in_miles=' || NEW.distance_in_miles || ' WHERE client_id=' || OLD.client_id || ' AND grid_point_id=' || OLD.grid_point_id || ' AND coastline_point_id=' || OLD.coastline_point_id || ';';
  END IF;
  RETURN NULL;
END;
$BODY$
LANGUAGE plpgsql;

DROP TRIGGER grid_point_client_coastline_point_partition_trigger ON grid_point_client_coastline_point;
CREATE TRIGGER grid_point_client_coastline_point_partition_trigger BEFORE INSERT OR UPDATE
ON grid_point_client_coastline_point
FOR EACH ROW
EXECUTE PROCEDURE grid_point_client_coastline_point_partition_trigger();


-- DON'T FORGET TO TURN ON CONSTRAINT EXCLUSION!!!
SET constraint_exclusion = PARTITION;

-- Reset the autoincrement for tables with SERIAL id columns, as postgres doesn't handle that automatically on import
SELECT pg_catalog.setval(pg_get_serial_sequence('client_request', 'id'), (SELECT MAX(id) FROM client_request)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('client_auth', 'id'), (SELECT MAX(id) FROM client_auth)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('client_auth_ip', 'id'), (SELECT MAX(id) FROM client_auth_ip)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('client_auth_referer', 'id'), (SELECT MAX(id) FROM client_auth_referer)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('client_auth_web', 'id'), (SELECT MAX(id) FROM client_auth_web)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('client_contact', 'id'), (SELECT MAX(id) FROM client_contact)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('client_contract', 'id'), (SELECT MAX(id) FROM client_contract)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('client_statistics', 'id'), (SELECT MAX(id) FROM client_statistics)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('coastline_point', 'id'), (SELECT MAX(id) FROM coastline_point)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('coastline_segment', 'id'), (SELECT MAX(id) FROM coastline_segment)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('grid_point', 'id'), (SELECT MAX(id) FROM grid_point)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('kbs_client', 'id'), (SELECT MAX(id) FROM kbs_client)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('request_error', 'id'), (SELECT MAX(id) FROM request_error)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('request_type', 'id'), (SELECT MAX(id) FROM request_type)+1);


-- If you don't make the web user the owner of parent tables, Postgres shits itself when a trigger tries to create a new child table
alter database coastal_distance owner to cd;
alter table client_coastline_segment owner to cd;
alter table coastline_point owner to cd;
alter table grid_point owner to cd;
alter table grid_point_client_coastline_point owner to cd;

GRANT ALL ON TABLE client_auth TO public;
GRANT ALL ON TABLE client_auth_ip TO public;
GRANT ALL ON TABLE client_auth_referer TO public;
GRANT ALL ON TABLE client_auth_web TO public;
GRANT ALL ON TABLE client_coastline_segment TO public;
GRANT ALL ON TABLE client_contact TO public;
GRANT ALL ON TABLE client_contract TO public;
GRANT ALL ON TABLE client_request TO public;
GRANT ALL ON TABLE client_statistics TO public;
GRANT ALL ON TABLE coastline_point TO public;
GRANT ALL ON TABLE coastline_point_1 TO public;
GRANT ALL ON TABLE coastline_point_2 TO public;
GRANT ALL ON TABLE coastline_point_2_orig TO public;
GRANT ALL ON TABLE coastline_segment TO public;
GRANT ALL ON TABLE grid_point TO public;
GRANT ALL ON TABLE grid_point_client_coastline_point TO public;
GRANT ALL ON TABLE grid_point_client_coastline_point_1 TO public;
GRANT ALL ON TABLE kbs_client TO public;
GRANT ALL ON TABLE request_error TO public;
GRANT ALL ON TABLE request_type TO public;


SELECT cp.* from coastline_point cp LEFT JOIN client_coastline_segment ccs ON cp.segment_id=ccs.segment_id WHERE ((cp.client_id=1 AND ccs.segment_id IS NULL) OR (cp.client_id=2 AND ccs.segment_id IS NOT NULL)) and lat=42.0427220 and lon=-124.2855560;








-- to re-upload table data to production:
TRUNCATE grid_point_client_coastline_point_2, grid_point_client_coastline_point_1, grid_point;
SELECT pg_catalog.setval(pg_get_serial_sequence('grid_point', 'id'), (SELECT MAX(id) FROM grid_point)+1);

TRUNCATE coastline_segment, client_coastline_segment, coastline_point, coastline_point_2, coastline_point_1;
SELECT pg_catalog.setval(pg_get_serial_sequence('coastline_segment', 'id'), (SELECT MAX(id) FROM coastline_segment)+1);
SELECT pg_catalog.setval(pg_get_serial_sequence('coastline_point', 'id'), (SELECT MAX(id) FROM coastline_point)+1);



