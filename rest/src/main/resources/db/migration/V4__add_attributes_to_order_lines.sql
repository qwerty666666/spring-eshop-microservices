ALTER TABLE cart_items DROP COLUMN IF EXISTS product_name;

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