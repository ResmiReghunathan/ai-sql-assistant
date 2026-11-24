CREATE TABLE customers (id SERIAL PRIMARY KEY, name TEXT, email TEXT, city TEXT);
CREATE TABLE products (id SERIAL PRIMARY KEY, name TEXT, category TEXT, price NUMERIC(10,2));
CREATE TABLE orders (id SERIAL PRIMARY KEY, customer_id INT REFERENCES customers(id), order_date DATE);
CREATE TABLE order_items (id SERIAL PRIMARY KEY, order_id INT REFERENCES orders(id),
  product_id INT REFERENCES products(id), quantity INT, unit_price NUMERIC(10,2));

INSERT INTO customers (name, email, city)
SELECT 'Customer ' || g, 'customer' || g || '@example.com',
  (ARRAY['Trivandrum','Kochi','Bengaluru','Chennai','Mumbai'])[1 + (g % 5)]
FROM generate_series(1, 50) g;

INSERT INTO products (name, category, price)
SELECT 'Product ' || g, (ARRAY['Electronics','Books','Clothing','Home'])[1 + (g % 4)],
  round((random() * 9000 + 100)::numeric, 2)
FROM generate_series(1, 20) g;

INSERT INTO orders (customer_id, order_date)
SELECT 1 + floor(random() * 50)::int, current_date - (random() * 180)::int
FROM generate_series(1, 300);

INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT x.order_id, x.product_id, x.quantity, p.price
FROM (SELECT o.id AS order_id, 1 + floor(random() * 20)::int AS product_id,
             1 + floor(random() * 4)::int AS quantity
      FROM orders o, generate_series(1, 2)) x
JOIN products p ON p.id = x.product_id;

CREATE USER readonly_user WITH PASSWORD 'readonly123';
GRANT CONNECT ON DATABASE shopdb TO readonly_user;
GRANT USAGE ON SCHEMA public TO readonly_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly_user;