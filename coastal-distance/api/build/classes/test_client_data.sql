INSERT INTO kbs_client (name, address, city, state, zip, created) VALUES('Test Client', '111 Fake St', 'Madison', 'WI', '53704', NOW());
INSERT INTO client_contract(client_id, name, request_type_id, max_requests, cents_per_request, starts, created) VALUES (1, 'Test Contract 1', 2, 5000, 0, NOW(), NOW());
INSERT INTO client_auth(client_id, name, token, created) VALUES(1, 'Test auth key', '5pQroJPe9b', NOW());
