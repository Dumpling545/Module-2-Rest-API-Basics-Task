SET MODE PostgreSQL;

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

CREATE TABLE public.tag (
    id bigserial NOT NULL,
    name character varying(100) UNIQUE NOT NULL,
    PRIMARY KEY (id)
);

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

CREATE TABLE IF NOT EXISTS public.cert_user
(
    id bigserial NOT NULL,
    user_name character varying(100) UNIQUE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.cert_order
(
    id bigserial NOT NULL,
    user_id bigserial NOT NULL,
    gift_certificate_id bigserial NOT NULL,
    cost real NOT NULL,
    purchase_date timestamp without time zone NOT NULL,
    CONSTRAINT fk_cert_order_user
            FOREIGN KEY(user_id)
            REFERENCES cert_user(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_cert_order_gift_certificate
            FOREIGN KEY(gift_certificate_id)
            REFERENCES gift_certificate(id)
            ON DELETE CASCADE,
    PRIMARY KEY (id)
);

