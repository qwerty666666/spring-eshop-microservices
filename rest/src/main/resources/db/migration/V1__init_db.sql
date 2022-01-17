CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START 1;

-- Cart

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
    product_name character varying(255)      NOT NULL,
    quantity     integer                     NOT NULL,
    cart_id      bigint                      NOT NULL,
    CONSTRAINT cart_items_pkey PRIMARY KEY (id),
    CONSTRAINT cart_items_uniq_item UNIQUE (cart_id, ean),
    CONSTRAINT cart_items_cart_fk FOREIGN KEY (cart_id) REFERENCES carts (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT cart_items_positive_qty CHECK (quantity > 0)
);


-- Checkout

CREATE TABLE IF NOT EXISTS deliveries
(
    id    character varying(255) NOT NULL,
    dtype character varying(31)  NOT NULL,
    name  character varying(255) NOT NULL,
    CONSTRAINT deliveries_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS payments
(
    id    character varying(255) NOT NULL,
    dtype character varying(31)  NOT NULL,
    name  character varying(255) NOT NULL,
    CONSTRAINT payments_pkey PRIMARY KEY (id)
);


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
    id   character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
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

CREATE TABLE IF NOT EXISTS product_images
(
    product_id character varying(255) NOT NULL,
    file_id    bigint                 NOT NULL,
    sort       integer                NOT NULL,
    CONSTRAINT product_images_pkey PRIMARY KEY (product_id, file_id),
    CONSTRAINT product_images_file_fk FOREIGN KEY (file_id) REFERENCES files (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT product_images_product_fk FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE ON UPDATE CASCADE
);


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


-- Customers

CREATE TABLE IF NOT EXISTS customers
(
    id        character varying(255) NOT NULL,
    birthday  date,
    email     character varying(255) NOT NULL,
    firstname character varying(255) NOT NULL,
    lastname  character varying(255) NOT NULL,
    password  character varying(255) NOT NULL,
    CONSTRAINT customers_pkey PRIMARY KEY (id),
    CONSTRAINT customers_uniq_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS permissions
(
    id   integer                NOT NULL,
    name character varying(255) NOT NULL,
    CONSTRAINT permissions_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS roles
(
    id   integer                NOT NULL,
    name character varying(255) NOT NULL,
    CONSTRAINT roles_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS roles_permissions
(
    role_id       integer NOT NULL,
    permission_id integer NOT NULL,
    CONSTRAINT roles_permissions_pkey PRIMARY KEY (role_id, permission_id),
    CONSTRAINT roles_permissions_permission_fk FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT roles_permissions_role_fk FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS users_roles
(
    user_id character varying(255) NOT NULL,
    role_id integer                NOT NULL,
    CONSTRAINT users_roles_pk PRIMARY KEY (user_id, role_id)
);


-- Orders

CREATE TYPE order_statuses AS ENUM ('PENDING');

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

CREATE TABLE order_lines
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


-- Stocks

CREATE TABLE stock_items
(
    id       bigint                        NOT NULL,
    ean      character varying(255) UNIQUE NOT NULL,
    quantity integer                       NOT NULL,
    CONSTRAINT stock_items_pkey PRIMARY KEY (id),
    CONSTRAINT stock_items_non_negative_qty CHECK (quantity >= 0)
);
