package me.dio.wow_character_interaction.domain.model;

import jakarta.persistence.*;

@Entity(name = "wow_characters")
public class WowCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String gender;

    private String race;

    private String characterClass;

    private String occupation;

    @Column(columnDefinition = "TEXT")
    private String lore;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(String characterClass) {
        this.characterClass = characterClass;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getLore() {
        return lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    public String generateContextByQuestion(String question) {
        return """
                Question: %s
                Character Name: %s
                Gender: %s
                Race: %s
                Class: %s
                Occupation: %s
                Lore: %s
                """.formatted(question, this.name, this.gender, this.race, this.characterClass, this.occupation, this.lore);
    }
}
