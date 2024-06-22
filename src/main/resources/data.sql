CREATE TABLE IF NOT EXISTS wow_characters (
    id LONG AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    gender VARCHAR(50) NOT NULL,
    race VARCHAR(50) NOT NULL,
    character_class VARCHAR(50) NOT NULL,
    occupation VARCHAR(150) NOT NULL,
    lore TEXT NOT NULL
);

INSERT INTO wow_characters (name, gender, race, character_class, occupation, lore) VALUES
    ('Arthas Menethil', 'Male', 'Human', 'Paladin', 'Prince of Lordaeron', 'Arthas Menethil, Crown Prince of Lordaeron and Knight of the Silver Hand, was the son of King Terenas Menethil II and heir to the throne. He was trained as a paladin by Uther the Lightbringer and was inducted into the Order of the Silver Hand. Arthas also had a romantic relationship with the kind sorceress Jaina Proudmoore. Committed to the protection of his people, Arthas was determined to stop the plague spreading throughout Lordaeron.')
    ;