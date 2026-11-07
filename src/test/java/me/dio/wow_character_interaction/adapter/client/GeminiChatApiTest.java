package me.dio.wow_character_interaction.adapter.client;

import me.dio.wow_character_interaction.service.GeminiResponseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static me.dio.wow_character_interaction.adapter.client.GeminiChatApi.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeminiChatApiTest {

    @Test
    void sendsThePromptAndCombinesTextFromTheFirstCandidate() {
        GeminiChatApi client = request -> {
            assertEquals("Act as a character\nQuestion: Where are we?\n",
                    request.contents().get(0).parts().get(0).text());
            return response(new GeminiApiPart("For Azeroth!"), new GeminiApiPart(null),
                    new GeminiApiPart("Follow me."));
        };

        assertEquals("For Azeroth!\nFollow me.", client.askCharacterQuestion("Act as a character",
                "Question: Where are we?"));
    }

    @ParameterizedTest
    @MethodSource("responsesWithoutText")
    void rejectsResponsesWithoutAnAnswer(GeminiApiResponse response) {
        GeminiChatApi client = request -> response;

        assertThrows(GeminiResponseException.class, () -> client.askCharacterQuestion("Role", "Context"));
    }

    private static Stream<GeminiApiResponse> responsesWithoutText() {
        return Stream.of(null, new GeminiApiResponse(null), new GeminiApiResponse(List.of()),
                new GeminiApiResponse(List.of(new GeminiApiCandidate(null))),
                response(), response(new GeminiApiPart(" ")));
    }

    private static GeminiApiResponse response(GeminiApiPart... parts) {
        return new GeminiApiResponse(List.of(new GeminiApiCandidate(new GeminiApiContent(List.of(parts)))));
    }
}
