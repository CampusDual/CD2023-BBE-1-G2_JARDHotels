INSERT INTO coin (coin) VALUES 
('€'),
('$'),
('Other');


INSERT INTO country (country, coin) VALUES 
	('Spain', 1),
	('United States', 2),
	('United Kingdom', 3),
	('France', 1),
	('Germany', 1),
	('Portugal', 1),
	('China', 3),
	('Other', 3);


INSERT INTO hotel (name, stars, address, country, latitude, longitude, phone) VALUES
    ('Hotel Grand Hyatt', 5, '123 Main Street, Cityville', 3, 40.7128, -74.0060, '9121212121'),
    ('Hotel Hilton', 4, '456 Elm Avenue, Townsville', 7, 51.5074, -0.1278, '775546543'),
    ('Hotel Marriott', 4, '789 Oak Lane, Villageland', 4, 39.9526, -75.1652, '748396655'),
    ('Hotel Radisson', 3, '321 Pine Road, Countryside', 1, 41.8781, -87.6298, '112233445'),
    ('Hotel Sheraton', 4, '987 Cedar Drive, Mountainview', 5, 37.3861, -122.0839, '12345678901234567890'),
    ('Hotel Holiday Inn', 3, '654 Maple Court, Lakeside', 6, 45.4215, -75.6906, '688899944'),
    ('Hotel Four Seasons', 5, '876 Birch Street, Beachtown', 4, 40.7128, -74.0060, '001100223'),
    ('Hotel Ritz-Carlton', 5, '543 Walnut Circle, Seaside', 2, 25.7617, -80.1918, '9998887776'),
    ('Hotel Best Western', 3, '210 Spruce Avenue, Hillside', 3, 40.7128, -74.0060, '5728192836'),
    ('Hotel Ibis', 2, '135 Oakwood Lane, Riverside', 8, 34.0522, -118.2437, '01912718919');


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
   

INSERT INTO person (name, surname, phone, documentation, country, phonecountry) VALUES
    ('Juan', 'Gómez', '123456789', '12345678Z', 1,1),
    ('María', 'Rodríguez', '16012345', 'XY9876543', 5,5),
    ('Antonio', 'López', '1234567891', '111222333', 2,2),
    ('Carmen', 'García', '111222333', '76543210D', 8,8),
    ('Manuel', 'Martínez', '987654321', '987654198512311234', 7,7),
    ('Laura', 'Fernández', '987654321', '9305078912345678', 4,4),
    ('Pedro', 'Navarro', '289876543', '987654321', 6,6),
    ('Isabel', 'Sánchez', '987987987', '87654321X', 1,1),
    ('José', 'Romero', '888888888', '56789012I', 8,8),
    ('Ana', 'Jiménez', '7876543210', 'G12345678', 3,3);
   
   
INSERT INTO person (id, name, surname, phone, documentation, country, phonecountry) VALUES   
	(-1, 'Admin', 'Admin', 'Admin', 'Admin', 8,8),
	(-2, 'Demo', 'Demo', 'Demo', 'Demo', 8,8);
   
INSERT INTO tuser (username, password, email, idperson) VALUES
	('juan123', 'password1', 'juan123@example.com', 1),
	('maria456', 'password2', 'maria456@example.com', 2),
	('antonio789', 'password3', 'antonio789@example.com', 3),
	('carmen123', 'password4', 'carmen123@example.com', 4),
	('manuel987', 'password5', 'manuel987@example.com', 5),
	('laura321', 'password6', 'laura321@example.com', 6),
	('pedro654', 'password7', 'pedro654@example.com', 7),
	('isabel987', 'password8', 'isabel987@example.com', 8),
	('jose123', 'password9', 'jose123@example.com', 9),
	('ana456', 'password10', 'ana456@example.com', 10),
	('admin', 'admin', 'admin@admin.com', -1),
	('demouser', 'demo', 'demo@demo.com', -2);


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


INSERT INTO guest (id) VALUES
   (2),
   (3),
   (5),
   (6),
   (8),
   (10);
      
  
INSERT INTO staff (id, bankaccount, bankaccountformat, salary, job, idhotel)VALUES 
	(1, 'ES6600190020961234567890', 1, 2500.00, 3, 1),
    (4, 'DE89370400440532013000', 1, 3200.00, 4, 2),
    (6, '6789678967896789678967', 2, 2600.00, 3, 4),
    (7, '789012', 3, 2900.00, 7, 6),
    (8, '901234567', 5, 3100.00, 8, 7),
    (9, '123456789', 4, 2700.00, 9, 8),
    (10, 'GB29NWBK60161331926819', 1, 3500.00, 10, 10);

   
 --PERMISOS
/*********************************************************************************/
   
INSERT INTO trole VALUES 
	(1,'guest', '<?xml version="1.0" encoding="UTF-8"?><security></security>'),
	(2,'recepcionist', '<?xml version="1.0" encoding="UTF-8"?><security></security>'),
	(3,'hotel manager', '<?xml version="1.0" encoding="UTF-8"?><security></security>'),
	(4,'admin', '<?xml version="1.0" encoding="UTF-8"?><security></security>'),
	(5,'demo', '<?xml version="1.0" encoding="UTF-8"?><security></security>');

INSERT INTO tuser_role(id_role, user_name) VALUES
	(1, 'maria456'),
	(1, 'antonio789'),
	(1, 'manuel987'),
	(1, 'laura321'),
	(2, 'laura321'),
	(1, 'isabel987'),
	(1, 'ana456'),
	(2, 'juan123'),
	(3, 'ana456'),
	(4, 'admin'),
	(5, 'demouser');
	
INSERT INTO tserver_permission VALUES 
     (1,'com.campusdual.jardhotelsontimize.api.core.service.IHotelService/hotelQuery'),
	 (2,'com.campusdual.jardhotelsontimize.api.core.service.IHotelService/hotelInsert'),
	 (3,'com.campusdual.jardhotelsontimize.api.core.service.IHotelService/hotelUpdate'),
	 (4,'com.campusdual.jardhotelsontimize.api.core.service.IHotelService/hotelDelete'),
	 (5,'com.campusdual.jardhotelsontimize.api.core.service.IRoomService/roomQuery'),
	 (6,'com.campusdual.jardhotelsontimize.api.core.service.IRoomService/roomInsert'),
	 (7,'com.campusdual.jardhotelsontimize.api.core.service.IRoomService/roomUpdate'),
	 (8,'com.campusdual.jardhotelsontimize.api.core.service.IRoomService/roomDelete'),
	 (9,'com.campusdual.jardhotelsontimize.api.core.service.IPersonService/personQuery'),
	 (10,'com.campusdual.jardhotelsontimize.api.core.service.IPersonService/personInsert'),
	 (11,'com.campusdual.jardhotelsontimize.api.core.service.IPersonService/personUpdate'),
	 (12,'com.campusdual.jardhotelsontimize.api.core.service.IPersonService/personDelete'),
	 (13,'com.campusdual.jardhotelsontimize.api.core.service.IGuestService/guestQuery'),
	 (14,'com.campusdual.jardhotelsontimize.api.core.service.IGuestService/guestUpdate'),
	 (15,'com.campusdual.jardhotelsontimize.api.core.service.IGuestService/guestDelete'),
	 (16,'com.campusdual.jardhotelsontimize.api.core.service.IJobService/jobQuery'),
	 (17,'com.campusdual.jardhotelsontimize.api.core.service.IBankAccountService/bankaccountQuery'),
	 (18,'com.campusdual.jardhotelsontimize.api.core.service.IStaffService/staffQuery'),
	 (19,'com.campusdual.jardhotelsontimize.api.core.service.IStaffService/staffInsert'),
	 (20,'com.campusdual.jardhotelsontimize.api.core.service.IStaffService/staffUpdate'),
	 (21,'com.campusdual.jardhotelsontimize.api.core.service.IStaffService/staffDelete'),
	 (22,'com.campusdual.jardhotelsontimize.api.core.service.IBookingService/bookingQuery'),
	 (23,'com.campusdual.jardhotelsontimize.api.core.service.IBookingService/bookingInsert'),
	 (24,'com.campusdual.jardhotelsontimize.api.core.service.IBookingService/bookingUpdate'),
	 (25,'com.campusdual.jardhotelsontimize.api.core.service.IBookingService/bookingDelete'),
	 (26,'com.campusdual.jardhotelsontimize.api.core.service.IUserService/userQuery'),
	 (27,'com.campusdual.jardhotelsontimize.api.core.service.IMenuService/menuInsert'),
	 (28,'com.campusdual.jardhotelsontimize.api.core.service.IMenuService/menuUpdate'),
	 (29,'com.campusdual.jardhotelsontimize.api.core.service.IMenuService/menuDelete');
	 
	 
INSERT INTO trole_server_permission(id_rolename, id_server_permission) VALUES 
	 (1, 1), -- 1 -> guest
	 (1, 5),
	 (1, 9),
	 (1, 11),
	 (1, 12),
	 (1, 13),
	 (1, 14),
	 (1, 15),
	 (1, 18),
	 (1, 23),
	 (1, 24),
	 (1, 25),
	 (1, 26),
	 (1, 22),
	 (2, 5), -- 2 -> recepcionist
	 (2, 13),
	 (2, 10),
	 (2, 14),
	 (2, 15),
	 (2, 23),
	 (2, 24),
	 (2, 25),
	 (2, 26),
	 (2, 18),
	 (2, 22),
	 (2, 9),
	 (3, 1), -- 3 -> hotel manager
	 (3, 3),
	 (3, 5),
	 (3, 6),
	 (3, 7),
	 (3, 8),
	 (3, 9),
	 (3, 10),
	 (3, 11),
	 (3, 12),
	 (3, 19),
	 (3, 20),
	 (3, 21),
	 (3, 18),
	 (3, 26),
	 (3, 18),
	 (3, 17),
	 (3, 9),
	 (3, 16),
	 (4, 1), -- 4 -> admin
	 (4, 2),
	 (4, 3),
	 (4, 4),
	 (4, 5),
	 (4, 6),
	 (4, 7),
	 (4, 8),
	 (4, 9),
	 (4, 10),
	 (4, 11),
	 (4, 12),
	 (4, 13),
	 (4, 14),
	 (4, 15),
	 (4, 16),
	 (4, 17),
	 (4, 18),
	 (4, 19),
  	 (4, 20),
	 (4, 21),
	 (4, 22),
	 (4, 23),
	 (4, 24),
	 (4, 25),
	 (4, 26),
	 (4, 27),
	 (4, 28),
	 (4, 29),
	 (5, 9),  -- 5 -> demouser
	 (5, 26),
     (5, 10);

/*********************************************************************************/


INSERT INTO booking (room, guest, arrivaldate, departuredate, code)
VALUES
    (3, 5, '2024-06-10', '2024-06-15','3-5-2024-06-10-95134'),
    (2, 6, '2024-07-20', '2024-07-25', '2-6-2024-07-20-71739'),
    (7, 10, '2024-11-25', '2024-11-30', '7-10-2024-11-25-32760'),
    (6, 3, '2025-03-15', '2025-03-20', '6-3-2025-03-15-09528'),
    (9, 8, '2025-06-10', '2025-06-15', '9-8-2025-06-10-98765');
   
INSERT INTO menu (name) VALUES
	('Champagne'),
	('Croissant'),
	('Fruit Platter'),
	('Omelette'),
	('Yogurt'),
	('Coffee'),
	('Tea'),
	('Cheese Platter'),
	('Sandwich'),
	('Pasta Salad');

-- Hotel 1
INSERT INTO pantry (idmenu, idhotel, amount) VALUES
(1, 1, 5), -- Champagne (5 bottles)
(2, 1, 10), -- Croissant (10 pieces)
(3, 1, 3), -- Fruit Platter (3 platters)
(4, 1, 6), -- Omelette (6 servings)
(6, 1, 20), -- Coffee (20 cups)
(7, 1, 15), -- Tea (15 cups)
(8, 1, 4), -- Cheese Platter (4 platters)
(9, 1, 12), -- Sandwich (12 pieces)
(10, 1, 8); -- Pasta Salad (8 servings)

-- Hotel 2
INSERT INTO pantry (idmenu, idhotel, amount) VALUES
(1, 2, 3), -- Champagne (3 bottles)
(2, 2, 8), -- Croissant (8 pieces)
(3, 2, 5), -- Fruit Platter (5 platters)
(4, 2, 10), -- Omelette (10 servings)
(6, 2, 25), -- Coffee (25 cups)
(7, 2, 20), -- Tea (20 cups)
(8, 2, 6), -- Cheese Platter (6 platters)
(9, 2, 15), -- Sandwich (15 pieces)
(10, 2, 10); -- Pasta Salad (10 servings)

-- Hotel 3
INSERT INTO pantry (idmenu, idhotel, amount) VALUES
(1, 3, 6), -- Champagne (6 bottles)
(2, 3, 12), -- Croissant (12 pieces)
(3, 3, 8), -- Fruit Platter (8 platters)
(4, 3, 15), -- Omelette (15 servings)
(6, 3, 30), -- Coffee (30 cups)
(7, 3, 25), -- Tea (25 cups)
(8, 3, 8), -- Cheese Platter (8 platters)
(9, 3, 20), -- Sandwich (20 pieces)
(10, 3, 15); -- Pasta Salad (15 servings)

-- Hotel 4
INSERT INTO pantry (idmenu, idhotel, amount) VALUES
(1, 4, 4), -- Champagne (4 bottles)
(2, 4, 10), -- Croissant (10 pieces)
(3, 4, 6), -- Fruit Platter (6 platters)
(4, 4, 12), -- Omelette (12 servings)
(6, 4, 20), -- Coffee (20 cups)
(7, 4, 18), -- Tea (18 cups)
(8, 4, 5), -- Cheese Platter (5 platters)
(9, 4, 12), -- Sandwich (12 pieces)
(10, 4, 10); -- Pasta Salad (10 servings)

-- Hotel 5
INSERT INTO pantry (idmenu, idhotel, amount) VALUES
(1, 5, 7), -- Champagne (7 bottles)
(2, 5, 15), -- Croissant (15 pieces)
(3, 5, 10), -- Fruit Platter (10 platters)
(4, 5, 20), -- Omelette (20 servings)
(6, 5, 35), -- Coffee (35 cups)
(7, 5, 30), -- Tea (30 cups)
(8, 5, 10), -- Cheese Platter (10 platters)
(9, 5, 25), -- Sandwich (25 pieces)
(10, 5, 20); -- Pasta Salad (20 servings)

-- Hotel 6
INSERT INTO pantry (idmenu, idhotel, amount) VALUES
(1, 6, 5), -- Champagne (5 bottles)
(2, 6, 12), -- Croissant (12 pieces)
(3, 6, 7), -- Fruit Platter (7 platters)
(4, 6, 14), -- Omelette (14 servings)
(6, 6, 25), -- Coffee (25 cups)
(7, 6, 22), -- Tea (22 cups)
(8, 6, 6), -- Cheese Platter (6 platters)
(9, 6, 18), -- Sandwich (18 pieces)
(10, 6, 15); -- Pasta Salad (15 servings)

-- Hotel 7
INSERT INTO pantry (idmenu, idhotel, amount) VALUES
(1, 7, 3), -- Champagne (3 bottles)
(2, 7, 8), -- Croissant (8 pieces)
(3, 7, 5), -- Fruit Platter (5 platters)
(4, 7, 10), -- Omelette (10 servings)
(6, 7, 20), -- Coffee (20 cups)
(7, 7, 18), -- Tea (18 cups)
(8, 7, 4), -- Cheese Platter (4 platters)
(9, 7, 12), -- Sandwich (12 pieces)
(10, 7, 10); -- Pasta Salad (10 servings)

-- Hotel 8
INSERT INTO pantry (idmenu, idhotel, amount) VALUES
(1, 8, 6), -- Champagne (6 bottles)
(2, 8, 12), -- Croissant (12 pieces)
(3, 8, 8), -- Fruit Platter (8 platters)
(4, 8, 15), -- Omelette (15 servings)
(6, 8, 30), -- Coffee (30 cups)
(7, 8, 25), -- Tea (25 cups)
(8, 8, 8), -- Cheese Platter (8 platters)
(9, 8, 20), -- Sandwich (20 pieces)
(10, 8, 15); -- Pasta Salad (15 servings)

-- Hotel 9
INSERT INTO pantry (idmenu, idhotel, amount) VALUES
(1, 9, 4), -- Champagne (4 bottles)
(2, 9, 10), -- Croissant (10 pieces)
(3, 9, 6), -- Fruit Platter (6 platters)
(4, 9, 12), -- Omelette (12 servings)
(6, 9, 20), -- Coffee (20 cups)
(7, 9, 18), -- Tea (18 cups)
(8, 9, 5), -- Cheese Platter (5 platters)
(9, 9, 12), -- Sandwich (12 pieces)
(10, 9, 10); -- Pasta Salad (10 servings)

-- Hotel 10
INSERT INTO pantry (idmenu, idhotel, amount) VALUES
(1, 10, 7), -- Champagne (7 bottles)
(2, 10, 15), -- Croissant (15 pieces)
(3, 10, 10), -- Fruit Platter (10 platters)
(4, 10, 20), -- Omelette (20 servings)
(6, 10, 35), -- Coffee (35 cups)
(7, 10, 30), -- Tea (30 cups)
(8, 10, 10), -- Cheese Platter (10 platters)
(9, 10, 25), -- Sandwich (25 pieces)
(10, 10, 20); -- Pasta Salad (20 servings)
  

