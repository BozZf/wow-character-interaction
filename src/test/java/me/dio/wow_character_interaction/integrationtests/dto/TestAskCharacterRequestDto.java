package me.dio.wow_character_interaction.integrationtests.dto;

public class TestAskCharacterRequestDto {

    private String question;


    public TestAskCharacterRequestDto() {
    }

    public TestAskCharacterRequestDto(String request) {
        this.question = request;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
