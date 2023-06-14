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


INSERT INTO hotel (name, stars, address,country) VALUES
    ('Hotel Grand Hyatt', 5, '123 Main Street, Cityville',3),
    ('Hotel Hilton', 4, '456 Elm Avenue, Townsville',7),
    ('Hotel Marriott', 4, '789 Oak Lane, Villageland',4),
    ('Hotel Radisson', 3, '321 Pine Road, Countryside',1),
    ('Hotel Sheraton', 4, '987 Cedar Drive, Mountainview',5),
    ('Hotel Holiday Inn', 3, '654 Maple Court, Lakeside',6),
    ('Hotel Four Seasons', 5, '876 Birch Street, Beachtown',4),
    ('Hotel Ritz-Carlton', 5, '543 Walnut Circle, Seaside',2),
    ('Hotel Best Western', 3, '210 Spruce Avenue, Hillside',3),
    ('Hotel Ibis', 2, '135 Oakwood Lane, Riverside',8);

INSERT INTO room (number, capacity, description, hotel, price) VALUES
    (101, 2, 'Standard Room with a queen bed', 1, 50),
    (102, 2, 'Standard Room with a queen bed', 1, 50),
    (201, 4, 'Family Suite with two queen beds', 2, 120),
    (202, 4, 'Family Suite with two queen beds', 2, 120),
    (301, 1, 'Single Room with a twin bed', 3, 75),
    (302, 1, 'Single Room with a twin bed', 3, 75),
    (401, 2, 'Double Room with two double beds', 4, 75),
    (402, 2, 'Double Room with two double beds', 4, 75),
    (501, 2, 'Deluxe Room with a king bed and ocean view', 5, 200),
    (502, 2, 'Deluxe Room with a king bed and ocean view', 5, 200);
   

INSERT INTO person (name, surname, phone, email, documentation, country, phonecountry) VALUES
    ('Juan', 'Gómez', '+34123456789', 'juangomez@example.com', '12345678Z', 1,1),
    ('María', 'Rodríguez', '+4916012345', 'mariarodriguez@example.com', 'XY9876543', 5,5),
    ('Antonio', 'López', '+11234567891', 'antoniolopez@example.com', '111222333', 2,2),
    ('Carmen', 'García', '111222333', 'carmengarcia@example.com', '76543210D', 8,8),
    ('Manuel', 'Martínez', '+861987654321', 'manuelmartinez@example.com', '987654198512311234', 7,7),
    ('Laura', 'Fernández', '+330987654321', 'laurafernandez@example.com', '9305078912345678', 4,4),
    ('Pedro', 'Navarro', '+351289876543', 'pedronavarro@example.com', '987654321', 6,6),
    ('Isabel', 'Sánchez', '+34987987987', 'isabelsanchez@example.com', '87654321X', 1,1),
    ('José', 'Romero', '888888888', 'joseromero@example.com', '56789012I', 8,8),
    ('Ana', 'Jiménez', '+447876543210', 'anajimenez@example.com', 'G12345678', 3,3);
   
INSERT INTO guest (id) VALUES
   (2),
   (3),
   (5),
   (6),
   (8),
   (10);

INSERT INTO job (job) VALUES 
 ('cleaning service'),
 ('bellhop'),
 ('recepcionist'),
 ('cooker'),
 ('waiter'),
 ('lifeguard'),
 ('masseuse'),
 ('room service'),
 ('maintenance'),
 ('hotel manager');
 
INSERT INTO bankaccountformat (format) VALUES 
('IBAN'),
('CCC'),
('Sort Code'),
('ABA'),
('Other');

INSERT INTO staff (id, bankaccount, bankaccountformat, salary, job)
VALUES (1, '12345678901234567890', 1, 2500.00, 1),
       (4, '45678901234567890123', 1, 3200.00, 4),
       (6, '67890123456789012345', 2, 2600.00, 6),
       (7, '78901234567890123456', 3, 2900.00, 7),
       (8, '89012345678901234567', 5, 3100.00, 8),
       (9, '90123456789012345678', 4, 2700.00, 9),
       (10, '01234567890123456789', 1, 3500.00, 10);
 
INSERT INTO booking (room, guest, arrivaldate, departuredate)
VALUES
    (3, 5, '2024-06-10', '2024-06-15'),
    (2, 6, '2024-07-20', '2024-07-25'),
    (7, 10, '2024-11-25', '2024-11-30'),
    (6, 3, '2025-03-15', '2025-03-20'),
    (9, 8, '2025-06-10', '2025-06-15');
  
   --(1, 2, '2024-05-01', '2024-05-05'),
   --(4, 1, '2024-09-03', '2024-09-06'),

