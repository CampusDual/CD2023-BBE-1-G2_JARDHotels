INSERT INTO hotel (name, stars, address) VALUES
    ('Hotel Grand Hyatt', 5, '123 Main Street, Cityville'),
    ('Hotel Hilton', 4, '456 Elm Avenue, Townsville'),
    ('Hotel Marriott', 4, '789 Oak Lane, Villageland'),
    ('Hotel Radisson', 3, '321 Pine Road, Countryside'),
    ('Hotel Sheraton', 4, '987 Cedar Drive, Mountainview'),
    ('Hotel Holiday Inn', 3, '654 Maple Court, Lakeside'),
    ('Hotel Four Seasons', 5, '876 Birch Street, Beachtown'),
    ('Hotel Ritz-Carlton', 5, '543 Walnut Circle, Seaside'),
    ('Hotel Best Western', 3, '210 Spruce Avenue, Hillside'),
    ('Hotel Ibis', 2, '135 Oakwood Lane, Riverside');

INSERT INTO room (number, capacity, description, hotel) VALUES
    (101, 2, 'Standard Room with a queen bed', 1),
    (102, 2, 'Standard Room with a queen bed', 1),
    (201, 4, 'Family Suite with two queen beds', 2),
    (202, 4, 'Family Suite with two queen beds', 2),
    (301, 1, 'Single Room with a twin bed', 3),
    (302, 1, 'Single Room with a twin bed', 3),
    (401, 2, 'Double Room with two double beds', 4),
    (402, 2, 'Double Room with two double beds', 4),
    (501, 2, 'Deluxe Room with a king bed and ocean view', 5),
    (502, 2, 'Deluxe Room with a king bed and ocean view', 5);
   
INSERT INTO coin (coin) VALUES 
('€'),
('$'),
('Other');


INSERT INTO country (country, coin) VALUES 
	('Spain' ,1),
	('United States', 2),
	('United Kingdom', 3),
	('France', 1),
	('Germany', 1),
	('Portugal', 1),
	('China', 3),
	('Other', 3);


INSERT INTO guest (name, surname, phone, email, documentation, country) VALUES
    ('Juan', 'Gómez', '+34123456789', 'juangomez@example.com', '12345678Z', 1),
    ('María', 'Rodríguez', '987654321', 'mariarodriguez@example.com', '87654321B', 5),
    ('Antonio', 'López', '+11234567891', 'antoniolopez@example.com', '111222333', 2),
    ('Carmen', 'García', '111222333', 'carmengarcia@example.com', '76543210D', 8),
    ('Manuel', 'Martínez', '444444444', 'manuelmartinez@example.com', '34567890E', 7),
    ('Laura', 'Fernández', '666666666', 'laurafernandez@example.com', '67890123F', 4),
    ('Pedro', 'Navarro', '999999999', 'pedronavarro@example.com', '45678901G', 6),
    ('Isabel', 'Sánchez', '+34987987987', 'isabelsanchez@example.com', '87654321X', 1),
    ('José', 'Romero', '888888888', 'joseromero@example.com', '56789012I', 8),
    ('Ana', 'Jiménez', '222333444', 'anajimenez@example.com', '89012345J', 8);

INSERT INTO booking (room, guest, checkindate, checkoutdate)
VALUES
    (1, 2, '2024-05-01', '2024-05-05'),
    (3, 5, '2024-06-10', '2024-06-15'),
    (2, 6, '2024-07-20', '2024-07-25'),
    (4, 1, '2024-09-03', '2024-09-06'),
    (5, 4, '2024-10-15', '2024-10-20'),
    (7, 10, '2024-11-25', '2024-11-30'),
    (8, 9, '2025-02-05', '2025-02-10'),
    (6, 3, '2025-03-15', '2025-03-20'),
    (10, 7, '2025-05-01', '2025-05-05'),
    (9, 8, '2025-06-10', '2025-06-15');

