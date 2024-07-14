CREATE TABLE public.wow_characters (
    id bigint NOT NULL,
    name character varying(255),
    gender character varying(255),
    race character varying(255),
    character_class character varying(255),
    occupation character varying(255),
    lore text
);