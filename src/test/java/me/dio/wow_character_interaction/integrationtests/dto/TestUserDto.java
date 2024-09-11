package me.dio.wow_character_interaction.integrationtests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

public class TestUserDto {

    @JsonProperty("user_name")
    @Mapping("username")
    private String username;

    @JsonProperty("full_name")
    private String fullName;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestUserDto userDto = (TestUserDto) o;
        return Objects.equals(username, userDto.username) && Objects.equals(fullName, userDto.fullName) && Objects.equals(password, userDto.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, fullName, password);
    }
}
