DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS guest;
DROP TABLE IF EXISTS room;
DROP TABLE IF EXISTS hotel;

CREATE TABLE IF NOT EXISTS hotel(
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	stars INTEGER NOT NULL,
	address VARCHAR (200) NOT NULL,
	CONSTRAINT "Stars must be between one and five" CHECK(stars >= 1
AND stars <= 5)
);

CREATE TABLE IF NOT EXISTS room(
	id SERIAL PRIMARY KEY,
	number INTEGER NOT NULL,
	capacity INTEGER NOT NULL,
	description TEXT,
	hotel INTEGER NOT NULL,
	FOREIGN KEY(hotel) REFERENCES hotel(id) ON
DELETE
	CASCADE ON
	UPDATE
		CASCADE,
		CONSTRAINT "Repeated number in hotel" UNIQUE (number,
		hotel),
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
	CONSTRAINT "Repeated documentation in another guest" UNIQUE (documentation),
	CONSTRAINT "Invalid email format" CHECK (email ~* '^[A-ZA-Z0-9._%+-]+@[A-ZA-Z0-9.-]+\.[A-ZA-Z]{2,}$')
);

CREATE TABLE IF NOT EXISTS booking (
	id SERIAL PRIMARY KEY,
	room INTEGER NOT NULL,
	guest INTEGER NOT NULL,
	checkindate DATE NOT NULL,
	checkoutdate DATE NOT NULL,
	FOREIGN KEY(room) REFERENCES room(id) ON
DELETE
	CASCADE ON
	UPDATE
		CASCADE,
		FOREIGN KEY(guest) REFERENCES guest(id) ON
		DELETE
			CASCADE ON
			UPDATE
				CASCADE,
				CONSTRAINT "Check-out date must be greater than check-in date" CHECK (checkindate < checkoutdate),
				CONSTRAINT "Check-in date must be greater than or equal to current date"
		CHECK (checkindate >= current_date)
);

CREATE OR REPLACE
FUNCTION check_booking_overlap() RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
SELECT
	1
FROM
	booking
WHERE
	room = NEW.room
	AND (
            (checkindate <= NEW.checkindate
		AND checkoutdate > NEW.checkindate)
	OR (checkindate < NEW.checkoutdate
		AND checkoutdate >= NEW.checkoutdate)
	OR (checkindate >= NEW.checkindate
		AND checkoutdate <= NEW.checkoutdate)
        )
	AND (id IS NULL
		OR id <> NEW.id)
    ) THEN
        RAISE EXCEPTION 'The date range overlaps with the dates of an existing booking';
END IF;

RETURN NEW;
END;

$$ LANGUAGE plpgsql;

CREATE TRIGGER check_booking_overlap_trigger
    BEFORE
INSERT
	OR
UPDATE
	ON
	booking
    FOR EACH ROW
    EXECUTE FUNCTION check_booking_overlap();
