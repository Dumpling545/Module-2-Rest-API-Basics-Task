-- tags
INSERT INTO tag(id, name) VALUES (1, 'tag1');
INSERT INTO tag(id, name) VALUES (2, 'tag2');
INSERT INTO tag(id, name) VALUES (3, 'tag3');
INSERT INTO tag(id, name) VALUES (4, 'tag4');
INSERT INTO tag(id, name) VALUES (5, 'tag5');
INSERT INTO tag(id, name) VALUES (6, 'tag6');
INSERT INTO tag(id, name) VALUES (7, 'tag7');
INSERT INTO tag(id, name) VALUES (8, 'tag8');
INSERT INTO tag(id, name) VALUES (9, 'tag9');
INSERT INTO tag(id, name) VALUES (10, 'tag10');
-- certificates
INSERT INTO gift_certificate(id, name, description, duration, create_date, last_update_date, price)
VALUES(1, 'certificate1', 'description1', 1, '2010-01-02', NOW(), 12.34);
INSERT INTO gift_certificate(id, name, description, duration, create_date, last_update_date, price)
VALUES(2, 'certificate2', 'description2', 2, '2021-01-02', NOW(), 9112.18);
INSERT INTO gift_certificate(id, name, description, duration, create_date, last_update_date, price)
VALUES(3, 'certificate3', 'description3', 3, '2011-01-03', NOW(), 784.251);
INSERT INTO gift_certificate(id, name, description, duration, create_date, last_update_date, price)
VALUES(4, 'certificate4', 'description4', 4, '2020-01-02', NOW(), 29142.091);
INSERT INTO gift_certificate(id, name, description, duration, create_date, last_update_date, price)
VALUES(5, 'certificate5', 'description5', 5, '2012-01-02', NOW(), 1.0291);
INSERT INTO gift_certificate(id, name, description, duration, create_date, last_update_date, price)
VALUES(6, 'certificate6', 'new', 6, '2019-01-02', NOW(), 1.3);
INSERT INTO gift_certificate(id, name, description, duration, create_date, last_update_date, price)
VALUES(7, 'new', 'description7', 7, '2013-01-02', NOW(), 1.6);
-- tag-certificate conjunction table
INSERT INTO tag_gift_certificate(gift_certificate_id, tag_id) VALUES (1, 8);
INSERT INTO tag_gift_certificate(gift_certificate_id, tag_id) VALUES (1, 9);
INSERT INTO tag_gift_certificate(gift_certificate_id, tag_id) VALUES (1, 10);

INSERT INTO tag_gift_certificate(gift_certificate_id, tag_id) VALUES (2, 3);
INSERT INTO tag_gift_certificate(gift_certificate_id, tag_id) VALUES (2, 6);
INSERT INTO tag_gift_certificate(gift_certificate_id, tag_id) VALUES (2, 7);

INSERT INTO tag_gift_certificate(gift_certificate_id, tag_id) VALUES (3, 3);
INSERT INTO tag_gift_certificate(gift_certificate_id, tag_id) VALUES (3, 5);
INSERT INTO tag_gift_certificate(gift_certificate_id, tag_id) VALUES (3, 8);

INSERT INTO tag_gift_certificate(gift_certificate_id, tag_id) VALUES (6, 8);
INSERT INTO tag_gift_certificate(gift_certificate_id, tag_id) VALUES (7, 8);
-- users
INSERT INTO cert_user(id, user_name, role, password, external_id, external_provider) VALUES (1, 'user1', 'USER', null, 'external_id_1', 'external_provider_1');
INSERT INTO cert_user(id, user_name, role, password, external_id, external_provider) VALUES (2, 'user2', 'USER', null, null, null);
INSERT INTO cert_user(id, user_name, role, password, external_id, external_provider) VALUES (3, 'user3', 'ADMIN', null, 'external_id_1', 'external_provider_2');
INSERT INTO cert_user(id, user_name, role, password, external_id, external_provider) VALUES (4, 'user4', 'ADMIN', null, null, null);
INSERT INTO cert_user(id, user_name, role, password, external_id, external_provider) VALUES (5, 'user5', 'USER', null, 'external_id_2', 'external_provider_2');
INSERT INTO cert_user(id, user_name, role, password, external_id, external_provider) VALUES (6, 'user6', 'USER', null, 'external_id_2', 'external_provider_1');

-- orders
INSERT INTO cert_order(id, user_id, gift_certificate_id, cost, purchase_date) VALUES (1, 1, 3, 1.1, '2018-01-01');
INSERT INTO cert_order(id, user_id, gift_certificate_id, cost, purchase_date) VALUES (2, 1, 6, 2.9, '2020-04-15');
INSERT INTO cert_order(id, user_id, gift_certificate_id, cost, purchase_date) VALUES (3, 1, 2, 1.9, NOW());

