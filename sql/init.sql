--
-- PostgreSQL database dump
--

-- Dumped from database version 14.7
-- Dumped by pg_dump version 14.7

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'SQL_ASCII';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: adminpack; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS adminpack WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION adminpack; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION adminpack IS 'administrative functions for PostgreSQL';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: chats; Type: TABLE; Schema: public; Owner: zybarev
--

CREATE TABLE public.chats (
    id bigint NOT NULL,
    type character varying(20),
    title text,
    username character varying(50),
    firstname character varying(50),
    lastname character varying(50),
    location_longitude double precision,
    location_latitude double precision
);


ALTER TABLE public.chats OWNER TO zybarev;

--
-- Name: example_answers; Type: TABLE; Schema: public; Owner: zybarev
--

CREATE TABLE public.example_answers (
    id integer NOT NULL,
    "timestamp" integer,
    user_id integer,
    is_right boolean,
    timer integer,
    chat_id bigint,
    score integer DEFAULT 0,
    chat_extended_id character varying(50)
);


ALTER TABLE public.example_answers OWNER TO zybarev;

--
-- Name: example_answers_id_seq; Type: SEQUENCE; Schema: public; Owner: zybarev
--

CREATE SEQUENCE public.example_answers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.example_answers_id_seq OWNER TO zybarev;

--
-- Name: example_answers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: zybarev
--

ALTER SEQUENCE public.example_answers_id_seq OWNED BY public.example_answers.id;


--
-- Name: locations; Type: TABLE; Schema: public; Owner: zybarev
--

CREATE TABLE public.locations (
    id integer NOT NULL,
    address text,
    aliases text,
    latitude double precision,
    longitude double precision
);


ALTER TABLE public.locations OWNER TO zybarev;

--
-- Name: locations_id_seq; Type: SEQUENCE; Schema: public; Owner: zybarev
--

CREATE SEQUENCE public.locations_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.locations_id_seq OWNER TO zybarev;

--
-- Name: locations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: zybarev
--

ALTER SEQUENCE public.locations_id_seq OWNED BY public.locations.id;


--
-- Name: quotes; Type: TABLE; Schema: public; Owner: zybarev
--

CREATE TABLE public.quotes (
    id integer,
    deleted integer,
    author text,
    quote text,
    channel text,
    "time" integer
);


ALTER TABLE public.quotes OWNER TO zybarev;

--
-- Name: sec_authorities; Type: TABLE; Schema: public; Owner: zybarev
--

CREATE TABLE public.sec_authorities (
    username character varying(15),
    authority character varying(25)
);


ALTER TABLE public.sec_authorities OWNER TO zybarev;

--
-- Name: sec_users; Type: TABLE; Schema: public; Owner: zybarev
--

CREATE TABLE public.sec_users (
    username character varying(15) NOT NULL,
    password character varying(100),
    enabled smallint
);


ALTER TABLE public.sec_users OWNER TO zybarev;

--
-- Name: users; Type: TABLE; Schema: public; Owner: zybarev
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    firstname text,
    lastname text,
    username text,
    location_id integer
);


ALTER TABLE public.users OWNER TO zybarev;

--
-- Name: wordle_chat_attempt_list; Type: TABLE; Schema: public; Owner: zybarev
--

CREATE TABLE public.wordle_chat_attempt_list (
    chat_id bigint NOT NULL,
    attempt_list character varying(255)[],
    extended_chat_id character varying(255)
);


ALTER TABLE public.wordle_chat_attempt_list OWNER TO zybarev;

--
-- Name: wordle_chat_current_word; Type: TABLE; Schema: public; Owner: zybarev
--

CREATE TABLE public.wordle_chat_current_word (
    chat_id bigint NOT NULL,
    current_word character varying(255)
);


ALTER TABLE public.wordle_chat_current_word OWNER TO zybarev;

--
-- Name: wordle_chat_word_list; Type: TABLE; Schema: public; Owner: zybarev
--

CREATE TABLE public.wordle_chat_word_list (
    chat_id bigint NOT NULL,
    word_list character varying(255)[]
);


ALTER TABLE public.wordle_chat_word_list OWNER TO zybarev;

--
-- Name: wordle_event; Type: TABLE; Schema: public; Owner: zybarev
--

CREATE TABLE public.wordle_event (
    id bigint NOT NULL,
    attempt_word character varying(255),
    chat_id bigint,
    current_word character varying(255),
    is_right boolean,
    user_id bigint,
    "timestamp" bigint
);


ALTER TABLE public.wordle_event OWNER TO zybarev;

--
-- Name: wordle_event_id_seq; Type: SEQUENCE; Schema: public; Owner: zybarev
--

CREATE SEQUENCE public.wordle_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.wordle_event_id_seq OWNER TO zybarev;

--
-- Name: wordle_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: zybarev
--

ALTER SEQUENCE public.wordle_event_id_seq OWNED BY public.wordle_event.id;


--
-- Name: wordle_user; Type: TABLE; Schema: public; Owner: zybarev
--

CREATE TABLE public.wordle_user (
    id integer NOT NULL,
    user_id bigint,
    chat_id bigint,
    attempts_count integer,
    first_attempt_timestamp_seconds bigint,
    points bigint
);


ALTER TABLE public.wordle_user OWNER TO zybarev;

--
-- Name: wordle_user_id_seq; Type: SEQUENCE; Schema: public; Owner: zybarev
--

CREATE SEQUENCE public.wordle_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.wordle_user_id_seq OWNER TO zybarev;

--
-- Name: wordle_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: zybarev
--

ALTER SEQUENCE public.wordle_user_id_seq OWNED BY public.wordle_user.id;


--
-- Name: wordle_word; Type: TABLE; Schema: public; Owner: zybarev
--

CREATE TABLE public.wordle_word (
    word_id integer NOT NULL,
    word character varying(64)
);


ALTER TABLE public.wordle_word OWNER TO zybarev;

--
-- Name: wordle_word_word_id_seq; Type: SEQUENCE; Schema: public; Owner: zybarev
--

CREATE SEQUENCE public.wordle_word_word_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.wordle_word_word_id_seq OWNER TO zybarev;

--
-- Name: wordle_word_word_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: zybarev
--

ALTER SEQUENCE public.wordle_word_word_id_seq OWNED BY public.wordle_word.word_id;


--
-- Name: example_answers id; Type: DEFAULT; Schema: public; Owner: zybarev
--

ALTER TABLE ONLY public.example_answers ALTER COLUMN id SET DEFAULT nextval('public.example_answers_id_seq'::regclass);


--
-- Name: locations id; Type: DEFAULT; Schema: public; Owner: zybarev
--

ALTER TABLE ONLY public.locations ALTER COLUMN id SET DEFAULT nextval('public.locations_id_seq'::regclass);


--
-- Name: wordle_event id; Type: DEFAULT; Schema: public; Owner: zybarev
--

ALTER TABLE ONLY public.wordle_event ALTER COLUMN id SET DEFAULT nextval('public.wordle_event_id_seq'::regclass);


--
-- Name: wordle_user id; Type: DEFAULT; Schema: public; Owner: zybarev
--

ALTER TABLE ONLY public.wordle_user ALTER COLUMN id SET DEFAULT nextval('public.wordle_user_id_seq'::regclass);


--
-- Name: wordle_word word_id; Type: DEFAULT; Schema: public; Owner: zybarev
--

ALTER TABLE ONLY public.wordle_word ALTER COLUMN word_id SET DEFAULT nextval('public.wordle_word_word_id_seq'::regclass);
