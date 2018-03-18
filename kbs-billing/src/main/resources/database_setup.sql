-- in coastal_distance
CREATE USER 'billing'@'localhost' IDENTIFIED BY '5ecUreb1lling';
GRANT SELECT ON coastal_distance.client_request TO 'billing'@'localhost';
GRANT SELECT ON coastal_distance.client_auth TO 'billing'@'localhost';
GRANT SELECT ON coastal_distance.kbs_client TO 'billing'@'localhost';