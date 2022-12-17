
INSERT INTO organization
VALUES (1, 'org1', 11),
       (2, 'org2', 22),
       (3, 'org3', 33);

INSERT INTO product
VALUES (1, 'prod1'),
       (2, 'prod2'),
       (3, 'prod3');

INSERT INTO consignment_note
VALUES (1, '1970-01-01', 1),
       (2, '1970-01-02', 2),
       (3, '1970-01-03', 3);

INSERT INTO consignment_note_item (product_id, price, quantity, consignment_note_id)
VALUES (1, 1, 1, 1),
       (2, 2, 2, 1),
       (2, 4, 3, 2),
       (3, 3, 3, 2);
