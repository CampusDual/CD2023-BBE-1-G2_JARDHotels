DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS trole_server_permission;
DROP TABLE IF EXISTS tserver_permission;
DROP TABLE IF EXISTS tuser_role;
DROP TABLE IF EXISTS trole;
DROP TABLE IF EXISTS tuser;
DROP TABLE IF EXISTS guest;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS job;
DROP TABLE IF EXISTS bankaccountformat; 
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS room;
DROP TABLE IF EXISTS hotel;
DROP TABLE IF EXISTS country;
DROP TABLE IF EXISTS coin;

/******************Tabla moneda******************/

CREATE TABLE IF NOT EXISTS coin (
	id SERIAL PRIMARY KEY,
	coin VARCHAR(30) NOT NULL
);

/******************Tabla país******************/

CREATE TABLE IF NOT EXISTS country (
	id SERIAL PRIMARY KEY,
	country VARCHAR(60) NOT NULL,
	coin INTEGER NOT NULL,
	FOREIGN KEY(coin) REFERENCES coin(id) 
	ON DELETE CASCADE 
	ON UPDATE CASCADE
);

/******************Tabla hotel******************/
CREATE TABLE IF NOT EXISTS hotel(
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	stars INTEGER NOT NULL,
	address VARCHAR (200) NOT NULL,
	country INTEGER NOT NULL,
	latitude DECIMAL (9,7) NOT NULL,
	longitude DECIMAL (10,7) NOT NULL,
	
	FOREIGN KEY(country) REFERENCES country(id)
	ON DELETE RESTRICT
	ON UPDATE CASCADE,
	
	CONSTRAINT "Stars must be between one and five" 
		CHECK(stars >= 1 AND stars <= 5)
);

/******************Tabla habitación******************/
CREATE TABLE IF NOT EXISTS room(
	id SERIAL PRIMARY KEY,
	number INTEGER NOT NULL,
	capacity INTEGER NOT NULL,
	description TEXT,
	hotel INTEGER NOT NULL,
	price DECIMAL NOT NULL,
	
	FOREIGN KEY(hotel) REFERENCES hotel(id) 
		ON DELETE CASCADE 
		ON UPDATE CASCADE,
		
	CONSTRAINT "Repeated number in hotel" 
		UNIQUE (number,hotel),
	
	CONSTRAINT "Number must be over zero" 
		CHECK(number >= 1),
	
	CONSTRAINT "Capacity must be over zero" 
		CHECK(capacity >= 1),
		
	CONSTRAINT "The price must be greater than 0"
		CHECK (price > 0)
);

CREATE OR REPLACE FUNCTION check_hotel_from_room()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.hotel <> OLD.hotel THEN
        RAISE EXCEPTION 'Change the hotel of a room is not allowed';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_hotel_room_update
BEFORE UPDATE ON room
FOR EACH ROW
EXECUTE FUNCTION check_hotel_from_room();



 /*************************************************/
/******************Tabla persona******************/
CREATE TABLE IF NOT EXISTS person (
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	surname VARCHAR(250) NOT NULL,
	phone VARCHAR(30) NOT NULL,
	documentation VARCHAR(80) NOT NULL,
	country INTEGER NOT NULL,
	phonecountry INTEGER NOT NULL,
	
	FOREIGN KEY(country) REFERENCES country(id)
		ON DELETE RESTRICT
		ON UPDATE CASCADE,
		
	FOREIGN KEY(phonecountry) REFERENCES country(id)
		ON DELETE RESTRICT
		ON UPDATE CASCADE,
	
	CONSTRAINT "Repeated documentation in another person" 
		UNIQUE (documentation)
	
);
--COMPROBAR DOCUMENTACIONES Y TELEFONOS

--ESPAÑA: 

--Función de DNI España
--comprobar 8 numeros y una letra
--comprobar letra correcta
CREATE OR REPLACE FUNCTION verify_documentation_spain() RETURNS TRIGGER AS $$
DECLARE
  letters_dni text[] := ARRAY['T','R','W','A','G','M','Y','F','P','D','X','B','N','J','Z','S','Q','V','H','L','C','K','E']; -- Letras para el DNI
  dni_number INTEGER;
  dni_letter TEXT;
BEGIN
  dni_number := CAST(substring(NEW.documentation, 1, 8) AS INTEGER);
  dni_letter := substring(NEW.documentation, 9, 1);
  
  IF length(NEW.documentation) <> 9 THEN
    RAISE EXCEPTION 'The spanish DNI must have 9 characters';
  END IF;
  
  IF substring(NEW.documentation, 1, 8) !~ '^\d+$' THEN
    RAISE EXCEPTION 'The 8 first characters of a spanish DNI must be numbers';
  END IF;

  IF dni_letter !~ '^[A-Z]$' THEN
    RAISE EXCEPTION 'The last character of a spanish DNI must be a letter';
  END IF;
  
  IF dni_letter <> letters_dni[(dni_number % 23) + 1] THEN
    RAISE EXCEPTION 'The letter of the spanish DNI is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--funcion telefono españa
--empieza por +34 seguido de 9 numeros
CREATE OR REPLACE FUNCTION verify_phone_spain() RETURNS TRIGGER AS $$
BEGIN
  IF NEW.phone !~ '\d{9}$' THEN
    RAISE EXCEPTION 'The format for the spanish phone is incorrect. It must be +34 and 9 numbers';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_verify_phone_spain
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.phonecountry = 1) --Id de España debe ser 1
EXECUTE FUNCTION verify_phone_spain();

CREATE TRIGGER trigger_verify_documentation_spain
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.country = 1) --Id de España debe ser 1
EXECUTE FUNCTION verify_documentation_spain();

--ESTADOS UNIDOS:

--Función de Pasaporte de Estados Unidos
--9 caracteres
CREATE OR REPLACE FUNCTION verify_documentation_united_states() RETURNS TRIGGER AS $$
BEGIN
  IF NEW.documentation !~ '^\d{9}$' THEN
    RAISE EXCEPTION 'El format for the passport in United States is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--Función de Telefono de EEUU
--(3 numeros)3 numeros-4 numeros ej: +11234567890
CREATE OR REPLACE FUNCTION verify_phone_united_states() RETURNS TRIGGER AS $$
BEGIN
  IF NEW.phone !~ '\d{10}$' THEN
    RAISE EXCEPTION 'The format for the phone in United States is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_verify_phone_united_states
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.phonecountry = 2) --Id de EEUU debe ser 2
EXECUTE FUNCTION verify_phone_united_states();

CREATE TRIGGER trigger_verify_documentation_united_states
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.country = 2) --Id de EEUU debe ser 2
EXECUTE FUNCTION verify_documentation_united_states();


--REINO UNIDO
--Función de Pasaporte de Reino Unido ej: AB1234567

CREATE OR REPLACE FUNCTION verify_documentation_united_kingdom() RETURNS TRIGGER AS $$
BEGIN
  IF NEW.documentation !~ '^(?!^0+$)[a-zA-Z0-9]{9}$|^(?!^0+$)[a-zA-Z0-9]{10}$' THEN
    RAISE EXCEPTION 'The format for the passport in United Kingdom is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;


--Función de Telefono de Reino Unido ej: +441234567890

CREATE OR REPLACE FUNCTION verify_phone_united_kingdom() RETURNS TRIGGER AS $$
BEGIN
  IF NEW.phone !~ '[1-9]\d{9}$' THEN
    RAISE EXCEPTION 'The format for the phone in United Kingdom is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_verify_phone_united_kingdom
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.phonecountry = 3) --Id de UK debe ser 3
EXECUTE FUNCTION verify_phone_united_kingdom();

CREATE TRIGGER trigger_verify_documentation_united_kingdom
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.country = 3) --Id de UK debe ser 3
EXECUTE FUNCTION verify_documentation_united_kingdom();


--FRANCIA
--Función de Tarjeta Nacional de identidad de Francia ej: 8310123456789016

CREATE OR REPLACE FUNCTION verify_documentation_france() RETURNS TRIGGER AS $$
BEGIN
  IF NEW.documentation !~ '^(?!00000)[0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[1-2][0-9]|3[0-1])(?:[0-9]{3})(?:(?:[0-8][0-9])|(?:9[0-7]))[0-9]{3}[0-9]{2}$' THEN
    RAISE EXCEPTION 'The format for the card in France is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--Función de Telefono de Francia ej: +330612345678
CREATE OR REPLACE FUNCTION verify_phone_france() RETURNS TRIGGER AS $$
BEGIN
  IF NEW.phone !~ '\d{9}$' THEN
    RAISE EXCEPTION 'The format for the phone in France is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_verify_phone_france
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.phonecountry = 4) --Id de Francia debe ser 4
EXECUTE FUNCTION verify_phone_france();

CREATE TRIGGER trigger_verify_documentation_france
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.country = 4) --Id de Francia debe ser 4
EXECUTE FUNCTION verify_documentation_france();


--ALEMANIA
--Función de documento de identidad de Alemania ej: AB1234568

CREATE OR REPLACE FUNCTION verify_documentation_germany() RETURNS TRIGGER AS $$
BEGIN
  IF NEW.documentation !~ '^[A-Z]{2}\d{7}$' THEN
    RAISE EXCEPTION 'The format for the documentation in Germany is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--Función de Telefono de Alemania ej: +49123456789

CREATE OR REPLACE FUNCTION verify_phone_germany() RETURNS TRIGGER AS $$
BEGIN
  IF NEW.phone !~ '[1-9]\d{1,4}\d{1,10}$' THEN
    RAISE EXCEPTION 'The format for the phone in Germany is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_verify_phone_germany
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.phonecountry = 5) --Id de Alemania debe ser 5
EXECUTE FUNCTION verify_phone_germany();

CREATE TRIGGER trigger_verify_documentation_germany
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.country = 5) --Id de Alemania debe ser 5
EXECUTE FUNCTION verify_documentation_germany();

--PORTUGAL
--Función de documento de identidad de Portugal ej: 123456789

CREATE OR REPLACE FUNCTION verify_documentation_portugal() RETURNS TRIGGER AS $$
BEGIN
  IF NEW.documentation !~ '^\d{9}$' THEN
    RAISE EXCEPTION 'The format for the documentation in Portugal is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--Función de Telefono de Portugal ej: +351912345678

CREATE OR REPLACE FUNCTION verify_phone_portugal() RETURNS TRIGGER AS $$
BEGIN
  IF NEW.phone !~ '[28969]\d{8}$' THEN
    RAISE EXCEPTION 'The format for the phone in Portugal is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_verify_phone_portugal
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.phonecountry = 6) --Id de Portugal debe ser 6
EXECUTE FUNCTION verify_phone_portugal();

CREATE TRIGGER trigger_verify_documentation_portugal
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.country = 6) --Id de Portugal debe ser 6
EXECUTE FUNCTION verify_documentation_portugal();

--CHINA
--Función de documento de China ej: 123456199001010123

CREATE OR REPLACE FUNCTION verify_documentation_china() RETURNS TRIGGER AS $$
DECLARE
  birthdate DATE;
BEGIN
  IF NEW.documentation !~ '^[1-9]\d{5}(?:19|20)\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01])\d{3}[\dX]$' THEN
    RAISE EXCEPTION 'The format for the documentation in China is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--Función de Telefono de China ej: +861012345678

CREATE OR REPLACE FUNCTION verify_phone_china() RETURNS TRIGGER AS $$
BEGIN
  IF NEW.phone !~ '\d{9}$' THEN
    RAISE EXCEPTION 'The format for the phone in China is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trigger_verify_phone_china
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.phonecountry = 7) --Id de China debe ser 7
EXECUTE FUNCTION verify_phone_china();

CREATE TRIGGER trigger_verify_documentation_china
BEFORE INSERT OR UPDATE ON person
FOR EACH ROW
WHEN (NEW.country = 7) --Id de China debe ser 7
EXECUTE FUNCTION verify_documentation_china();


--------------------------SECURIZACIÓN-------------------------------
 /*************************************************/
/******************Tabla usuario******************/

CREATE TABLE IF NOT EXISTS tuser(
	username VARCHAR(50) NOT NULL PRIMARY KEY,
	password VARCHAR(255) NOT NULL,
	email VARCHAR(100) NOT NULL,
	lastpasswordupdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	firstlogin BOOLEAN DEFAULT TRUE,
	userbloqued BOOLEAN DEFAULT FALSE,
	idperson INTEGER NOT NULL,
	
	UNIQUE(idperson),
	
	FOREIGN KEY(idperson) REFERENCES person (id)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	
	CONSTRAINT "Invalid email format" 
		CHECK (email ~* '^[A-ZA-Z0-9._%+-]+@[A-ZA-Z0-9.-]+\.[A-ZA-Z]{2,}$')
);

 /*************************************************/
/******************Tabla rol**********************/
CREATE TABLE IF NOT EXISTS trole(
	id SERIAL PRIMARY KEY,
	rolename VARCHAR(255) NOT NULL,
	xmlclientpermission VARCHAR(10485760) NOT NULL
);

 /*********************************************************/
/******************Tabla usuario rol**********************/
CREATE TABLE IF NOT EXISTS tuser_role(
	id SERIAL PRIMARY KEY,
	id_role INTEGER NOT NULL,
	user_name VARCHAR(50) NOT NULL,
	
	CONSTRAINT "unique role in user" 
		UNIQUE (id_role, user_name),
	
	FOREIGN KEY(id_role) REFERENCES trole(id)
		ON DELETE RESTRICT
		ON UPDATE CASCADE,
		
	FOREIGN KEY(user_name) references tuser(username)
		ON DELETE CASCADE
		ON UPDATE CASCADE
); 

 /******************************************************/
/******************Tabla permisos**********************/
CREATE TABLE tserver_permission(
	id SERIAL PRIMARY KEY,
	permission_name VARCHAR(10485760)
);

CREATE TABLE trole_server_permission(
	id SERIAL PRIMARY KEY,
	id_rolename INTEGER NOT NULL,
	id_server_permission INTEGER NOT NULL,
	
	FOREIGN KEY(id_rolename) REFERENCES trole(id),
	FOREIGN KEY(id_server_permission) REFERENCES tserver_permission(id)
);

---------------------------------------------------------------------
---------------------------------------------------------------------

 /*************************************************/
/******************Tabla huesped******************/

CREATE TABLE IF NOT EXISTS guest (
	id INTEGER PRIMARY KEY,
	FOREIGN KEY(id) REFERENCES person(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);
 /*****************************************************************/
/******************Tabla formato cuenta bancaria******************/
CREATE TABLE IF NOT EXISTS bankaccountformat(
	id SERIAL PRIMARY KEY,
	format VARCHAR(20) NOT NULL
);
/*************************************************/
/******************Tabla puestos******************/

CREATE TABLE IF NOT exists job(
	id SERIAL PRIMARY KEY,
	job VARCHAR(40) NOT NULL
);


/**************************************************/
/******************Tabla personal******************/

CREATE TABLE IF NOT EXISTS staff (
	id INTEGER PRIMARY KEY,
	bankaccount VARCHAR(50) NOT NULL,
	bankaccountformat INTEGER NOT NULL,
	salary DECIMAL(8,2) NOT NULL,
	job INTEGER NOT NULL,
	idhotel INTEGER NOT NULL,
	FOREIGN KEY(id) REFERENCES person(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	FOREIGN KEY(idhotel) REFERENCES hotel(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	FOREIGN KEY(bankaccountformat) REFERENCES bankaccountformat(id)
		ON DELETE RESTRICT
		ON UPDATE CASCADE,
	FOREIGN KEY(job) REFERENCES job(id)
		ON DELETE RESTRICT
		ON UPDATE CASCADE
);

 /*************************************************/
/******************Tabla reserva******************/
CREATE TABLE IF NOT EXISTS booking (
	id SERIAL PRIMARY KEY,
	room INTEGER NOT NULL,
	guest INTEGER NOT NULL,
	arrivaldate DATE NOT NULL,
	departuredate DATE NOT NULL,
	totalprice 	DECIMAL NOT NULL,
	FOREIGN KEY(room) REFERENCES room(id) 
		ON DELETE CASCADE 
		ON UPDATE CASCADE,
		
	FOREIGN KEY(guest) REFERENCES guest(id)
		ON DELETE CASCADE 
		ON UPDATE CASCADE,
				
	CONSTRAINT "Departure date must be greater than arrival date" 
		CHECK (arrivaldate < departuredate),
		
	CONSTRAINT "Arrival date must be greater than or equal to current date"
		CHECK (arrivaldate >= current_date),
		
	CONSTRAINT "The total price can't be lower than 0"
		CHECK (totalprice >= 0)
);

CREATE OR REPLACE
FUNCTION check_booking_overlap() RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
		SELECT 1 FROM booking 
			WHERE room = NEW.room
			AND ((arrivaldate <= NEW.arrivaldate AND departuredate > NEW.arrivaldate)
			OR (arrivaldate < NEW.departuredate AND departuredate >= NEW.departuredate)
			OR (arrivaldate >= NEW.arrivaldate AND departuredate <= NEW.departuredate))
			AND (id IS NULL OR id <> NEW.id)
    ) THEN
        RAISE EXCEPTION 'The date range overlaps with the dates of an existing booking';
END IF;

RETURN NEW;
END;

$$ LANGUAGE plpgsql;

CREATE TRIGGER check_booking_overlap_trigger
    BEFORE INSERT OR UPDATE ON booking
    FOR EACH ROW
    EXECUTE FUNCTION check_booking_overlap();
   
-- Función para calcular el precio total
CREATE OR REPLACE FUNCTION calculate_total_price() RETURNS TRIGGER AS $$
BEGIN
	IF NEW.totalprice IS NULL THEN
		NEW.totalprice := (NEW.departuredate - NEW.arrivaldate) * (SELECT price FROM room WHERE id = NEW.room);
	END IF;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;



-- Creación del disparador para calcular el precio total antes de insertar o actualizar en la tabla "booking"
CREATE TRIGGER calculate_total_price_trigger
    BEFORE INSERT OR UPDATE ON booking
    FOR EACH ROW
    EXECUTE FUNCTION calculate_total_price();
   
   
CREATE OR REPLACE FUNCTION prevent_guest_change()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.guest <> OLD.guest THEN
        RAISE EXCEPTION 'Changing the guest is not allowed';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_guest_update
BEFORE UPDATE ON booking
FOR EACH ROW
EXECUTE FUNCTION prevent_guest_change();


CREATE OR REPLACE FUNCTION prevent_room_hotel_change()
RETURNS TRIGGER AS $$
DECLARE
    old_hotel_id INTEGER;
    new_hotel_id INTEGER;
BEGIN
    old_hotel_id := (SELECT hotel FROM room WHERE id = OLD.room);
    new_hotel_id := (SELECT hotel FROM room WHERE id = NEW.room);
    
    IF old_hotel_id <> new_hotel_id THEN
        RAISE EXCEPTION 'Changing the room to a different hotel is not allowed';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_room_hotel_update
BEFORE UPDATE ON booking
FOR EACH ROW
EXECUTE FUNCTION prevent_room_hotel_change();


   
   
