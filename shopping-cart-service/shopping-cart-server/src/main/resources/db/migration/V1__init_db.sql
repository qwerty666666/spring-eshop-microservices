CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START 1;

CREATE TABLE IF NOT EXISTS carts
(
    id          bigint                 NOT NULL,
    customer_id character varying(255) NOT NULL,
    CONSTRAINT carts_pkey PRIMARY KEY (id),
    CONSTRAINT carts_uniq_customer_id UNIQUE (customer_id)
);

CREATE TABLE IF NOT EXISTS cart_items
(
    id           bigint                      NOT NULL,
    create_time  timestamp without time zone NOT NULL,
    ean          character varying(255)      NOT NULL,
    price        numeric(19, 2)              NOT NULL,
    currency     character varying(255)      NOT NULL,
    quantity     integer                     NOT NULL,
    cart_id      bigint                      NOT NULL,
    CONSTRAINT cart_items_pkey PRIMARY KEY (id),
    CONSTRAINT cart_items_uniq_item UNIQUE (cart_id, ean),
    CONSTRAINT cart_items_cart_fk FOREIGN KEY (cart_id) REFERENCES carts (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT cart_items_positive_qty CHECK (quantity > 0)
);
CREATE INDEX IF NOT EXISTS cart_items_cart_id_idx ON cart_items (cart_id);
