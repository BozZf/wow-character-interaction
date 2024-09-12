package me.dio.wow_character_interaction.integrationtests.dto;

public class TestAskCharacterResponseDto {

    private String answer;

    public TestAskCharacterResponseDto() {
    }

    public TestAskCharacterResponseDto(String response) {
        this.answer = response;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
