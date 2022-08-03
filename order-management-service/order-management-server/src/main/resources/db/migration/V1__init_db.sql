CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START 1;

DO $$ BEGIN
    CREATE TYPE order_statuses AS ENUM ('PENDING');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS orders
(
    id                      uuid                        NOT NULL,
    creation_date           timestamp without time zone NOT NULL,
    customer_id             character varying(255)      NOT NULL,
    building                character varying(255)      NOT NULL,
    city                    character varying(255)      NOT NULL,
    country                 character varying(255)      NOT NULL,
    flat                    character varying(255),
    fullname                character varying(255)      NOT NULL,
    phone                   character varying(255),
    street                  character varying(255),
    delivery_id             character varying(255)      NOT NULL,
    delivery_name           character varying(255)      NOT NULL,
    delivery_price          numeric(19, 2)              NOT NULL,
    delivery_price_currency character varying(255)      NOT NULL,
    payment_id              character varying(255)      NOT NULL,
    payment_name            character varying(255)      NOT NULL,
    status                  order_statuses              NOT NULL,
    CONSTRAINT orders_pk PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS orders_customer_id_idx ON orders (customer_id);;

CREATE TABLE IF NOT EXISTS order_lines
(
    id                  bigint                 NOT NULL,
    ean                 character varying(255) NOT NULL,
    item_price          numeric(19, 2)         NOT NULL,
    item_price_currency character varying(255) NOT NULL,
    product_name        character varying(255) NOT NULL,
    quantity            integer                NOT NULL,
    order_id            uuid                   NOT NULL,
    sort                integer,
    CONSTRAINT order_lines_pkey PRIMARY KEY (id),
    CONSTRAINT order_lines_order_fk FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT order_lines_positive_qty CHECK (quantity > 0)
);
CREATE INDEX IF NOT EXISTS order_lines_order_id_idx ON order_lines (order_id);

CREATE TABLE IF NOT EXISTS order_line_attributes
(
    order_line_id bigint                 NOT NULL,
    attribute_id  bigint                 NOT NULL,
    sort          integer                NOT NULL,
    value         character varying(255) NOT NULL,
    name          character varying(255) NOT NULL,
    CONSTRAINT order_line_attributes_order_line_fk FOREIGN KEY (order_line_id)
        REFERENCES order_lines (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS order_line_images
(
    order_line_id bigint  NOT NULL,
    sort          integer NOT NULL,
    location      text    NOT NULL,
    CONSTRAINT order_line_images_order_line_fk FOREIGN KEY (order_line_id)
        REFERENCES order_lines (id) ON DELETE CASCADE ON UPDATE CASCADE
);