package me.dio.wow_character_interaction.integrationtests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TestWowCharacterDTO {

    private Long id;

    private String name;

    private String gender;

    private String race;

    @JsonProperty("character_class")
    private String characterClass;

    private String occupation;

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
        TestWowCharacterDTO that = (TestWowCharacterDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(gender, that.gender) && Objects.equals(race, that.race) && Objects.equals(characterClass, that.characterClass) && Objects.equals(occupation, that.occupation) && Objects.equals(lore, that.lore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, gender, race, characterClass, occupation, lore);
    }
}
