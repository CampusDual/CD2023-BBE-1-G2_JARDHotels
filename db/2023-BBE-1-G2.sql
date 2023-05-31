DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS guest;
DROP TABLE IF EXISTS room;
DROP TABLE IF EXISTS hotel;
DROP TABLE IF EXISTS country;
DROP TABLE IF EXISTS coin;

/******************Tabla hotel******************/
CREATE TABLE IF NOT EXISTS hotel(
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	stars INTEGER NOT NULL,
	address VARCHAR (200) NOT NULL,
	
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
	
	FOREIGN KEY(hotel) REFERENCES hotel(id) 
		ON DELETE CASCADE 
		ON UPDATE CASCADE,
		
	CONSTRAINT "Repeated number in hotel" 
		UNIQUE (number,hotel),
	
	CONSTRAINT "Number must be over zero" 
		CHECK(number >= 1),
	
	CONSTRAINT "Capacity must be over zero" 
		CHECK(capacity >= 1)
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

/******************Tabla moneda******************/

CREATE TABLE IF NOT EXISTS coin (
	id SERIAL PRIMARY KEY,
	coin VARCHAR(30) NOT NULL
);

/******************Tabla país******************/

CREATE TABLE IF NOT EXISTS country (
	id SERIAL PRIMARY KEY,
	country VARCHAR(60) NOT NULL,
	coin int not null,
	FOREIGN KEY(coin) REFERENCES coin(id) 
	ON DELETE CASCADE 
	ON UPDATE CASCADE
);

 /*************************************************/
/******************Tabla huésped******************/

CREATE TABLE IF NOT EXISTS guest (
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	surname VARCHAR(250) NOT NULL,
	phone VARCHAR(30) NOT NULL,
	email VARCHAR(100) NOT NULL,
	documentation VARCHAR(80) NOT NULL,
	country INTEGER NOT NULL,
	
	FOREIGN KEY(country) REFERENCES country(id)
		ON DELETE RESTRICT
		ON UPDATE CASCADE,
	
	CONSTRAINT "Repeated documentation in another guest" 
		UNIQUE (documentation),
	
	CONSTRAINT "Invalid email format" 
		CHECK (email ~* '^[A-ZA-Z0-9._%+-]+@[A-ZA-Z0-9.-]+\.[A-ZA-Z]{2,}$')
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
CREATE OR REPLACE FUNCTION verifify_phone_spain() RETURNS TRIGGER AS $$
BEGIN
  IF NEW.phone !~ '^\+34\d{9}$' THEN
    RAISE EXCEPTION 'The format for the spanish phone is incorrect. It must be +34 and 9 numbers';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_verify_phone_spain
BEFORE INSERT OR UPDATE ON guest
FOR EACH ROW
WHEN (NEW.country = 1) --Id de España debe ser 1
EXECUTE FUNCTION verifify_phone_spain();

CREATE TRIGGER trigger_verify_documentation_spain
BEFORE INSERT OR UPDATE ON guest
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
  IF NEW.phone !~ '^\\+1\\d{10}$' THEN
    RAISE EXCEPTION 'The format for the phone in United States is incorrect';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_verify_phone_united_states
BEFORE INSERT OR UPDATE ON guest
FOR EACH ROW
WHEN (NEW.country = 2) --Id de EEUU debe ser 2
EXECUTE FUNCTION verify_phone_united_states();

CREATE TRIGGER trigger_verify_documentation_united_states
BEFORE INSERT OR UPDATE ON guest
FOR EACH ROW
WHEN (NEW.country = 2) --Id de EEUU debe ser 2
EXECUTE FUNCTION verify_documentation_united_states();


 /*************************************************/
/******************Tabla reserva******************/
CREATE TABLE IF NOT EXISTS booking (
	id SERIAL PRIMARY KEY,
	room INTEGER NOT NULL,
	guest INTEGER NOT NULL,
	checkindate DATE NOT NULL,
	checkoutdate DATE NOT NULL,
	FOREIGN KEY(room) REFERENCES room(id) 
		ON DELETE CASCADE 
		ON UPDATE CASCADE,
		
	FOREIGN KEY(guest) REFERENCES guest(id)
		ON DELETE CASCADE 
		ON UPDATE CASCADE,
				
	CONSTRAINT "Check-out date must be greater than check-in date" 
		CHECK (checkindate < checkoutdate),
		
	CONSTRAINT "Check-in date must be greater than or equal to current date"
		CHECK (checkindate >= current_date)
);

CREATE OR REPLACE
FUNCTION check_booking_overlap() RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
		SELECT 1 FROM booking 
			WHERE room = NEW.room
			AND ((checkindate <= NEW.checkindate AND checkoutdate > NEW.checkindate)
			OR (checkindate < NEW.checkoutdate AND checkoutdate >= NEW.checkoutdate)
			OR (checkindate >= NEW.checkindate AND checkoutdate <= NEW.checkoutdate))
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
