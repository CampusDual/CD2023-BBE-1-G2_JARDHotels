--
-- PostgreSQL database dump
--

-- Dumped from database version 11.16 (Debian 11.16-0+deb10u1)
-- Dumped by pg_dump version 14.2

-- Started on 2023-07-27 08:15:33

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE "2023-BBE-1-G2";
--
-- TOC entry 3156 (class 1262 OID 206203)
-- Name: 2023-BBE-1-G2; Type: DATABASE; Schema: -; Owner: -
--

CREATE DATABASE "2023-BBE-1-G2" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'en_US.UTF-8';


\connect -reuse-previous=on "dbname='2023-BBE-1-G2'"

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA public;


--
-- TOC entry 3157 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- TOC entry 245 (class 1255 OID 216792)
-- Name: calculate_distance(numeric, numeric); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.calculate_distance(lat numeric, lon numeric) RETURNS TABLE(id integer, name character varying, stars integer, address character varying, country integer, latitude numeric, longitude numeric, distance double precision)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT *,
        2 * 6371 * ASIN(
            SQRT(
                POWER(SIN((RADIANS(h.latitude - lat)) / 2), 2) +
                COS(RADIANS(lat)) * COS(RADIANS(h.latitude)) *
                POWER(SIN((RADIANS(h.longitude - lon)) / 2), 2)
            )
        ) AS distance
    FROM hotel h
    ORDER BY distance;
END;
$$;


--
-- TOC entry 262 (class 1255 OID 213424)
-- Name: calculate_total_price(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.calculate_total_price() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	IF NEW.totalprice IS NULL THEN
		NEW.totalprice := (NEW.departuredate - NEW.arrivaldate) * (SELECT price FROM room WHERE id = NEW.room);
	END IF;
	RETURN NEW;
END;
$$;


--
-- TOC entry 261 (class 1255 OID 207273)
-- Name: check_booking_overlap(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.check_booking_overlap() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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

$$;


--
-- TOC entry 240 (class 1255 OID 209134)
-- Name: check_hotel_from_room(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.check_hotel_from_room() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.hotel <> OLD.hotel THEN
        RAISE EXCEPTION 'Change the hotel of a room is not allowed';
    END IF;
    
    RETURN NEW;
END;
$$;


--
-- TOC entry 247 (class 1255 OID 215456)
-- Name: prevent_guest_change(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.prevent_guest_change() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.guest <> OLD.guest THEN
        RAISE EXCEPTION 'Changing the guest is not allowed';
    END IF;
    RETURN NEW;
END;
$$;


--
-- TOC entry 246 (class 1255 OID 218749)
-- Name: prevent_person_change(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.prevent_person_change() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.person <> OLD.person THEN
        RAISE EXCEPTION 'Changing the person is not allowed';
    END IF;
    RETURN NEW;
END;
$$;


--
-- TOC entry 248 (class 1255 OID 215458)
-- Name: prevent_room_hotel_change(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.prevent_room_hotel_change() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
$$;


--
-- TOC entry 239 (class 1255 OID 210249)
-- Name: verify_documentation(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_documentation() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  IF NEW.country IS NOT NULL THEN
    DECLARE
      country_name VARCHAR(60);
    BEGIN
      SELECT country INTO country_name FROM country WHERE id = NEW.country;
      IF country_name = 'Spain' THEN
        PERFORM verify_documentation_spain();
      ELSIF country_name = 'United States' THEN
        PERFORM verify_documentation_united_states();
      END IF;
    END;
  END IF;
  
  RETURN NEW;
END;
$$;


--
-- TOC entry 260 (class 1255 OID 214216)
-- Name: verify_documentation_china(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_documentation_china() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
DECLARE
  birthdate DATE;
BEGIN
  IF NEW.documentation !~ '^[1-9]\d{5}(?:19|20)\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01])\d{3}[\dX]$' THEN
    RAISE EXCEPTION 'The format for the documentation in China is incorrect';
  END IF;

  RETURN NEW;
END;
$_$;


--
-- TOC entry 256 (class 1255 OID 214204)
-- Name: verify_documentation_france(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_documentation_france() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
BEGIN
  IF NEW.documentation !~ '^(?!00000)[0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[1-2][0-9]|3[0-1])(?:[0-9]{3})(?:(?:[0-8][0-9])|(?:9[0-7]))[0-9]{3}[0-9]{2}$' THEN
    RAISE EXCEPTION 'The format for the card in France is incorrect';
  END IF;

  RETURN NEW;
END;
$_$;


--
-- TOC entry 258 (class 1255 OID 214208)
-- Name: verify_documentation_germany(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_documentation_germany() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
BEGIN
  IF NEW.documentation !~ '^[A-Z]{2}\d{7}$' THEN
    RAISE EXCEPTION 'The format for the documentation in Germany is incorrect';
  END IF;

  RETURN NEW;
END;
$_$;


--
-- TOC entry 244 (class 1255 OID 211418)
-- Name: verify_documentation_phone_spain(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_documentation_phone_spain() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	PERFORM verify_phone_spain();
  	RETURN verify_documentation_spain();
END;
$$;


--
-- TOC entry 243 (class 1255 OID 211421)
-- Name: verify_documentation_phone_united_states(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_documentation_phone_united_states() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  PERFORM verify_phone_united_states();
  RETURN verify_documentation_united_states();
END;
$$;


--
-- TOC entry 252 (class 1255 OID 214212)
-- Name: verify_documentation_portugal(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_documentation_portugal() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
BEGIN
  IF NEW.documentation !~ '^\d{9}$' THEN
    RAISE EXCEPTION 'The format for the documentation in Portugal is incorrect';
  END IF;

  RETURN NEW;
END;
$_$;


--
-- TOC entry 251 (class 1255 OID 210247)
-- Name: verify_documentation_spain(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_documentation_spain() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
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
$_$;


--
-- TOC entry 249 (class 1255 OID 214200)
-- Name: verify_documentation_united_kingdom(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_documentation_united_kingdom() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
BEGIN
  IF NEW.documentation !~ '^(?!^0+$)[a-zA-Z0-9]{9}$|^(?!^0+$)[a-zA-Z0-9]{10}$' THEN
    RAISE EXCEPTION 'The format for the passport in United Kingdom is incorrect';
  END IF;

  RETURN NEW;
END;
$_$;


--
-- TOC entry 242 (class 1255 OID 210248)
-- Name: verify_documentation_united_states(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_documentation_united_states() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
BEGIN
  IF NEW.documentation !~ '^\d{9}$' THEN
    RAISE EXCEPTION 'El format for the passport in United States is incorrect';
  END IF;

  RETURN NEW;
END;
$_$;


--
-- TOC entry 254 (class 1255 OID 214217)
-- Name: verify_phone_china(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_phone_china() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
BEGIN
  IF NEW.phone !~ '\d{9}$' THEN
    RAISE EXCEPTION 'The format for the phone in China is incorrect';
  END IF;

  RETURN NEW;
END;
$_$;


--
-- TOC entry 257 (class 1255 OID 214205)
-- Name: verify_phone_france(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_phone_france() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
BEGIN
  IF NEW.phone !~ '\d{9}$' THEN
    RAISE EXCEPTION 'The format for the phone in France is incorrect';
  END IF;

  RETURN NEW;
END;
$_$;


--
-- TOC entry 259 (class 1255 OID 214209)
-- Name: verify_phone_germany(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_phone_germany() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
BEGIN
  IF NEW.phone !~ '[1-9]\d{1,4}\d{1,10}$' THEN
    RAISE EXCEPTION 'The format for the phone in Germany is incorrect';
  END IF;

  RETURN NEW;
END;
$_$;


--
-- TOC entry 253 (class 1255 OID 214213)
-- Name: verify_phone_portugal(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_phone_portugal() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
BEGIN
  IF NEW.phone !~ '[28969]\d{8}$' THEN
    RAISE EXCEPTION 'The format for the phone in Portugal is incorrect';
  END IF;

  RETURN NEW;
END;
$_$;


--
-- TOC entry 241 (class 1255 OID 211515)
-- Name: verify_phone_spain(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_phone_spain() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
BEGIN
  IF NEW.phone !~ '\d{9}$' THEN
    RAISE EXCEPTION 'The format for the spanish phone is incorrect. It must be +34 and 9 numbers';
  END IF;

  RETURN NEW;
END;
$_$;


--
-- TOC entry 255 (class 1255 OID 214201)
-- Name: verify_phone_united_kingdom(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_phone_united_kingdom() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
BEGIN
  IF NEW.phone !~ '[1-9]\d{9}$' THEN
    RAISE EXCEPTION 'The format for the phone in United Kingdom is incorrect';
  END IF;

  RETURN NEW;
END;
$_$;


--
-- TOC entry 250 (class 1255 OID 211420)
-- Name: verify_phone_united_states(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.verify_phone_united_states() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
BEGIN
  IF NEW.phone !~ '\d{10}$' THEN
    RAISE EXCEPTION 'The format for the phone in United States is incorrect';
  END IF;

  RETURN NEW;
END;
$_$;


SET default_tablespace = '';

--
-- TOC entry 217 (class 1259 OID 286888)
-- Name: bankaccountformat; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.bankaccountformat (
    id integer NOT NULL,
    format character varying(20) NOT NULL
);


--
-- TOC entry 216 (class 1259 OID 286886)
-- Name: bankaccountformat_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.bankaccountformat_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3158 (class 0 OID 0)
-- Dependencies: 216
-- Name: bankaccountformat_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.bankaccountformat_id_seq OWNED BY public.bankaccountformat.id;


--
-- TOC entry 226 (class 1259 OID 286955)
-- Name: booking; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.booking (
    id integer NOT NULL,
    room integer NOT NULL,
    guest integer NOT NULL,
    arrivaldate date NOT NULL,
    departuredate date NOT NULL,
    totalprice numeric NOT NULL,
    checkindate timestamp without time zone,
    checkoutdate timestamp without time zone,
    code character varying(40) NOT NULL,
    CONSTRAINT "Departure date must be greater than arrival date" CHECK ((arrivaldate < departuredate)),
    CONSTRAINT "The total price can't be lower than 0" CHECK ((totalprice >= (0)::numeric))
);


--
-- TOC entry 225 (class 1259 OID 286953)
-- Name: booking_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.booking_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3159 (class 0 OID 0)
-- Dependencies: 225
-- Name: booking_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.booking_id_seq OWNED BY public.booking.id;


--
-- TOC entry 197 (class 1259 OID 286709)
-- Name: coin; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.coin (
    id integer NOT NULL,
    coin character varying(30) NOT NULL
);


--
-- TOC entry 196 (class 1259 OID 286707)
-- Name: coin_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.coin_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3160 (class 0 OID 0)
-- Dependencies: 196
-- Name: coin_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.coin_id_seq OWNED BY public.coin.id;


--
-- TOC entry 199 (class 1259 OID 286717)
-- Name: country; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.country (
    id integer NOT NULL,
    country character varying(60) NOT NULL,
    coin integer NOT NULL
);


--
-- TOC entry 198 (class 1259 OID 286715)
-- Name: country_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.country_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3161 (class 0 OID 0)
-- Dependencies: 198
-- Name: country_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.country_id_seq OWNED BY public.country.id;


--
-- TOC entry 215 (class 1259 OID 286876)
-- Name: guest; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.guest (
    id integer NOT NULL
);


--
-- TOC entry 201 (class 1259 OID 286730)
-- Name: hotel; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.hotel (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    stars integer NOT NULL,
    address character varying(200) NOT NULL,
    country integer NOT NULL,
    latitude numeric(9,7) NOT NULL,
    longitude numeric(10,7) NOT NULL,
    phone character varying(30) NOT NULL,
    CONSTRAINT "Stars must be between one and five" CHECK (((stars >= 1) AND (stars <= 5)))
);


--
-- TOC entry 200 (class 1259 OID 286728)
-- Name: hotel_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.hotel_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3162 (class 0 OID 0)
-- Dependencies: 200
-- Name: hotel_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.hotel_id_seq OWNED BY public.hotel.id;


--
-- TOC entry 219 (class 1259 OID 286896)
-- Name: job; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.job (
    id integer NOT NULL,
    job character varying(40) NOT NULL
);


--
-- TOC entry 218 (class 1259 OID 286894)
-- Name: job_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.job_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3163 (class 0 OID 0)
-- Dependencies: 218
-- Name: job_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.job_id_seq OWNED BY public.job.id;


--
-- TOC entry 222 (class 1259 OID 286929)
-- Name: menu; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.menu (
    id integer NOT NULL,
    name character varying(50) NOT NULL
);


--
-- TOC entry 221 (class 1259 OID 286927)
-- Name: menu_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.menu_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3164 (class 0 OID 0)
-- Dependencies: 221
-- Name: menu_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.menu_id_seq OWNED BY public.menu.id;


--
-- TOC entry 224 (class 1259 OID 286937)
-- Name: pantry; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pantry (
    id integer NOT NULL,
    idmenu integer NOT NULL,
    idhotel integer NOT NULL,
    amount integer NOT NULL
);


--
-- TOC entry 223 (class 1259 OID 286935)
-- Name: pantry_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.pantry_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3165 (class 0 OID 0)
-- Dependencies: 223
-- Name: pantry_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.pantry_id_seq OWNED BY public.pantry.id;


--
-- TOC entry 205 (class 1259 OID 286768)
-- Name: person; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.person (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    surname character varying(250) NOT NULL,
    phone character varying(30) NOT NULL,
    documentation character varying(80) NOT NULL,
    country integer NOT NULL,
    phonecountry integer NOT NULL
);


--
-- TOC entry 204 (class 1259 OID 286766)
-- Name: person_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.person_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3166 (class 0 OID 0)
-- Dependencies: 204
-- Name: person_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.person_id_seq OWNED BY public.person.id;


--
-- TOC entry 203 (class 1259 OID 286746)
-- Name: room; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.room (
    id integer NOT NULL,
    number integer NOT NULL,
    capacity integer NOT NULL,
    description text,
    hotel integer NOT NULL,
    price numeric NOT NULL,
    CONSTRAINT "Capacity must be over zero" CHECK ((capacity >= 1)),
    CONSTRAINT "Number must be over zero" CHECK ((number >= 1)),
    CONSTRAINT "The price must be greater than 0" CHECK ((price > (0)::numeric))
);


--
-- TOC entry 202 (class 1259 OID 286744)
-- Name: room_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.room_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3167 (class 0 OID 0)
-- Dependencies: 202
-- Name: room_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.room_id_seq OWNED BY public.room.id;


--
-- TOC entry 220 (class 1259 OID 286902)
-- Name: staff; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.staff (
    id integer NOT NULL,
    bankaccount character varying(50) NOT NULL,
    bankaccountformat integer NOT NULL,
    salary numeric(8,2) NOT NULL,
    job integer NOT NULL,
    idhotel integer NOT NULL
);


--
-- TOC entry 208 (class 1259 OID 286818)
-- Name: trole; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.trole (
    id integer NOT NULL,
    rolename character varying(255) NOT NULL,
    xmlclientpermission character varying(10485760) NOT NULL
);


--
-- TOC entry 207 (class 1259 OID 286816)
-- Name: trole_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.trole_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3168 (class 0 OID 0)
-- Dependencies: 207
-- Name: trole_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.trole_id_seq OWNED BY public.trole.id;


--
-- TOC entry 214 (class 1259 OID 286860)
-- Name: trole_server_permission; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.trole_server_permission (
    id integer NOT NULL,
    id_rolename integer NOT NULL,
    id_server_permission integer NOT NULL
);


--
-- TOC entry 213 (class 1259 OID 286858)
-- Name: trole_server_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.trole_server_permission_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3169 (class 0 OID 0)
-- Dependencies: 213
-- Name: trole_server_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.trole_server_permission_id_seq OWNED BY public.trole_server_permission.id;


--
-- TOC entry 212 (class 1259 OID 286849)
-- Name: tserver_permission; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tserver_permission (
    id integer NOT NULL,
    permission_name character varying(10485760)
);


--
-- TOC entry 211 (class 1259 OID 286847)
-- Name: tserver_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.tserver_permission_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3170 (class 0 OID 0)
-- Dependencies: 211
-- Name: tserver_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.tserver_permission_id_seq OWNED BY public.tserver_permission.id;


--
-- TOC entry 206 (class 1259 OID 286800)
-- Name: tuser; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tuser (
    username character varying(50) NOT NULL,
    password character varying(255) NOT NULL,
    email character varying(100) NOT NULL,
    lastpasswordupdate timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    firstlogin boolean DEFAULT true,
    userbloqued boolean DEFAULT false,
    idperson integer NOT NULL,
    CONSTRAINT "Invalid email format" CHECK (((email)::text ~* '^[A-ZA-Z0-9._%+-]+@[A-ZA-Z0-9.-]+\.[A-ZA-Z]{2,}$'::text))
);


--
-- TOC entry 210 (class 1259 OID 286829)
-- Name: tuser_role; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tuser_role (
    id integer NOT NULL,
    id_role integer NOT NULL,
    user_name character varying(50) NOT NULL
);


--
-- TOC entry 209 (class 1259 OID 286827)
-- Name: tuser_role_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.tuser_role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3171 (class 0 OID 0)
-- Dependencies: 209
-- Name: tuser_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.tuser_role_id_seq OWNED BY public.tuser_role.id;


--
-- TOC entry 2910 (class 2604 OID 286891)
-- Name: bankaccountformat id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bankaccountformat ALTER COLUMN id SET DEFAULT nextval('public.bankaccountformat_id_seq'::regclass);


--
-- TOC entry 2914 (class 2604 OID 286958)
-- Name: booking id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.booking ALTER COLUMN id SET DEFAULT nextval('public.booking_id_seq'::regclass);


--
-- TOC entry 2893 (class 2604 OID 286712)
-- Name: coin id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.coin ALTER COLUMN id SET DEFAULT nextval('public.coin_id_seq'::regclass);


--
-- TOC entry 2894 (class 2604 OID 286720)
-- Name: country id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.country ALTER COLUMN id SET DEFAULT nextval('public.country_id_seq'::regclass);


--
-- TOC entry 2895 (class 2604 OID 286733)
-- Name: hotel id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hotel ALTER COLUMN id SET DEFAULT nextval('public.hotel_id_seq'::regclass);


--
-- TOC entry 2911 (class 2604 OID 286899)
-- Name: job id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.job ALTER COLUMN id SET DEFAULT nextval('public.job_id_seq'::regclass);


--
-- TOC entry 2912 (class 2604 OID 286932)
-- Name: menu id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.menu ALTER COLUMN id SET DEFAULT nextval('public.menu_id_seq'::regclass);


--
-- TOC entry 2913 (class 2604 OID 286940)
-- Name: pantry id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pantry ALTER COLUMN id SET DEFAULT nextval('public.pantry_id_seq'::regclass);


--
-- TOC entry 2901 (class 2604 OID 286771)
-- Name: person id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.person ALTER COLUMN id SET DEFAULT nextval('public.person_id_seq'::regclass);


--
-- TOC entry 2897 (class 2604 OID 286749)
-- Name: room id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.room ALTER COLUMN id SET DEFAULT nextval('public.room_id_seq'::regclass);


--
-- TOC entry 2906 (class 2604 OID 286821)
-- Name: trole id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trole ALTER COLUMN id SET DEFAULT nextval('public.trole_id_seq'::regclass);


--
-- TOC entry 2909 (class 2604 OID 286863)
-- Name: trole_server_permission id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trole_server_permission ALTER COLUMN id SET DEFAULT nextval('public.trole_server_permission_id_seq'::regclass);


--
-- TOC entry 2908 (class 2604 OID 286852)
-- Name: tserver_permission id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tserver_permission ALTER COLUMN id SET DEFAULT nextval('public.tserver_permission_id_seq'::regclass);


--
-- TOC entry 2907 (class 2604 OID 286832)
-- Name: tuser_role id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuser_role ALTER COLUMN id SET DEFAULT nextval('public.tuser_role_id_seq'::regclass);


--
-- TOC entry 3141 (class 0 OID 286888)
-- Dependencies: 217
-- Data for Name: bankaccountformat; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.bankaccountformat VALUES (1, 'IBAN');
INSERT INTO public.bankaccountformat VALUES (2, 'CCC');
INSERT INTO public.bankaccountformat VALUES (3, 'Sort Code');
INSERT INTO public.bankaccountformat VALUES (4, 'ABA');
INSERT INTO public.bankaccountformat VALUES (5, 'Other');


--
-- TOC entry 3150 (class 0 OID 286955)
-- Dependencies: 226
-- Data for Name: booking; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.booking VALUES (1, 3, 5, '2024-06-10', '2024-06-15', 600, NULL, NULL, '3-5-2024-06-10-95134');
INSERT INTO public.booking VALUES (2, 2, 6, '2024-07-20', '2024-07-25', 250, NULL, NULL, '2-6-2024-07-20-71739');
INSERT INTO public.booking VALUES (3, 7, 10, '2024-11-25', '2024-11-30', 125, NULL, NULL, '7-10-2024-11-25-32760');
INSERT INTO public.booking VALUES (4, 6, 3, '2025-03-15', '2025-03-20', 375, NULL, NULL, '6-3-2025-03-15-09528');
INSERT INTO public.booking VALUES (5, 9, 8, '2025-06-10', '2025-06-15', 1000, NULL, NULL, '9-8-2025-06-10-98765');
INSERT INTO public.booking VALUES (6, 11, 3, '2023-01-01', '2023-01-10', 1800, NULL, NULL, '11-3-2023-01-01-52964');
INSERT INTO public.booking VALUES (7, 12, 3, '2023-02-02', '2023-02-20', 1800, NULL, NULL, '11-3-2023-02-02-23525');
INSERT INTO public.booking VALUES (8, 13, 3, '2023-03-03', '2023-03-04', 250, NULL, NULL, '11-3-2023-03-03-82746');
INSERT INTO public.booking VALUES (9, 14, 3, '2023-04-04', '2023-04-07', 225, NULL, NULL, '11-3-2023-04-04-94757');
INSERT INTO public.booking VALUES (10, 15, 3, '2023-05-05', '2023-05-11', 720, NULL, NULL, '11-3-2023-05-05-56546');
INSERT INTO public.booking VALUES (11, 11, 3, '2023-06-06', '2023-06-12', 1200, NULL, NULL, '11-3-2023-06-06-34563');
INSERT INTO public.booking VALUES (12, 12, 3, '2023-07-10', '2023-07-29', 1900, NULL, NULL, '11-3-2023-07-07-63467');
INSERT INTO public.booking VALUES (13, 8, 12, '2023-07-26', '2023-07-30', 300.0, NULL, NULL, '8-12-2023-07-26-47247');
INSERT INTO public.booking VALUES (14, 7, 2, '2023-07-26', '2023-07-30', 100.0, NULL, NULL, '7-2-2023-07-26-86627');


--
-- TOC entry 3121 (class 0 OID 286709)
-- Dependencies: 197
-- Data for Name: coin; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.coin VALUES (1, '€');
INSERT INTO public.coin VALUES (2, '$');
INSERT INTO public.coin VALUES (3, 'Other');


--
-- TOC entry 3123 (class 0 OID 286717)
-- Dependencies: 199
-- Data for Name: country; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.country VALUES (1, 'Spain', 1);
INSERT INTO public.country VALUES (2, 'United States', 2);
INSERT INTO public.country VALUES (3, 'United Kingdom', 3);
INSERT INTO public.country VALUES (4, 'France', 1);
INSERT INTO public.country VALUES (5, 'Germany', 1);
INSERT INTO public.country VALUES (6, 'Portugal', 1);
INSERT INTO public.country VALUES (7, 'China', 3);
INSERT INTO public.country VALUES (8, 'Other', 3);


--
-- TOC entry 3139 (class 0 OID 286876)
-- Dependencies: 215
-- Data for Name: guest; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.guest VALUES (2);
INSERT INTO public.guest VALUES (3);
INSERT INTO public.guest VALUES (5);
INSERT INTO public.guest VALUES (6);
INSERT INTO public.guest VALUES (8);
INSERT INTO public.guest VALUES (10);
INSERT INTO public.guest VALUES (12);


--
-- TOC entry 3125 (class 0 OID 286730)
-- Dependencies: 201
-- Data for Name: hotel; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.hotel VALUES (1, 'Hotel Grand Hyatt', 5, '123 Main Street, Cityville', 3, 40.7128000, -74.0060000, '9121212121');
INSERT INTO public.hotel VALUES (2, 'Hotel Hilton', 4, '456 Elm Avenue, Townsville', 7, 51.5074000, -0.1278000, '775546543');
INSERT INTO public.hotel VALUES (3, 'Hotel Marriott', 4, '789 Oak Lane, Villageland', 4, 39.9526000, -75.1652000, '748396655');
INSERT INTO public.hotel VALUES (4, 'Hotel Radisson Deluxe', 3, '321 Pine Road, Countryside', 2, 41.8781000, -87.6298000, '112233445');
INSERT INTO public.hotel VALUES (5, 'Hotel Sheraton', 4, '987 Cedar Drive, Mountainview', 5, 37.3861000, -122.0839000, '12345678901234567890');
INSERT INTO public.hotel VALUES (6, 'Hotel Holiday Inn', 3, '654 Maple Court, Lakeside', 6, 45.4215000, -75.6906000, '688899944');
INSERT INTO public.hotel VALUES (7, 'Hotel Four Seasons', 5, '876 Birch Street, Beachtown', 4, 40.7128000, -74.0060000, '001100223');
INSERT INTO public.hotel VALUES (8, 'Hotel Ritz-Carlton Deluxe', 5, '543 Walnut Circle, Seaside', 1, 25.7617000, -80.1918000, '9998887776');
INSERT INTO public.hotel VALUES (9, 'Hotel Best Western', 3, '210 Spruce Avenue, Hillside', 3, 40.7128000, -74.0060000, '5728192836');
INSERT INTO public.hotel VALUES (10, 'Hotel Ibis', 2, '135 Oakwood Lane, Riverside', 8, 34.0522000, -118.2437000, '01912718919');


--
-- TOC entry 3143 (class 0 OID 286896)
-- Dependencies: 219
-- Data for Name: job; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.job VALUES (1, 'cleaning service');
INSERT INTO public.job VALUES (2, 'bellhop');
INSERT INTO public.job VALUES (3, 'recepcionist');
INSERT INTO public.job VALUES (4, 'cooker');
INSERT INTO public.job VALUES (5, 'waiter');
INSERT INTO public.job VALUES (6, 'lifeguard');
INSERT INTO public.job VALUES (7, 'masseuse');
INSERT INTO public.job VALUES (8, 'room service');
INSERT INTO public.job VALUES (9, 'maintenance');
INSERT INTO public.job VALUES (10, 'hotel manager');


--
-- TOC entry 3146 (class 0 OID 286929)
-- Dependencies: 222
-- Data for Name: menu; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.menu VALUES (1, 'Champagne');
INSERT INTO public.menu VALUES (2, 'Croissant');
INSERT INTO public.menu VALUES (3, 'Fruit Platter');
INSERT INTO public.menu VALUES (4, 'Omelette');
INSERT INTO public.menu VALUES (5, 'Yogurt');
INSERT INTO public.menu VALUES (6, 'Coffee');
INSERT INTO public.menu VALUES (7, 'Tea');
INSERT INTO public.menu VALUES (8, 'Cheese Platter');
INSERT INTO public.menu VALUES (9, 'Sandwich');
INSERT INTO public.menu VALUES (10, 'Pasta Salad');


--
-- TOC entry 3148 (class 0 OID 286937)
-- Dependencies: 224
-- Data for Name: pantry; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.pantry VALUES (1, 1, 1, 5);
INSERT INTO public.pantry VALUES (2, 2, 1, 10);
INSERT INTO public.pantry VALUES (3, 3, 1, 3);
INSERT INTO public.pantry VALUES (4, 4, 1, 6);
INSERT INTO public.pantry VALUES (5, 6, 1, 20);
INSERT INTO public.pantry VALUES (6, 7, 1, 15);
INSERT INTO public.pantry VALUES (7, 8, 1, 4);
INSERT INTO public.pantry VALUES (8, 9, 1, 12);
INSERT INTO public.pantry VALUES (9, 10, 1, 8);
INSERT INTO public.pantry VALUES (10, 1, 2, 3);
INSERT INTO public.pantry VALUES (11, 2, 2, 8);
INSERT INTO public.pantry VALUES (12, 3, 2, 5);
INSERT INTO public.pantry VALUES (13, 4, 2, 10);
INSERT INTO public.pantry VALUES (14, 6, 2, 25);
INSERT INTO public.pantry VALUES (15, 7, 2, 20);
INSERT INTO public.pantry VALUES (16, 8, 2, 6);
INSERT INTO public.pantry VALUES (17, 9, 2, 15);
INSERT INTO public.pantry VALUES (18, 10, 2, 10);
INSERT INTO public.pantry VALUES (19, 1, 3, 6);
INSERT INTO public.pantry VALUES (20, 2, 3, 12);
INSERT INTO public.pantry VALUES (21, 3, 3, 8);
INSERT INTO public.pantry VALUES (22, 4, 3, 15);
INSERT INTO public.pantry VALUES (23, 6, 3, 30);
INSERT INTO public.pantry VALUES (24, 7, 3, 25);
INSERT INTO public.pantry VALUES (25, 8, 3, 8);
INSERT INTO public.pantry VALUES (26, 9, 3, 20);
INSERT INTO public.pantry VALUES (27, 10, 3, 15);
INSERT INTO public.pantry VALUES (28, 1, 4, 4);
INSERT INTO public.pantry VALUES (29, 2, 4, 10);
INSERT INTO public.pantry VALUES (30, 3, 4, 6);
INSERT INTO public.pantry VALUES (31, 4, 4, 12);
INSERT INTO public.pantry VALUES (32, 6, 4, 20);
INSERT INTO public.pantry VALUES (33, 7, 4, 18);
INSERT INTO public.pantry VALUES (34, 8, 4, 5);
INSERT INTO public.pantry VALUES (35, 9, 4, 12);
INSERT INTO public.pantry VALUES (36, 10, 4, 10);
INSERT INTO public.pantry VALUES (37, 1, 5, 7);
INSERT INTO public.pantry VALUES (38, 2, 5, 15);
INSERT INTO public.pantry VALUES (39, 3, 5, 10);
INSERT INTO public.pantry VALUES (40, 4, 5, 20);
INSERT INTO public.pantry VALUES (41, 6, 5, 35);
INSERT INTO public.pantry VALUES (42, 7, 5, 30);
INSERT INTO public.pantry VALUES (43, 8, 5, 10);
INSERT INTO public.pantry VALUES (44, 9, 5, 25);
INSERT INTO public.pantry VALUES (45, 10, 5, 20);
INSERT INTO public.pantry VALUES (46, 1, 6, 5);
INSERT INTO public.pantry VALUES (47, 2, 6, 12);
INSERT INTO public.pantry VALUES (48, 3, 6, 7);
INSERT INTO public.pantry VALUES (49, 4, 6, 14);
INSERT INTO public.pantry VALUES (50, 6, 6, 25);
INSERT INTO public.pantry VALUES (51, 7, 6, 22);
INSERT INTO public.pantry VALUES (52, 8, 6, 6);
INSERT INTO public.pantry VALUES (53, 9, 6, 18);
INSERT INTO public.pantry VALUES (54, 10, 6, 15);
INSERT INTO public.pantry VALUES (55, 1, 7, 3);
INSERT INTO public.pantry VALUES (56, 2, 7, 8);
INSERT INTO public.pantry VALUES (57, 3, 7, 5);
INSERT INTO public.pantry VALUES (58, 4, 7, 10);
INSERT INTO public.pantry VALUES (59, 6, 7, 20);
INSERT INTO public.pantry VALUES (60, 7, 7, 18);
INSERT INTO public.pantry VALUES (61, 8, 7, 4);
INSERT INTO public.pantry VALUES (62, 9, 7, 12);
INSERT INTO public.pantry VALUES (63, 10, 7, 10);
INSERT INTO public.pantry VALUES (64, 1, 8, 6);
INSERT INTO public.pantry VALUES (65, 2, 8, 12);
INSERT INTO public.pantry VALUES (66, 3, 8, 8);
INSERT INTO public.pantry VALUES (67, 4, 8, 15);
INSERT INTO public.pantry VALUES (68, 6, 8, 30);
INSERT INTO public.pantry VALUES (69, 7, 8, 25);
INSERT INTO public.pantry VALUES (70, 8, 8, 8);
INSERT INTO public.pantry VALUES (71, 9, 8, 20);
INSERT INTO public.pantry VALUES (72, 10, 8, 15);
INSERT INTO public.pantry VALUES (73, 1, 9, 4);
INSERT INTO public.pantry VALUES (74, 2, 9, 10);
INSERT INTO public.pantry VALUES (75, 3, 9, 6);
INSERT INTO public.pantry VALUES (76, 4, 9, 12);
INSERT INTO public.pantry VALUES (77, 6, 9, 20);
INSERT INTO public.pantry VALUES (78, 7, 9, 18);
INSERT INTO public.pantry VALUES (79, 8, 9, 5);
INSERT INTO public.pantry VALUES (80, 9, 9, 12);
INSERT INTO public.pantry VALUES (81, 10, 9, 10);
INSERT INTO public.pantry VALUES (83, 2, 10, 15);
INSERT INTO public.pantry VALUES (84, 3, 10, 10);
INSERT INTO public.pantry VALUES (85, 4, 10, 20);
INSERT INTO public.pantry VALUES (86, 6, 10, 35);
INSERT INTO public.pantry VALUES (87, 7, 10, 30);
INSERT INTO public.pantry VALUES (88, 8, 10, 10);
INSERT INTO public.pantry VALUES (89, 9, 10, 25);
INSERT INTO public.pantry VALUES (90, 10, 10, 20);
INSERT INTO public.pantry VALUES (82, 1, 10, 107);


--
-- TOC entry 3129 (class 0 OID 286768)
-- Dependencies: 205
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.person VALUES (1, 'Francisco', 'Gómez', '123456789', '12345678Z', 1, 1);
INSERT INTO public.person VALUES (2, 'María', 'Rodríguez', '16012345', 'XY9876543', 5, 5);
INSERT INTO public.person VALUES (3, 'Antonio', 'López', '1234567891', '111222333', 2, 2);
INSERT INTO public.person VALUES (4, 'Carmen', 'García', '111222333', '76543210D', 8, 8);
INSERT INTO public.person VALUES (5, 'Manuel', 'Martínez', '987654321', '987654198512311234', 7, 7);
INSERT INTO public.person VALUES (6, 'Laura', 'Fernández', '987654321', '9305078912345678', 4, 4);
INSERT INTO public.person VALUES (7, 'Pedro', 'Navarro', '289876543', '987654321', 6, 6);
INSERT INTO public.person VALUES (8, 'Isabel', 'Sánchez', '987987987', '87654321X', 1, 1);
INSERT INTO public.person VALUES (9, 'José', 'Romero', '888888888', '56789012I', 8, 8);
INSERT INTO public.person VALUES (10, 'Ana', 'Jiménez', '7876543210', 'G12345678', 3, 3);
INSERT INTO public.person VALUES (-1, 'Admin', 'Admin', 'Admin', 'Admin', 8, 8);
INSERT INTO public.person VALUES (-2, 'Demo', 'Demo', 'Demo', 'Demo', 8, 8);
INSERT INTO public.person VALUES (12, 'Marcos', 'Fernández', '+447975777666', 'AC1234567', 3, 3);


--
-- TOC entry 3127 (class 0 OID 286746)
-- Dependencies: 203
-- Data for Name: room; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.room VALUES (1, 101, 2, 'Standard Room with a queen bed', 1, 50);
INSERT INTO public.room VALUES (2, 102, 2, 'Standard Room with a queen bed', 1, 50);
INSERT INTO public.room VALUES (3, 201, 4, 'Family Suite with two queen beds', 2, 120);
INSERT INTO public.room VALUES (4, 202, 4, 'Family Suite with two queen beds', 2, 120);
INSERT INTO public.room VALUES (5, 301, 1, 'Single Room with a twin bed', 3, 75);
INSERT INTO public.room VALUES (6, 302, 1, 'Single Room with a twin bed', 3, 75);
INSERT INTO public.room VALUES (7, 401, 4, 'Double Room with two double beds', 4, 25);
INSERT INTO public.room VALUES (8, 402, 2, 'Double Room with two small beds', 4, 75);
INSERT INTO public.room VALUES (9, 501, 2, 'Deluxe Room with a king bed and ocean view', 5, 200);
INSERT INTO public.room VALUES (10, 502, 2, 'Deluxe Room with a king bed and ocean view', 5, 200);
INSERT INTO public.room VALUES (11, 200, 2, 'Deluxe Room with a king bed and ocean view', 10, 200);
INSERT INTO public.room VALUES (12, 201, 3, 'Triple Room with three twin beds', 10, 100);
INSERT INTO public.room VALUES (13, 202, 4, 'Family Suite with two king beds and garden view', 10, 250);
INSERT INTO public.room VALUES (14, 203, 1, 'Single Room with a twin bed', 10, 75);
INSERT INTO public.room VALUES (15, 204, 2, 'Double Room with a queen bed and city view', 10, 120);
INSERT INTO public.room VALUES (16, 205, 2, 'Deluxe Room with a king bed and a view of the park', 10, 200);


--
-- TOC entry 3144 (class 0 OID 286902)
-- Dependencies: 220
-- Data for Name: staff; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.staff VALUES (1, 'ES6600190020961234567890', 1, 2500.00, 3, 1);
INSERT INTO public.staff VALUES (4, 'DE89370400440532013000', 1, 3200.00, 4, 2);
INSERT INTO public.staff VALUES (6, '6789678967896789678967', 2, 2600.00, 3, 4);
INSERT INTO public.staff VALUES (7, '789012', 3, 2900.00, 7, 6);
INSERT INTO public.staff VALUES (8, '901234567', 5, 3100.00, 8, 7);
INSERT INTO public.staff VALUES (9, '123456789', 4, 2700.00, 9, 8);
INSERT INTO public.staff VALUES (10, 'GB29NWBK60161331926819', 1, 3500.00, 10, 10);


--
-- TOC entry 3132 (class 0 OID 286818)
-- Dependencies: 208
-- Data for Name: trole; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.trole VALUES (1, 'guest', '<?xml version="1.0" encoding="UTF-8"?><security></security>');
INSERT INTO public.trole VALUES (2, 'recepcionist', '<?xml version="1.0" encoding="UTF-8"?><security></security>');
INSERT INTO public.trole VALUES (3, 'hotel manager', '<?xml version="1.0" encoding="UTF-8"?><security></security>');
INSERT INTO public.trole VALUES (4, 'admin', '<?xml version="1.0" encoding="UTF-8"?><security></security>');
INSERT INTO public.trole VALUES (5, 'demo', '<?xml version="1.0" encoding="UTF-8"?><security></security>');


--
-- TOC entry 3138 (class 0 OID 286860)
-- Dependencies: 214
-- Data for Name: trole_server_permission; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.trole_server_permission VALUES (1, 1, 1);
INSERT INTO public.trole_server_permission VALUES (2, 1, 5);
INSERT INTO public.trole_server_permission VALUES (3, 1, 9);
INSERT INTO public.trole_server_permission VALUES (4, 1, 11);
INSERT INTO public.trole_server_permission VALUES (5, 1, 12);
INSERT INTO public.trole_server_permission VALUES (6, 1, 13);
INSERT INTO public.trole_server_permission VALUES (7, 1, 14);
INSERT INTO public.trole_server_permission VALUES (8, 1, 15);
INSERT INTO public.trole_server_permission VALUES (9, 1, 18);
INSERT INTO public.trole_server_permission VALUES (10, 1, 23);
INSERT INTO public.trole_server_permission VALUES (11, 1, 24);
INSERT INTO public.trole_server_permission VALUES (12, 1, 25);
INSERT INTO public.trole_server_permission VALUES (13, 1, 26);
INSERT INTO public.trole_server_permission VALUES (14, 1, 22);
INSERT INTO public.trole_server_permission VALUES (15, 2, 5);
INSERT INTO public.trole_server_permission VALUES (16, 2, 13);
INSERT INTO public.trole_server_permission VALUES (17, 2, 10);
INSERT INTO public.trole_server_permission VALUES (18, 2, 14);
INSERT INTO public.trole_server_permission VALUES (19, 2, 15);
INSERT INTO public.trole_server_permission VALUES (20, 2, 23);
INSERT INTO public.trole_server_permission VALUES (21, 2, 24);
INSERT INTO public.trole_server_permission VALUES (22, 2, 25);
INSERT INTO public.trole_server_permission VALUES (23, 2, 26);
INSERT INTO public.trole_server_permission VALUES (24, 2, 18);
INSERT INTO public.trole_server_permission VALUES (25, 2, 22);
INSERT INTO public.trole_server_permission VALUES (26, 2, 9);
INSERT INTO public.trole_server_permission VALUES (27, 3, 1);
INSERT INTO public.trole_server_permission VALUES (28, 3, 3);
INSERT INTO public.trole_server_permission VALUES (29, 3, 5);
INSERT INTO public.trole_server_permission VALUES (30, 3, 6);
INSERT INTO public.trole_server_permission VALUES (31, 3, 7);
INSERT INTO public.trole_server_permission VALUES (32, 3, 8);
INSERT INTO public.trole_server_permission VALUES (33, 3, 9);
INSERT INTO public.trole_server_permission VALUES (34, 3, 10);
INSERT INTO public.trole_server_permission VALUES (35, 3, 11);
INSERT INTO public.trole_server_permission VALUES (36, 3, 12);
INSERT INTO public.trole_server_permission VALUES (37, 3, 19);
INSERT INTO public.trole_server_permission VALUES (38, 3, 20);
INSERT INTO public.trole_server_permission VALUES (39, 3, 21);
INSERT INTO public.trole_server_permission VALUES (40, 3, 18);
INSERT INTO public.trole_server_permission VALUES (41, 3, 26);
INSERT INTO public.trole_server_permission VALUES (42, 3, 18);
INSERT INTO public.trole_server_permission VALUES (43, 3, 17);
INSERT INTO public.trole_server_permission VALUES (44, 3, 9);
INSERT INTO public.trole_server_permission VALUES (45, 3, 16);
INSERT INTO public.trole_server_permission VALUES (46, 3, 30);
INSERT INTO public.trole_server_permission VALUES (47, 3, 31);
INSERT INTO public.trole_server_permission VALUES (48, 4, 1);
INSERT INTO public.trole_server_permission VALUES (49, 4, 2);
INSERT INTO public.trole_server_permission VALUES (50, 4, 3);
INSERT INTO public.trole_server_permission VALUES (51, 4, 4);
INSERT INTO public.trole_server_permission VALUES (52, 4, 5);
INSERT INTO public.trole_server_permission VALUES (53, 4, 6);
INSERT INTO public.trole_server_permission VALUES (54, 4, 7);
INSERT INTO public.trole_server_permission VALUES (55, 4, 8);
INSERT INTO public.trole_server_permission VALUES (56, 4, 9);
INSERT INTO public.trole_server_permission VALUES (57, 4, 10);
INSERT INTO public.trole_server_permission VALUES (58, 4, 11);
INSERT INTO public.trole_server_permission VALUES (59, 4, 12);
INSERT INTO public.trole_server_permission VALUES (60, 4, 13);
INSERT INTO public.trole_server_permission VALUES (61, 4, 14);
INSERT INTO public.trole_server_permission VALUES (62, 4, 15);
INSERT INTO public.trole_server_permission VALUES (63, 4, 16);
INSERT INTO public.trole_server_permission VALUES (64, 4, 17);
INSERT INTO public.trole_server_permission VALUES (65, 4, 18);
INSERT INTO public.trole_server_permission VALUES (66, 4, 19);
INSERT INTO public.trole_server_permission VALUES (67, 4, 20);
INSERT INTO public.trole_server_permission VALUES (68, 4, 21);
INSERT INTO public.trole_server_permission VALUES (69, 4, 22);
INSERT INTO public.trole_server_permission VALUES (70, 4, 23);
INSERT INTO public.trole_server_permission VALUES (71, 4, 24);
INSERT INTO public.trole_server_permission VALUES (72, 4, 25);
INSERT INTO public.trole_server_permission VALUES (73, 4, 26);
INSERT INTO public.trole_server_permission VALUES (74, 4, 27);
INSERT INTO public.trole_server_permission VALUES (75, 4, 28);
INSERT INTO public.trole_server_permission VALUES (76, 4, 29);
INSERT INTO public.trole_server_permission VALUES (77, 4, 30);
INSERT INTO public.trole_server_permission VALUES (78, 4, 31);
INSERT INTO public.trole_server_permission VALUES (79, 5, 9);
INSERT INTO public.trole_server_permission VALUES (80, 5, 26);
INSERT INTO public.trole_server_permission VALUES (81, 5, 10);


--
-- TOC entry 3136 (class 0 OID 286849)
-- Dependencies: 212
-- Data for Name: tserver_permission; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tserver_permission VALUES (1, 'com.campusdual.jardhotelsontimize.api.core.service.IHotelService/hotelQuery');
INSERT INTO public.tserver_permission VALUES (2, 'com.campusdual.jardhotelsontimize.api.core.service.IHotelService/hotelInsert');
INSERT INTO public.tserver_permission VALUES (3, 'com.campusdual.jardhotelsontimize.api.core.service.IHotelService/hotelUpdate');
INSERT INTO public.tserver_permission VALUES (4, 'com.campusdual.jardhotelsontimize.api.core.service.IHotelService/hotelDelete');
INSERT INTO public.tserver_permission VALUES (5, 'com.campusdual.jardhotelsontimize.api.core.service.IRoomService/roomQuery');
INSERT INTO public.tserver_permission VALUES (6, 'com.campusdual.jardhotelsontimize.api.core.service.IRoomService/roomInsert');
INSERT INTO public.tserver_permission VALUES (7, 'com.campusdual.jardhotelsontimize.api.core.service.IRoomService/roomUpdate');
INSERT INTO public.tserver_permission VALUES (8, 'com.campusdual.jardhotelsontimize.api.core.service.IRoomService/roomDelete');
INSERT INTO public.tserver_permission VALUES (9, 'com.campusdual.jardhotelsontimize.api.core.service.IPersonService/personQuery');
INSERT INTO public.tserver_permission VALUES (10, 'com.campusdual.jardhotelsontimize.api.core.service.IPersonService/personInsert');
INSERT INTO public.tserver_permission VALUES (11, 'com.campusdual.jardhotelsontimize.api.core.service.IPersonService/personUpdate');
INSERT INTO public.tserver_permission VALUES (12, 'com.campusdual.jardhotelsontimize.api.core.service.IPersonService/personDelete');
INSERT INTO public.tserver_permission VALUES (13, 'com.campusdual.jardhotelsontimize.api.core.service.IGuestService/guestQuery');
INSERT INTO public.tserver_permission VALUES (14, 'com.campusdual.jardhotelsontimize.api.core.service.IGuestService/guestUpdate');
INSERT INTO public.tserver_permission VALUES (15, 'com.campusdual.jardhotelsontimize.api.core.service.IGuestService/guestDelete');
INSERT INTO public.tserver_permission VALUES (16, 'com.campusdual.jardhotelsontimize.api.core.service.IJobService/jobQuery');
INSERT INTO public.tserver_permission VALUES (17, 'com.campusdual.jardhotelsontimize.api.core.service.IBankAccountService/bankaccountQuery');
INSERT INTO public.tserver_permission VALUES (18, 'com.campusdual.jardhotelsontimize.api.core.service.IStaffService/staffQuery');
INSERT INTO public.tserver_permission VALUES (19, 'com.campusdual.jardhotelsontimize.api.core.service.IStaffService/staffInsert');
INSERT INTO public.tserver_permission VALUES (20, 'com.campusdual.jardhotelsontimize.api.core.service.IStaffService/staffUpdate');
INSERT INTO public.tserver_permission VALUES (21, 'com.campusdual.jardhotelsontimize.api.core.service.IStaffService/staffDelete');
INSERT INTO public.tserver_permission VALUES (22, 'com.campusdual.jardhotelsontimize.api.core.service.IBookingService/bookingQuery');
INSERT INTO public.tserver_permission VALUES (23, 'com.campusdual.jardhotelsontimize.api.core.service.IBookingService/bookingInsert');
INSERT INTO public.tserver_permission VALUES (24, 'com.campusdual.jardhotelsontimize.api.core.service.IBookingService/bookingUpdate');
INSERT INTO public.tserver_permission VALUES (25, 'com.campusdual.jardhotelsontimize.api.core.service.IBookingService/bookingDelete');
INSERT INTO public.tserver_permission VALUES (26, 'com.campusdual.jardhotelsontimize.api.core.service.IUserService/userQuery');
INSERT INTO public.tserver_permission VALUES (27, 'com.campusdual.jardhotelsontimize.api.core.service.IMenuService/menuInsert');
INSERT INTO public.tserver_permission VALUES (28, 'com.campusdual.jardhotelsontimize.api.core.service.IMenuService/menuUpdate');
INSERT INTO public.tserver_permission VALUES (29, 'com.campusdual.jardhotelsontimize.api.core.service.IMenuService/menuDelete');
INSERT INTO public.tserver_permission VALUES (30, 'com.campusdual.jardhotelsontimize.api.core.service.IPantryService/pantryDelete');
INSERT INTO public.tserver_permission VALUES (31, 'com.campusdual.jardhotelsontimize.api.core.service.IPantryService/pantryInsert');


--
-- TOC entry 3130 (class 0 OID 286800)
-- Dependencies: 206
-- Data for Name: tuser; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tuser VALUES ('francisco123', 'password1', 'francisco123@example.com', '2023-07-26 11:36:46.903637', true, false, 1);
INSERT INTO public.tuser VALUES ('maria456', 'password2', 'maria456@example.com', '2023-07-26 11:36:46.903637', true, false, 2);
INSERT INTO public.tuser VALUES ('antonio789', 'password3', 'antonio789@example.com', '2023-07-26 11:36:46.903637', true, false, 3);
INSERT INTO public.tuser VALUES ('carmen123', 'password4', 'carmen123@example.com', '2023-07-26 11:36:46.903637', true, false, 4);
INSERT INTO public.tuser VALUES ('manuel987', 'password5', 'manuel987@example.com', '2023-07-26 11:36:46.903637', true, false, 5);
INSERT INTO public.tuser VALUES ('laura321', 'password6', 'laura321@example.com', '2023-07-26 11:36:46.903637', true, false, 6);
INSERT INTO public.tuser VALUES ('pedro654', 'password7', 'pedro654@example.com', '2023-07-26 11:36:46.903637', true, false, 7);
INSERT INTO public.tuser VALUES ('isabel987', 'password8', 'isabel987@example.com', '2023-07-26 11:36:46.903637', true, false, 8);
INSERT INTO public.tuser VALUES ('jose123', 'password9', 'jose123@example.com', '2023-07-26 11:36:46.903637', true, false, 9);
INSERT INTO public.tuser VALUES ('ana456', 'password10', 'ana456@example.com', '2023-07-26 11:36:46.903637', true, false, 10);
INSERT INTO public.tuser VALUES ('admin', 'admin', 'admin@admin.com', '2023-07-26 11:36:46.903637', true, false, -1);
INSERT INTO public.tuser VALUES ('demouser', 'demo', 'demo@demo.com', '2023-07-26 11:36:46.903637', true, false, -2);
INSERT INTO public.tuser VALUES ('mfernandez', '12345678', 'mfernandez@gmail.com', '2023-07-26 12:00:18.014489', true, false, 12);


--
-- TOC entry 3134 (class 0 OID 286829)
-- Dependencies: 210
-- Data for Name: tuser_role; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tuser_role VALUES (1, 1, 'maria456');
INSERT INTO public.tuser_role VALUES (2, 1, 'antonio789');
INSERT INTO public.tuser_role VALUES (3, 1, 'manuel987');
INSERT INTO public.tuser_role VALUES (4, 1, 'laura321');
INSERT INTO public.tuser_role VALUES (5, 2, 'laura321');
INSERT INTO public.tuser_role VALUES (6, 1, 'isabel987');
INSERT INTO public.tuser_role VALUES (7, 1, 'ana456');
INSERT INTO public.tuser_role VALUES (8, 2, 'francisco123');
INSERT INTO public.tuser_role VALUES (9, 3, 'ana456');
INSERT INTO public.tuser_role VALUES (10, 4, 'admin');
INSERT INTO public.tuser_role VALUES (11, 5, 'demouser');
INSERT INTO public.tuser_role VALUES (13, 1, 'mfernandez');


--
-- TOC entry 3172 (class 0 OID 0)
-- Dependencies: 216
-- Name: bankaccountformat_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.bankaccountformat_id_seq', 5, true);


--
-- TOC entry 3173 (class 0 OID 0)
-- Dependencies: 225
-- Name: booking_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.booking_id_seq', 14, true);


--
-- TOC entry 3174 (class 0 OID 0)
-- Dependencies: 196
-- Name: coin_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.coin_id_seq', 3, true);


--
-- TOC entry 3175 (class 0 OID 0)
-- Dependencies: 198
-- Name: country_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.country_id_seq', 8, true);


--
-- TOC entry 3176 (class 0 OID 0)
-- Dependencies: 200
-- Name: hotel_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.hotel_id_seq', 11, true);


--
-- TOC entry 3177 (class 0 OID 0)
-- Dependencies: 218
-- Name: job_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.job_id_seq', 10, true);


--
-- TOC entry 3178 (class 0 OID 0)
-- Dependencies: 221
-- Name: menu_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.menu_id_seq', 11, true);


--
-- TOC entry 3179 (class 0 OID 0)
-- Dependencies: 223
-- Name: pantry_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.pantry_id_seq', 90, true);


--
-- TOC entry 3180 (class 0 OID 0)
-- Dependencies: 204
-- Name: person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.person_id_seq', 12, true);


--
-- TOC entry 3181 (class 0 OID 0)
-- Dependencies: 202
-- Name: room_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.room_id_seq', 16, true);


--
-- TOC entry 3182 (class 0 OID 0)
-- Dependencies: 207
-- Name: trole_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.trole_id_seq', 1, false);


--
-- TOC entry 3183 (class 0 OID 0)
-- Dependencies: 213
-- Name: trole_server_permission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.trole_server_permission_id_seq', 81, true);


--
-- TOC entry 3184 (class 0 OID 0)
-- Dependencies: 211
-- Name: tserver_permission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tserver_permission_id_seq', 1, false);


--
-- TOC entry 3185 (class 0 OID 0)
-- Dependencies: 209
-- Name: tuser_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tuser_role_id_seq', 13, true);


--
-- TOC entry 2930 (class 2606 OID 286775)
-- Name: person Repeated documentation in another person; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT "Repeated documentation in another person" UNIQUE (documentation);


--
-- TOC entry 2926 (class 2606 OID 286759)
-- Name: room Repeated number in hotel; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.room
    ADD CONSTRAINT "Repeated number in hotel" UNIQUE (number, hotel);


--
-- TOC entry 2922 (class 2606 OID 286738)
-- Name: hotel Repeated phone in an other hotel; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hotel
    ADD CONSTRAINT "Repeated phone in an other hotel" UNIQUE (phone);


--
-- TOC entry 2950 (class 2606 OID 286893)
-- Name: bankaccountformat bankaccountformat_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bankaccountformat
    ADD CONSTRAINT bankaccountformat_pkey PRIMARY KEY (id);


--
-- TOC entry 2960 (class 2606 OID 286965)
-- Name: booking booking_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT booking_pkey PRIMARY KEY (id);


--
-- TOC entry 2918 (class 2606 OID 286714)
-- Name: coin coin_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.coin
    ADD CONSTRAINT coin_pkey PRIMARY KEY (id);


--
-- TOC entry 2920 (class 2606 OID 286722)
-- Name: country country_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (id);


--
-- TOC entry 2948 (class 2606 OID 286880)
-- Name: guest guest_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.guest
    ADD CONSTRAINT guest_pkey PRIMARY KEY (id);


--
-- TOC entry 2924 (class 2606 OID 286736)
-- Name: hotel hotel_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hotel
    ADD CONSTRAINT hotel_pkey PRIMARY KEY (id);


--
-- TOC entry 2952 (class 2606 OID 286901)
-- Name: job job_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.job
    ADD CONSTRAINT job_pkey PRIMARY KEY (id);


--
-- TOC entry 2956 (class 2606 OID 286934)
-- Name: menu menu_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.menu
    ADD CONSTRAINT menu_pkey PRIMARY KEY (id);


--
-- TOC entry 2958 (class 2606 OID 286942)
-- Name: pantry pantry_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pantry
    ADD CONSTRAINT pantry_pkey PRIMARY KEY (id);


--
-- TOC entry 2932 (class 2606 OID 286773)
-- Name: person person_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);


--
-- TOC entry 2928 (class 2606 OID 286757)
-- Name: room room_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_pkey PRIMARY KEY (id);


--
-- TOC entry 2954 (class 2606 OID 286906)
-- Name: staff staff_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT staff_pkey PRIMARY KEY (id);


--
-- TOC entry 2938 (class 2606 OID 286826)
-- Name: trole trole_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trole
    ADD CONSTRAINT trole_pkey PRIMARY KEY (id);


--
-- TOC entry 2946 (class 2606 OID 286865)
-- Name: trole_server_permission trole_server_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trole_server_permission
    ADD CONSTRAINT trole_server_permission_pkey PRIMARY KEY (id);


--
-- TOC entry 2944 (class 2606 OID 286857)
-- Name: tserver_permission tserver_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tserver_permission
    ADD CONSTRAINT tserver_permission_pkey PRIMARY KEY (id);


--
-- TOC entry 2934 (class 2606 OID 286810)
-- Name: tuser tuser_idperson_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuser
    ADD CONSTRAINT tuser_idperson_key UNIQUE (idperson);


--
-- TOC entry 2936 (class 2606 OID 286808)
-- Name: tuser tuser_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuser
    ADD CONSTRAINT tuser_pkey PRIMARY KEY (username);


--
-- TOC entry 2940 (class 2606 OID 286834)
-- Name: tuser_role tuser_role_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuser_role
    ADD CONSTRAINT tuser_role_pkey PRIMARY KEY (id);


--
-- TOC entry 2942 (class 2606 OID 286836)
-- Name: tuser_role unique role in user; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuser_role
    ADD CONSTRAINT "unique role in user" UNIQUE (id_role, user_name);


--
-- TOC entry 2996 (class 2620 OID 286977)
-- Name: booking calculate_total_price_trigger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER calculate_total_price_trigger BEFORE INSERT OR UPDATE ON public.booking FOR EACH ROW EXECUTE PROCEDURE public.calculate_total_price();


--
-- TOC entry 2995 (class 2620 OID 286976)
-- Name: booking check_booking_overlap_trigger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER check_booking_overlap_trigger BEFORE INSERT OR UPDATE ON public.booking FOR EACH ROW EXECUTE PROCEDURE public.check_booking_overlap();


--
-- TOC entry 2997 (class 2620 OID 286978)
-- Name: booking check_guest_update; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER check_guest_update BEFORE UPDATE ON public.booking FOR EACH ROW EXECUTE PROCEDURE public.prevent_guest_change();


--
-- TOC entry 2980 (class 2620 OID 286765)
-- Name: room check_hotel_room_update; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER check_hotel_room_update BEFORE UPDATE ON public.room FOR EACH ROW EXECUTE PROCEDURE public.check_hotel_from_room();


--
-- TOC entry 2998 (class 2620 OID 286979)
-- Name: booking check_room_hotel_update; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER check_room_hotel_update BEFORE UPDATE ON public.booking FOR EACH ROW EXECUTE PROCEDURE public.prevent_room_hotel_change();


--
-- TOC entry 2994 (class 2620 OID 286799)
-- Name: person trigger_verify_documentation_china; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_documentation_china BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.country = 7)) EXECUTE PROCEDURE public.verify_documentation_china();


--
-- TOC entry 2988 (class 2620 OID 286793)
-- Name: person trigger_verify_documentation_france; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_documentation_france BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.country = 4)) EXECUTE PROCEDURE public.verify_documentation_france();


--
-- TOC entry 2990 (class 2620 OID 286795)
-- Name: person trigger_verify_documentation_germany; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_documentation_germany BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.country = 5)) EXECUTE PROCEDURE public.verify_documentation_germany();


--
-- TOC entry 2992 (class 2620 OID 286797)
-- Name: person trigger_verify_documentation_portugal; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_documentation_portugal BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.country = 6)) EXECUTE PROCEDURE public.verify_documentation_portugal();


--
-- TOC entry 2982 (class 2620 OID 286787)
-- Name: person trigger_verify_documentation_spain; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_documentation_spain BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.country = 1)) EXECUTE PROCEDURE public.verify_documentation_spain();


--
-- TOC entry 2986 (class 2620 OID 286791)
-- Name: person trigger_verify_documentation_united_kingdom; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_documentation_united_kingdom BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.country = 3)) EXECUTE PROCEDURE public.verify_documentation_united_kingdom();


--
-- TOC entry 2984 (class 2620 OID 286789)
-- Name: person trigger_verify_documentation_united_states; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_documentation_united_states BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.country = 2)) EXECUTE PROCEDURE public.verify_documentation_united_states();


--
-- TOC entry 2993 (class 2620 OID 286798)
-- Name: person trigger_verify_phone_china; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_phone_china BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.phonecountry = 7)) EXECUTE PROCEDURE public.verify_phone_china();


--
-- TOC entry 2987 (class 2620 OID 286792)
-- Name: person trigger_verify_phone_france; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_phone_france BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.phonecountry = 4)) EXECUTE PROCEDURE public.verify_phone_france();


--
-- TOC entry 2989 (class 2620 OID 286794)
-- Name: person trigger_verify_phone_germany; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_phone_germany BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.phonecountry = 5)) EXECUTE PROCEDURE public.verify_phone_germany();


--
-- TOC entry 2991 (class 2620 OID 286796)
-- Name: person trigger_verify_phone_portugal; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_phone_portugal BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.phonecountry = 6)) EXECUTE PROCEDURE public.verify_phone_portugal();


--
-- TOC entry 2981 (class 2620 OID 286786)
-- Name: person trigger_verify_phone_spain; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_phone_spain BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.phonecountry = 1)) EXECUTE PROCEDURE public.verify_phone_spain();


--
-- TOC entry 2985 (class 2620 OID 286790)
-- Name: person trigger_verify_phone_united_kingdom; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_phone_united_kingdom BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.phonecountry = 3)) EXECUTE PROCEDURE public.verify_phone_united_kingdom();


--
-- TOC entry 2983 (class 2620 OID 286788)
-- Name: person trigger_verify_phone_united_states; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trigger_verify_phone_united_states BEFORE INSERT OR UPDATE ON public.person FOR EACH ROW WHEN ((new.phonecountry = 2)) EXECUTE PROCEDURE public.verify_phone_united_states();


--
-- TOC entry 2979 (class 2606 OID 286971)
-- Name: booking booking_guest_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT booking_guest_fkey FOREIGN KEY (guest) REFERENCES public.guest(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2978 (class 2606 OID 286966)
-- Name: booking booking_room_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT booking_room_fkey FOREIGN KEY (room) REFERENCES public.room(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2961 (class 2606 OID 286723)
-- Name: country country_coin_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_coin_fkey FOREIGN KEY (coin) REFERENCES public.coin(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2971 (class 2606 OID 286881)
-- Name: guest guest_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.guest
    ADD CONSTRAINT guest_id_fkey FOREIGN KEY (id) REFERENCES public.person(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2962 (class 2606 OID 286739)
-- Name: hotel hotel_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hotel
    ADD CONSTRAINT hotel_country_fkey FOREIGN KEY (country) REFERENCES public.country(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 2977 (class 2606 OID 286948)
-- Name: pantry pantry_idhotel_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pantry
    ADD CONSTRAINT pantry_idhotel_fkey FOREIGN KEY (idhotel) REFERENCES public.hotel(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2976 (class 2606 OID 286943)
-- Name: pantry pantry_idmenu_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pantry
    ADD CONSTRAINT pantry_idmenu_fkey FOREIGN KEY (idmenu) REFERENCES public.menu(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2964 (class 2606 OID 286776)
-- Name: person person_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_country_fkey FOREIGN KEY (country) REFERENCES public.country(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 2965 (class 2606 OID 286781)
-- Name: person person_phonecountry_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_phonecountry_fkey FOREIGN KEY (phonecountry) REFERENCES public.country(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 2963 (class 2606 OID 286760)
-- Name: room room_hotel_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_hotel_fkey FOREIGN KEY (hotel) REFERENCES public.hotel(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2974 (class 2606 OID 286917)
-- Name: staff staff_bankaccountformat_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT staff_bankaccountformat_fkey FOREIGN KEY (bankaccountformat) REFERENCES public.bankaccountformat(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 2972 (class 2606 OID 286907)
-- Name: staff staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT staff_id_fkey FOREIGN KEY (id) REFERENCES public.person(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2973 (class 2606 OID 286912)
-- Name: staff staff_idhotel_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT staff_idhotel_fkey FOREIGN KEY (idhotel) REFERENCES public.hotel(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2975 (class 2606 OID 286922)
-- Name: staff staff_job_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT staff_job_fkey FOREIGN KEY (job) REFERENCES public.job(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 2969 (class 2606 OID 286866)
-- Name: trole_server_permission trole_server_permission_id_rolename_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trole_server_permission
    ADD CONSTRAINT trole_server_permission_id_rolename_fkey FOREIGN KEY (id_rolename) REFERENCES public.trole(id);


--
-- TOC entry 2970 (class 2606 OID 286871)
-- Name: trole_server_permission trole_server_permission_id_server_permission_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trole_server_permission
    ADD CONSTRAINT trole_server_permission_id_server_permission_fkey FOREIGN KEY (id_server_permission) REFERENCES public.tserver_permission(id);


--
-- TOC entry 2966 (class 2606 OID 286811)
-- Name: tuser tuser_idperson_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuser
    ADD CONSTRAINT tuser_idperson_fkey FOREIGN KEY (idperson) REFERENCES public.person(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2967 (class 2606 OID 286837)
-- Name: tuser_role tuser_role_id_role_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuser_role
    ADD CONSTRAINT tuser_role_id_role_fkey FOREIGN KEY (id_role) REFERENCES public.trole(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 2968 (class 2606 OID 286842)
-- Name: tuser_role tuser_role_user_name_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuser_role
    ADD CONSTRAINT tuser_role_user_name_fkey FOREIGN KEY (user_name) REFERENCES public.tuser(username) ON UPDATE CASCADE ON DELETE CASCADE;


-- Completed on 2023-07-27 08:15:38

--
-- PostgreSQL database dump complete
--

