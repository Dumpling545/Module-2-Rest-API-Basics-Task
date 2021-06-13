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
VALUES(1, 'certificate1', 'description1', 1, '2019-01-02', NOW(), 12.34);
INSERT INTO gift_certificate(id, name, description, duration, create_date, last_update_date, price)
VALUES(2, 'certificate2', 'description2', 2, '2019-01-02', NOW(), 9112.18);
INSERT INTO gift_certificate(id, name, description, duration, create_date, last_update_date, price)
VALUES(3, 'certificate3', 'description3', 3, '2019-01-03', NOW(), 784.251);
INSERT INTO gift_certificate(id, name, description, duration, create_date, last_update_date, price)
VALUES(4, 'certificate4', 'description4', 4, '2019-01-02', NOW(), 29142.091);
INSERT INTO gift_certificate(id, name, description, duration, create_date, last_update_date, price)
VALUES(5, 'certificate5', 'description5', 5, '2019-01-02', NOW(), 1.0291);
INSERT INTO gift_certificate(id, name, description, duration, create_date, last_update_date, price)
VALUES(6, 'certificate6', 'new', 6, '2019-01-02', NOW(), 1.3);
INSERT INTO gift_certificate(id, name, description, duration, create_date, last_update_date, price)
VALUES(7, 'new', 'description7', 6, '2019-01-02', NOW(), 1.6);
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

