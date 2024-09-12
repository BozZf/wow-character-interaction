package me.dio.wow_character_interaction.data.dto;

public class AskCharacterRequestDto {

    private String question;


    public AskCharacterRequestDto() {
    }

    public AskCharacterRequestDto(String request) {
        this.question = request;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
