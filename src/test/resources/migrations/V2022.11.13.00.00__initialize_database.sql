
CREATE TABLE organization
(
    taxpayer_identification_number INT     NOT NULL UNIQUE,
    name                           VARCHAR NOT NULL,
    payment_account                INT     NOT NULL,
    CONSTRAINT organization_pk PRIMARY KEY (taxpayer_identification_number)
);

CREATE TABLE product
(
    code INT     NOT NULL UNIQUE,
    name VARCHAR NOT NULL,
    CONSTRAINT product_pk PRIMARY KEY (code)
);

CREATE TABLE consignment_note
(
    number          INT  NOT NULL UNIQUE,
    datetime        DATE NOT NULL,
    organization_id INT  NOT NULL REFERENCES organization (taxpayer_identification_number) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT consignment_note_pk PRIMARY KEY (number)
);

CREATE TABLE consignment_note_item
(
    id                  SERIAL NOT NULL,
    product_id          INT    NOT NULL REFERENCES product (code) ON UPDATE CASCADE ON DELETE CASCADE,
    price               INT    NOT NULL,
    quantity            INT    NOT NULL,
    consignment_note_id INT    NOT NULL REFERENCES consignment_note (number) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT consignment_note_item_pk PRIMARY KEY (id)
);
