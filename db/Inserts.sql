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


INSERT INTO hotel (name, stars, address, country, latitude, longitude) VALUES
    ('Hotel Grand Hyatt', 5, '123 Main Street, Cityville', 3, 40.7128, -74.0060),
    ('Hotel Hilton', 4, '456 Elm Avenue, Townsville', 7, 51.5074, -0.1278),
    ('Hotel Marriott', 4, '789 Oak Lane, Villageland', 4, 39.9526, -75.1652),
    ('Hotel Radisson', 3, '321 Pine Road, Countryside', 1, 41.8781, -87.6298),
    ('Hotel Sheraton', 4, '987 Cedar Drive, Mountainview', 5, 37.3861, -122.0839),
    ('Hotel Holiday Inn', 3, '654 Maple Court, Lakeside', 6, 45.4215, -75.6906),
    ('Hotel Four Seasons', 5, '876 Birch Street, Beachtown', 4, 40.7128, -74.0060),
    ('Hotel Ritz-Carlton', 5, '543 Walnut Circle, Seaside', 2, 25.7617, -80.1918),
    ('Hotel Best Western', 3, '210 Spruce Avenue, Hillside', 3, 40.7128, -74.0060),
    ('Hotel Ibis', 2, '135 Oakwood Lane, Riverside', 8, 34.0522, -118.2437);


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
    ('Juan', 'Gómez', '+34123456789', '12345678Z', 1,1),
    ('María', 'Rodríguez', '+4916012345', 'XY9876543', 5,5),--
    ('Antonio', 'López', '+11234567891', '111222333', 2,2),--
    ('Carmen', 'García', '111222333', '76543210D', 8,8),
    ('Manuel', 'Martínez', '+861987654321', '987654198512311234', 7,7),--
    ('Laura', 'Fernández', '+330987654321', '9305078912345678', 4,4),--
    ('Pedro', 'Navarro', '+351289876543', '987654321', 6,6),
    ('Isabel', 'Sánchez', '+34987987987', '87654321X', 1,1),--
    ('José', 'Romero', '888888888', '56789012I', 8,8),
    ('Ana', 'Jiménez', '+447876543210', 'G12345678', 3,3);--
   
   
INSERT INTO tuser (user_, password, email, idperson) VALUES
	('juan123', 'password1', 'juan123@example.com', 1),
	('maria456', 'password2', 'maria456@example.com', 2),
	('antonio789', 'password3', 'antonio789@example.com', 3),
	('carmen123', 'password4', 'carmen123@example.com', 4),
	('manuel987', 'password5', 'manuel987@example.com', 5),
	('laura321', 'password6', 'laura321@example.com', 6),
	('pedro654', 'password7', 'pedro654@example.com', 7),
	('isabel987', 'password8', 'isabel987@example.com', 8),
	('jose123', 'password9', 'jose123@example.com', 9),
	('ana456', 'password10', 'ana456@example.com', 10);


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
      
  
INSERT INTO staff (id, bankaccount, bankaccountformat, salary, job)VALUES 
	(1, 'ES6600190020961234567890', 1, 2500.00, 3),
    (4, 'DE89370400440532013000', 1, 3200.00, 4),
    (6, '6789678967896789678967', 2, 2600.00, 6),
    (7, '789012', 3, 2900.00, 7),
    (8, '901234567', 5, 3100.00, 8),
    (9, '123456789', 4, 2700.00, 9),
    (10, 'GB29NWBK60161331926819', 1, 3500.00, 10);

   
 --PERMISOS
/*********************************************************************************/
   
INSERT INTO trole VALUES 
	(1,'guest', '<?xml version="1.0" encoding="UTF-8"?><security></security>'),
	(2,'recepcionist', '<?xml version="1.0" encoding="UTF-8"?><security></security>'),
	(3,'hotel manager', '<?xml version="1.0" encoding="UTF-8"?><security></security>'),
	(4,'admin', '<?xml version="1.0" encoding="UTF-8"?><security></security>');

INSERT INTO tuser_role VALUES
	(1, 1, 'maria456'),
	(2, 1, 'antonio789'),
	(3, 1, 'manuel987'),
	(5, 1, 'laura321'),
	(6, 1, 'isabel987'),
	(7, 1, 'ana456'),
	(8, 2, 'juan123'),
	(9, 3, 'ana456');
	
INSERT INTO tserver_permission
	VALUES (1,'com.campusdual.jardhotelsontimize.api.core.service.IHotelService/hotelQuery'),
	VALUES (2,'com.campusdual.jardhotelsontimize.api.core.service.IHotelService/hotelInsert'),
	VALUES (3,'com.campusdual.jardhotelsontimize.api.core.service.IHotelService/hotelUpdate'),
	VALUES (4,'com.campusdual.jardhotelsontimize.api.core.service.IHotelService/hotelDelete'),
	VALUES (5,'com.campusdual.jardhotelsontimize.api.core.service.IRoomService/roomQuery'),
	VALUES (6,'com.campusdual.jardhotelsontimize.api.core.service.IRoomService/roomInsert'),
	VALUES (7,'com.campusdual.jardhotelsontimize.api.core.service.IRoomService/roomUpdate'),
	VALUES (8,'com.campusdual.jardhotelsontimize.api.core.service.IRoomService/roomDelete'),
	VALUES (9,'com.campusdual.jardhotelsontimize.api.core.service.IPersonService/personQuery'),
	VALUES (10,'com.campusdual.jardhotelsontimize.api.core.service.IPersonService/personInsert'),
	VALUES (11,'com.campusdual.jardhotelsontimize.api.core.service.IPersonService/personUpdate'),
	VALUES (12,'com.campusdual.jardhotelsontimize.api.core.service.IPersonService/personDelete'),
	VALUES (13,'com.campusdual.jardhotelsontimize.api.core.service.IGuestService/guestQuery'),
	VALUES (14,'com.campusdual.jardhotelsontimize.api.core.service.IGuestService/guestInsert'),
	VALUES (15,'com.campusdual.jardhotelsontimize.api.core.service.IGuestService/guestUpdate'),
	VALUES (16,'com.campusdual.jardhotelsontimize.api.core.service.IGuestService/guestDelete'),
	VALUES (17,'com.campusdual.jardhotelsontimize.api.core.service.IJobService/jobQuery'),
	VALUES (18,'com.campusdual.jardhotelsontimize.api.core.service.IBankAccountService/bankaccountQuery'),
	VALUES (19,'com.campusdual.jardhotelsontimize.api.core.service.IStaffService/staffQuery'),
	VALUES (20,'com.campusdual.jardhotelsontimize.api.core.service.IStaffService/staffInsert'),
	VALUES (21,'com.campusdual.jardhotelsontimize.api.core.service.IStaffService/staffUpdate'),
	VALUES (22,'com.campusdual.jardhotelsontimize.api.core.service.IStaffService/staffDelete'),
	VALUES (23,'com.campusdual.jardhotelsontimize.api.core.service.IBookingService/bookingQuery'),
	VALUES (24,'com.campusdual.jardhotelsontimize.api.core.service.IBookingService/bookingInsert'),
	VALUES (25,'com.campusdual.jardhotelsontimize.api.core.service.IBookingService/bookingUpdate'),
	VALUES (26,'com.campusdual.jardhotelsontimize.api.core.service.IBookingService/bookingDelete');

INSERT INTO trole_server_permission(id_rolename, id_server_permission)
	VALUES (1, 1), -- 1 -> guest
	VALUES (1, 5),
	VALUES (1, 9),
	VALUES (1, 10),
	VALUES (1, 11),
	VALUES (1, 12),
	VALUES (1, 13),
	VALUES (1, 14),
	VALUES (1, 15),
	VALUES (1, 16),
	VALUES (1, 23),
	VALUES (1, 24),
	VALUES (1, 25),
	VALUES (1, 26),
	VALUES (2, 5), -- 2 -> recepcionist
	VALUES (2, 13),
	VALUES (2, 14),
	VALUES (2, 15),
	VALUES (2, 16),
	VALUES (2, 23),
	VALUES (2, 24),
	VALUES (2, 25),
	VALUES (2, 26),
	VALUES (3, 1), -- 3 -> hotel manager
	VALUES (3, 3),
	VALUES (3, 5),
	VALUES (3, 6),
	VALUES (3, 7),
	VALUES (3, 8),
	VALUES (3, 19),
	VALUES (3, 20),
	VALUES (3, 21),
	VALUES (3, 22),
	VALUES(4,1), -- 4 -> admin
	VALUES(4,2),
	VALUES(4,3),
	VALUES(4,4),
	VALUES(4,5),
	VALUES(4,6),
	VALUES(4,7),
	VALUES(4,8),
	VALUES(4,9),
	VALUES(4,10),
	VALUES(4,11),
	VALUES(4,12),
	VALUES(4,13),
	VALUES(4,14),
	VALUES(4,15),
	VALUES(4,16),
	VALUES(4,17),
	VALUES(4,18),
	VALUES(4,19),
	VALUES(4,20),
	VALUES(4,21),
	VALUES(4,22),
	VALUES(4,23),
	VALUES(4,24),
	VALUES(4,25),
	VALUES(4,26);

/*********************************************************************************/


INSERT INTO booking (room, guest, arrivaldate, departuredate)
VALUES
    (3, 5, '2024-06-10', '2024-06-15'),
    (2, 6, '2024-07-20', '2024-07-25'),
    (7, 10, '2024-11-25', '2024-11-30'),
    (6, 3, '2025-03-15', '2025-03-20'),
    (9, 8, '2025-06-10', '2025-06-15');
  

