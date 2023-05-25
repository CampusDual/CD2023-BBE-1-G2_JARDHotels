DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS room;
DROP TABLE IF EXISTS hotel;
DROP TABLE IF EXISTS guest;


CREATE TABLE IF NOT EXISTS hotel(
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	stars INTEGER NOT NULL,
	address VARCHAR (200) NOT NULL,
	CONSTRAINT "Stars must be between one and five" CHECK(stars >= 1 AND stars <= 5)
);

CREATE TABLE IF NOT EXISTS room(
	id SERIAL PRIMARY KEY,
	number INTEGER NOT NULL,
	capacity INTEGER NOT NULL,
	description TEXT,
	hotel INTEGER NOT NULL,
	FOREIGN KEY(hotel) REFERENCES hotel(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT "Repeated number in hotel" UNIQUE (number,hotel),
	CONSTRAINT "Number must be over zero" CHECK(number >= 1),
	CONSTRAINT "Capacity must be over zero" CHECK(capacity >= 1)
);

CREATE TABLE IF NOT EXISTS guest (
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	surname VARCHAR(250) NOT NULL,
	phone VARCHAR(12) NOT NULL,
	email VARCHAR(100) NOT NULL,
	documentation VARCHAR(80) NOT NULL,
	CONSTRAINT "Repeated documentation in another guest" UNIQUE (documentation)
);

CREATE TABLE IF NOT EXISTS booking (
	id SERIAL PRIMARY KEY,
	room INTEGER NOT NULL,
	guest INTEGER NOT NULL,
	checkindate TIMESTAMP NOT NULL,
	checkoutdate TIMESTAMP NOT NULL,
	FOREIGN KEY(room) REFERENCES room(id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY(guest) REFERENCES guest(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT "Check-out date must be greater than check-in date" CHECK (checkindate < checkoutdate),
	CONSTRAINT "Check-in date must be greater than or equal to current date"
		CHECK (checkindate >= CURRENT_DATE)
);
CREATE OR REPLACE FUNCTION check_booking_overlap()
RETURNS TRIGGER AS $$
BEGIN
	IF EXISTS (
		SELECT 1
		FROM booking B
		WHERE B.id <> NEW.id
			AND B.room = NEW.room
			AND (
				(NEW.checkindate >= B.checkindate AND NEW.checkindate < B.checkoutdate)
				OR (NEW.checkoutdate > B.checkindate AND NEW.checkoutdate <= B.checkoutdate)
				OR (NEW.checkindate <= B.checkindate AND NEW.checkoutdate >= B.checkoutdate)
			)
	) THEN
		RAISE EXCEPTION 'The date range overlaps with the dates of an existing booking';
	END IF;

	RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

CREATE TRIGGER check_booking_overlap_trigger
BEFORE INSERT OR UPDATE ON booking
FOR EACH ROW
EXECUTE FUNCTION check_booking_overlap();