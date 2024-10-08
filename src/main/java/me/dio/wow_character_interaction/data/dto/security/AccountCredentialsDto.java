package me.dio.wow_character_interaction.data.dto.security;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.Objects;

@JacksonXmlRootElement
public class AccountCredentialsDto {

    private String username;

    private String password;

    public AccountCredentialsDto() {}

    public AccountCredentialsDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        AccountCredentialsDto that = (AccountCredentialsDto) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
