--
-- PostgreSQL database dump
--

-- Dumped from database version 13.3 (Ubuntu 13.3-1.pgdg21.04+1)
-- Dumped by pg_dump version 13.3 (Ubuntu 13.3-1.pgdg21.04+1)

-- SET statement_timeout = 0;
-- SET lock_timeout = 0;
-- SET idle_in_transaction_session_timeout = 0;
-- SET client_encoding = 'UTF8';
-- SET standard_conforming_strings = on;
-- SELECT pg_catalog.set_config('search_path', '', false);
-- SET check_function_bodies = false;
-- SET xmloption = content;
-- SET client_min_messages = warning;
-- SET row_security = off;

-- SET default_tablespace = '';

-- SET default_table_access_method = heap;

--
-- Name: gift_certificate; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.gift_certificate (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    description character varying(500) NOT NULL,
    duration smallint NOT NULL,
    create_date timestamp without time zone NOT NULL,
    last_update_date timestamp without time zone NOT NULL,
    price real NOT NULL,
    CONSTRAINT last_update_after_creation_check CHECK ((last_update_date >= create_date)),
    CONSTRAINT positive_duration_check CHECK ((duration > 0)),
    CONSTRAINT positive_price_check CHECK ((price > (0)::double precision))
);


--ALTER TABLE public.gift_certificate OWNER TO root;

--
-- Name: gift_certificate_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.gift_certificate_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER TABLE public.gift_certificate_id_seq OWNER TO root;

--
-- Name: gift_certificate_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

-- ALTER SEQUENCE public.gift_certificate_id_seq OWNED BY public.gift_certificate.id;


--
-- Name: tag; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.tag (
    id SERIAL PRIMARY KEY,
    name character varying(100) UNIQUE NOT NULL
);


--ALTER TABLE public.tag OWNER TO root;

--
-- Name: tag_gift_certificate; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.tag_gift_certificate (
    tag_id bigint NOT NULL,
    gift_certificate_id bigint NOT NULL,
    CONSTRAINT fk_tag
        FOREIGN KEY(tag_id)
        REFERENCES tag(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_gift_certificate
        FOREIGN KEY(gift_certificate_id)
        REFERENCES gift_certificate(id)
        ON DELETE CASCADE,
    PRIMARY KEY (tag_id, gift_certificate_id)
);


--ALTER TABLE public.tag_gift_certificate OWNER TO root;

--
-- Name: tag_gift_certificate_gift_certificate_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.tag_gift_certificate_gift_certificate_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER TABLE public.tag_gift_certificate_gift_certificate_id_seq OWNER TO root;

--
-- Name: tag_gift_certificate_gift_certificate_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

-- ALTER SEQUENCE public.tag_gift_certificate_gift_certificate_id_seq OWNED BY public.tag_gift_certificate.gift_certificate_id;


--
-- Name: tag_gift_certificate_tag_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.tag_gift_certificate_tag_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- ALTER TABLE public.tag_gift_certificate_tag_id_seq OWNER TO root;

--
-- Name: tag_gift_certificate_tag_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

-- ALTER SEQUENCE public.tag_gift_certificate_tag_id_seq OWNED BY public.tag_gift_certificate.tag_id;


--
-- Name: tag_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.tag_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- ALTER TABLE public.tag_id_seq OWNER TO root;

--
-- Name: tag_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

-- ALTER SEQUENCE public.tag_id_seq OWNED BY public.tag.id;


--
-- Name: gift_certificate id; Type: DEFAULT; Schema: public; Owner: root
--

--  ONLY public.gift_certificate ALTER COLUMN id SET DEFAULT nextval('public.gift_certificate_id_seq'::regclass);


--
-- Name: tag id; Type: DEFAULT; Schema: public; Owner: root
--

-- ALTER TABLE ONLY public.tag ALTER COLUMN id SET DEFAULT nextval('public.tag_id_seq'::regclass);


--
-- Name: tag_gift_certificate tag_id; Type: DEFAULT; Schema: public; Owner: root
--

-- ALTER TABLE ONLY public.tag_gift_certificate ALTER COLUMN tag_id SET DEFAULT nextval('public.tag_gift_certificate_tag_id_seq'::regclass);


--
-- Name: tag_gift_certificate gift_certificate_id; Type: DEFAULT; Schema: public; Owner: root
--

-- ALTER TABLE ONLY public.tag_gift_certificate ALTER COLUMN gift_certificate_id SET DEFAULT nextval('public.tag_gift_certificate_gift_certificate_id_seq'::regclass);


--
-- Name: gift_certificate gift_certificate_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

--ALTER TABLE ONLY public.gift_certificate
 --   ADD CONSTRAINT gift_certificate_pkey PRIMARY KEY (id);


--
-- Name: tag tag_name_key; Type: CONSTRAINT; Schema: public; Owner: root
--

--ALTER TABLE ONLY public.tag
--    ADD CONSTRAINT tag_name_key UNIQUE (name);


--
-- Name: tag tag_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

--ALTER TABLE ONLY public.tag
--    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);


--
-- Name: tag_gift_certificate tag_gift_certificate_gift_certificate_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

--ALTER TABLE ONLY public.tag_gift_certificate
--    ADD CONSTRAINT tag_gift_certificate_gift_certificate_id_fkey FOREIGN KEY (gift_certificate_id) REFERENCES public.gift_certificate(id) ON DELETE CASCADE;


--
-- Name: tag_gift_certificate tag_gift_certificate_tag_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

--ALTER TABLE ONLY public.tag_gift_certificate
--    ADD CONSTRAINT tag_gift_certificate_tag_id_fkey FOREIGN KEY (tag_id) REFERENCES public.tag(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

