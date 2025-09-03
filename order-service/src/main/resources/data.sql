INSERT INTO orders (total_price, order_status) VALUES
(100.5, 'PENDING'),
(200.75, 'CONFIRMED');

INSERT INTO order_item (order_id, product_id, quantity) VALUES
(1, 1, 2),
(2, 2, 1);