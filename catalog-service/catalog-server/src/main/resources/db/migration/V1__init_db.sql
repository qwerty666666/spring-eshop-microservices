CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START 1;

-- Categories

CREATE TABLE IF NOT EXISTS categories
(
    id        character varying(255) NOT NULL,
    name      character varying(255) NOT NULL,
    parent_id character varying(255),
    CONSTRAINT categories_pkey PRIMARY KEY (id),
    CONSTRAINT categories_parent_fk FOREIGN KEY (parent_id) REFERENCES categories (id) ON UPDATE CASCADE
);


-- Files

CREATE TABLE IF NOT EXISTS files
(
    id       bigint                 NOT NULL,
    location character varying(255) NOT NULL,
    CONSTRAINT files_pkey PRIMARY KEY (id),
    CONSTRAINT files_uniq_location UNIQUE (location)
);


-- Products

CREATE TABLE IF NOT EXISTS products
(
    id          character varying(255) NOT NULL,
    name        character varying(255) NOT NULL,
    description text                   NOT NULL,
    CONSTRAINT products_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS products_categories
(
    added_on    timestamp without time zone NOT NULL,
    product_id  character varying(255)      NOT NULL,
    category_id character varying(255)      NOT NULL,
    CONSTRAINT products_categories_pkey PRIMARY KEY (category_id, product_id),
    CONSTRAINT products_categories_category_fk FOREIGN KEY (category_id) REFERENCES categories (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT products_categories_product_fk FOREIGN KEY (product_id) REFERENCES products (id) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS products_categories_category_id_idx ON products_categories (category_id);
CREATE INDEX IF NOT EXISTS products_categories_product_id_idx ON products_categories (product_id);

CREATE TABLE IF NOT EXISTS product_images
(
    product_id character varying(255) NOT NULL,
    file_id    bigint                 NOT NULL,
    sort       integer                NOT NULL,
    CONSTRAINT product_images_pkey PRIMARY KEY (product_id, file_id),
    CONSTRAINT product_images_file_fk FOREIGN KEY (file_id) REFERENCES files (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT product_images_product_fk FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS product_images_product_id_idx ON product_images (product_id);


-- Sku

CREATE TABLE IF NOT EXISTS sku
(
    id                 bigint                 NOT NULL,
    available_quantity integer                NOT NULL,
    ean                character varying(13)  NOT NULL,
    price              numeric(19, 2)         NOT NULL,
    currency           character varying(255) NOT NULL,
    product_id         character varying(255) NOT NULL,
    CONSTRAINT sku_pkey PRIMARY KEY (id),
    CONSTRAINT sku_uniq_ean UNIQUE (ean),
    CONSTRAINT sku_product_fk FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT sku_non_negative_qty CHECK (available_quantity >= 0)
);
CREATE INDEX IF NOT EXISTS sku_product_id_idx ON sku(product_id);

CREATE TABLE IF NOT EXISTS attributes
(
    id   bigint                 NOT NULL,
    name character varying(255) NOT NULL,
    CONSTRAINT attributes_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS attribute_values
(
    id           bigint                 NOT NULL,
    sort         integer                NOT NULL,
    value        character varying(255) NOT NULL,
    attribute_id bigint                 NOT NULL,
    CONSTRAINT attribute_values_pkey PRIMARY KEY (id),
    CONSTRAINT attribute_values_attribute_fk FOREIGN KEY (attribute_id) REFERENCES attributes (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS attribute_values_attribute_id_idx ON attribute_values(attribute_id);

CREATE TABLE IF NOT EXISTS sku_attributes
(
    sku_id             bigint NOT NULL,
    attribute_value_id bigint NOT NULL,
    CONSTRAINT sku_attributes_pkey PRIMARY KEY (sku_id, attribute_value_id)
);
CREATE INDEX IF NOT EXISTS sku_attributes_sku_id_idx ON sku_attributes (sku_id);