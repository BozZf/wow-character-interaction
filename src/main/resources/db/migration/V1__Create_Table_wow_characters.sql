CREATE TABLE wow_characters (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100),
    gender VARCHAR(50),
    race VARCHAR(50),
    character_class VARCHAR(100),
    occupation VARCHAR(100),
    lore TEXT
);