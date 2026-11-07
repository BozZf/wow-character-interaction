package me.dio.wow_character_interaction.adapter.client;

import me.dio.wow_character_interaction.configs.GeminiApiConfig;
import me.dio.wow_character_interaction.service.GeminiService;
import me.dio.wow_character_interaction.service.GeminiResponseException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@FeignClient(name = "gemini", url = "${gemini.api.url}", configuration = GeminiApiConfig.class)
public interface GeminiChatApi extends GeminiService {

    @PostMapping("/v1beta/models/${gemini.api.model}:generateContent")
    GeminiApiResponse chatResponse(@RequestBody GeminiApiRequest request);

    @Override
    default String askCharacterQuestion(String objective, String context) {
        String prompt = """
                %s
                %s
                """.formatted(objective, context);

        GeminiApiRequest request = new GeminiApiRequest(
                List.of(new GeminiApiContent(
                        List.of(new GeminiApiPart(prompt)))));

        GeminiApiResponse response = chatResponse(request);
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            throw new GeminiResponseException();
        }

        GeminiApiCandidate candidate = response.candidates().get(0);
        if (candidate == null || candidate.content() == null || candidate.content().parts() == null) {
            throw new GeminiResponseException();
        }

        String answer = candidate.content().parts().stream()
                .filter(Objects::nonNull)
                .map(GeminiApiPart::text)
                .filter(text -> text != null && !text.isBlank())
                .collect(Collectors.joining("\n"));
        if (answer.isBlank()) {
            throw new GeminiResponseException();
        }
        return answer;
    }

    record GeminiApiRequest(List<GeminiApiContent> contents) { }

    record GeminiApiContent(List<GeminiApiPart> parts) { }

    record GeminiApiPart(String text) { }

    record GeminiApiResponse(List<GeminiApiCandidate> candidates) { }

    record GeminiApiCandidate(GeminiApiContent content) { }
}
