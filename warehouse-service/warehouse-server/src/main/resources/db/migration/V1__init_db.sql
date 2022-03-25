CREATE TABLE IF NOT EXISTS stock_items
(
    id       bigint                        NOT NULL,
    ean      character varying(255) UNIQUE NOT NULL,
    quantity integer                       NOT NULL,
    CONSTRAINT stock_items_pkey PRIMARY KEY (id),
    CONSTRAINT stock_items_non_negative_qty CHECK (quantity >= 0)
);
