DROP TABLE IF EXISTS grid_point;
CREATE TABLE grid_point (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	lat DECIMAL(9,6),
	lon DECIMAL(9,6),
	distance_in_miles DECIMAL(11,6),
	closest_coastline_point_id BIGINT,
	INDEX idx_distance_grid_lat_lon (lat, lon),
	INDEX idx_distance_grid_coastline_point_id(closest_coastline_point_id)
);

DROP TABLE IF EXISTS grid_point_128;
CREATE TABLE grid_point_128 (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	lat DECIMAL(9,6),
	lon DECIMAL(9,6),
	distance_in_miles DECIMAL(11,6),
	coastline_point_id BIGINT,
	INDEX idx_grid_point_128_lat_lon (lat, lon),
	INDEX idx_grid_point_128_coastline_point_id(coastline_point_id)
);

DROP TABLE IF EXISTS grid_point_64;
CREATE TABLE grid_point_64 (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	lat DECIMAL(9,6),
	lon DECIMAL(9,6),
	distance_in_miles DECIMAL(11,6),
	coastline_point_id BIGINT,
	INDEX idx_grid_point_64_lat_lon (lat, lon),
	INDEX idx_grid_point_64_coastline_point_id(coastline_point_id)
);

DROP TABLE IF EXISTS grid_point_32;
CREATE TABLE grid_point_32 (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	lat DECIMAL(9,6),
	lon DECIMAL(9,6),
	distance_in_miles DECIMAL(11,6),
	coastline_point_id BIGINT,
	INDEX idx_grid_point_32_lat_lon (lat, lon),
	INDEX idx_grid_point_32_coastline_point_id(coastline_point_id)
);

DROP TABLE IF EXISTS grid_point_16;
CREATE TABLE grid_point_16 (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	lat DECIMAL(9,6),
	lon DECIMAL(9,6),
	distance_in_miles DECIMAL(11,6),
	coastline_point_id BIGINT,
	INDEX idx_grid_point_16_lat_lon (lat, lon),
	INDEX idx_grid_point_16_coastline_point_id(coastline_point_id)
);

DROP TABLE IF EXISTS grid_point_8;
CREATE TABLE grid_point_8 (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	lat DECIMAL(9,6),
	lon DECIMAL(9,6),
	distance_in_miles DECIMAL(11,6),
	coastline_point_id BIGINT,
	INDEX idx_grid_point_8_lat_lon (lat, lon),
	INDEX idx_grid_point_8_coastline_point_id(coastline_point_id)
);

-- Maybe no longer needed
-- DROP TABLE IF EXISTS distance_grid;
-- CREATE TABLE distance_grid (
-- 	id BIGINT PRIMARY KEY AUTO_INCREMENT,
-- 	lat DECIMAL(9,6),
-- 	lon DECIMAL(9,6),
-- 	distance_miles DECIMAL(9,5),
-- 	closest_coastline_point_id BIGINT,
-- 	INDEX idx_distance_grid_lat_lon (lat, lon),
-- 	INDEX idx_distance_grid_coastline_point_id(closest_coastline_point_id)
-- );

DROP TABLE IF EXISTS coastline_point;
CREATE TABLE coastline_point (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	lat DECIMAL(9,6),
	lon DECIMAL(9,6)
	sort_order BIGINT,
	INDEX idx_coastline_point_sort_order (sort_order)
);

DROP TABLE IF EXISTS client_statistics;
DROP TABLE IF EXISTS client_auth_ip;
DROP TABLE IF EXISTS client_auth_referer;
DROP TABLE IF EXISTS client_auth_web;
DROP TABLE IF EXISTS client_contract;
DROP TABLE IF EXISTS client_request;
DROP TABLE IF EXISTS request_error;
DROP TABLE IF EXISTS client_auth;
DROP TABLE IF EXISTS request_type;
DROP TABLE IF EXISTS client_contact;
DROP TABLE IF EXISTS kbs_client;

CREATE TABLE kbs_client (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255),
	address VARCHAR(255),
	city VARCHAR(30),
	state CHAR(2),
	zip VARCHAR(10),
	phone VARCHAR(11),
	created DATETIME NOT NULL,
	updated DATETIME,
	INDEX idx_client_id (id)
);

CREATE TABLE client_contact (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	client_id BIGINT NOT NULL,
	last_name VARCHAR(255),
	first_name VARCHAR(255),
	email VARCHAR(255),
	address VARCHAR(255),
	city VARCHAR(30),
	state CHAR(2),
	zip VARCHAR(10),
	phone VARCHAR(11),
	is_primary TINYINT(1),
	created DATETIME NOT NULL,
	updated DATETIME,
	INDEX idx_client_contact_client_id (client_id),
	FOREIGN KEY (client_id) REFERENCES kbs_client(id)
);

CREATE TABLE client_auth (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	client_id BIGINT NOT NULL,
	name VARCHAR(255),
	token VARCHAR(255),
	expires DATETIME,
	created DATETIME NOT NULL,
	updated DATETIME,
	INDEX idx_client_auth_client_id (client_id),
	FOREIGN KEY (client_id) REFERENCES kbs_client(id)
);

CREATE TABLE client_auth_ip (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	client_auth_id BIGINT NOT NULL,
	name VARCHAR(255),
	ip VARCHAR(20),
	created DATETIME NOT NULL,
	updated DATETIME,
	INDEX idx_client_auth_ip_client_auth_id (client_auth_id),
	FOREIGN KEY (client_auth_id) REFERENCES client_auth(id)
);

CREATE TABLE client_auth_referer (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	client_auth_id BIGINT NOT NULL,
	name VARCHAR(255),
	referers VARCHAR(255),
	created DATETIME NOT NULL,
	updated DATETIME,
	INDEX idx_client_auth_referer_client_auth_id (client_auth_id),
	FOREIGN KEY (client_auth_id) REFERENCES client_auth(id)
);

CREATE TABLE client_auth_web (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	client_auth_id BIGINT NOT NULL,
	client_contact_id BIGINT NOT NULL,
	username VARCHAR(255),
	password VARCHAR(255),
	starts DATETIME,
	expires DATETIME,
	created DATETIME NOT NULL,
	updated DATETIME,
	INDEX idx_client_auth_web_client_auth_id (client_auth_id),
	FOREIGN KEY (client_auth_id) REFERENCES client_auth(id),
	FOREIGN KEY (client_contact_id) REFERENCES client_contact(id)
);

CREATE TABLE request_type (
	id INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255),
	created DATETIME NOT NULL,
	updated DATETIME,
	INDEX idx_request_type_id (id)
);

CREATE TABLE client_contract (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	client_id BIGINT NOT NULL,
	name VARCHAR(255),
	request_type_id INT NOT NULL,
	max_requests BIGINT NOT NULL,
	cents_per_request INT NOT NULL,
	starts DATETIME,
	expires DATETIME,
	created DATETIME NOT NULL,
	updated DATETIME,
	INDEX idx_client_contract_client_id (client_id),
	FOREIGN KEY (client_id) REFERENCES kbs_client(id),
	FOREIGN KEY (request_type_id) REFERENCES request_type(id)
);

CREATE TABLE client_request (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	client_auth_id BIGINT,
	source_ip VARCHAR(20),
	request_url VARCHAR(255),
	request_type_id INT,
	request_body TEXT,
	response_status INT,
	response_body TEXT,
	request_time DATETIME,
	response_time DATETIME,
	error BIT(1),
	error_message VARCHAR(255),
	created DATETIME NOT NULL,
	updated DATETIME,
	INDEX idx_client_request_client_auth_id (id),
	FOREIGN KEY (client_auth_id) REFERENCES client_auth(id),
	FOREIGN KEY (request_type_id) REFERENCES request_type(id)
);

CREATE TABLE request_error (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	client_id BIGINT,
	source_ip VARCHAR(20),
	request_url VARCHAR(255),
	request_type_id INT,
	request_body TEXT,
	response_status INT,
	response_body TEXT,
	request_time DATETIME,
	response_time DATETIME,
	created DATETIME NOT NULL,
	INDEX idx_client_request_client_id (client_id),
	FOREIGN KEY (request_type_id) REFERENCES request_type(id)
);

-- Client dashboard summary data
CREATE TABLE client_statistics (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	client_id BIGINT NOT NULL,
	request_type_id INT,
	year INT,
	month INT,
	request_count BIGINT,
	error_count BIGINT,
	created DATETIME NOT NULL,
	updated DATETIME,
	INDEX idx_client_statistics_client_id (client_id),
	FOREIGN KEY (client_id) REFERENCES kbs_client(id),
	FOREIGN KEY (request_type_id) REFERENCES request_type(id)
); 

-- Prep request types - this is the only static billing data
INSERT INTO request_type (name, created) VALUES('ERROR', NOW());
INSERT INTO request_type (name, created) VALUES('DISTANCE_TO_COAST', NOW());
INSERT INTO request_type (name, created) VALUES('CONSOLE', NOW());
