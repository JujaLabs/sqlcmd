ALTER TABLE orders DROP CONSTRAINT FK_orders_customers;
ALTER TABLE orders DROP CONSTRAINT FK_orders_shops;
ALTER TABLE orders_products DROP CONSTRAINT FK_products_id_orders_products;
ALTER TABLE orders_products DROP CONSTRAINT FK_orders_id_orders_products;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS orders_products CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS shops CASCADE;
