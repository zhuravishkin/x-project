INSERT INTO cep.records (status, payload, external_id)
VALUES ('CREATED', '{"orderId": 101, "amount": 1500, "currency": "USD"}', NULL),
       ('CREATED', '{"orderId": 102, "amount": 2000, "currency": "EUR"}', NULL),
       ('COMPLETE', '{"orderId": 103, "amount": 750,  "currency": "USD"}', 1),
       ('CREATED', '{"orderId": 104, "amount": 999,  "currency": "GBP"}', NULL),
       ('COMPLETE', '{"orderId": 105, "amount": 4500, "currency": "USD"}', 2),
       ('CREATED', '{"orderId": 106, "amount": 1200, "currency": "USD"}', NULL),
       ('COMPLETE', '{"orderId": 107, "amount": 300,  "currency": "EUR"}', 3),
       ('CREATED', '{"orderId": 108, "amount": 2200, "currency": "USD"}', NULL),
       ('COMPLETE', '{"orderId": 109, "amount": 510,  "currency": "JPY"}', 4),
       ('CREATED', '{"orderId": 110, "amount": 830,  "currency": "USD"}', NULL);
