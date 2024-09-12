package me.dio.wow_character_interaction.data.dto;

public class AskCharacterResponseDto {

    private String answer;

    public AskCharacterResponseDto() {
    }

    public AskCharacterResponseDto(String response) {
        this.answer = response;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
