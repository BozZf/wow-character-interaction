package me.dio.wow_character_interaction.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

@JsonPropertyOrder({"id", "name", "gender", "race", "character_class", "occupation", "lore"})
public class WowCharacterDto extends RepresentationModel<WowCharacterDto> {

    @JsonProperty("id")
    @Mapping("id")
    private Long key;

    private String name;

    private String gender;

    private String race;

    @JsonProperty("character_class")
    private String characterClass;

    private String occupation;

    private String lore;

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
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
        WowCharacterDto that = (WowCharacterDto) o;
        return Objects.equals(key, that.key) && Objects.equals(name, that.name) && Objects.equals(gender, that.gender) && Objects.equals(race, that.race) && Objects.equals(characterClass, that.characterClass) && Objects.equals(occupation, that.occupation) && Objects.equals(lore, that.lore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, name, gender, race, characterClass, occupation, lore);
    }
}
