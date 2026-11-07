package me.dio.wow_character_interaction.service;

public class GeminiResponseException extends RuntimeException {

    public GeminiResponseException() {
        super("Gemini returned no answer");
    }
}
