package me.dio.wow_character_interaction.domain.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "wow_characters")
public class WowCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String gender;

    private String race;

    @Column(name = "character_class")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WowCharacter that = (WowCharacter) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(race, that.race) &&
                Objects.equals(characterClass, that.characterClass) &&
                Objects.equals(occupation, that.occupation) &&
                Objects.equals(lore, that.lore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, gender, race, characterClass, occupation, lore);
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
