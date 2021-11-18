CREATE INDEX IF NOT EXISTS cart_items_cart_id_idx ON cart_items (cart_id);

CREATE INDEX IF NOT EXISTS products_categories_product_id_idx ON products_categories (product_id);

CREATE INDEX IF NOT EXISTS product_images_product_id_idx ON product_images (product_id);

CREATE INDEX IF NOT EXISTS sku_attributes_sku_id_idx ON sku_attributes (sku_id);

CREATE INDEX IF NOT EXISTS users_roles_user_id_idx ON users_roles (user_id);

CREATE INDEX IF NOT EXISTS orders_customer_id_idx ON orders (customer_id);;

CREATE INDEX IF NOT EXISTS order_lines_order_id_idx ON order_lines (order_id);

CREATE INDEX IF NOT EXISTS stock_items_ean_idx ON stock_items (ean);
