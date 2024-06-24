package me.dio.wow_character_interaction.adapter.client;

import me.dio.wow_character_interaction.service.GeminiService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "gemini", url = "${gemini.api.url}", configuration = GeminiApiConfig.class)
public interface GeminiChatApi extends GeminiService {

    @PostMapping("/v1beta/models/gemini-1.5-flash:generateContent")
    GeminiApiResponse chatResponse(GeminiApiRequest request);

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
        return response.candidates().get(0).content().parts().get(0).text();
    }

    record GeminiApiRequest(List<GeminiApiContent> contents) { }

    record GeminiApiContent(List<GeminiApiPart> parts) { }

    record GeminiApiPart(String text) { }

    record GeminiApiResponse(List<GeminiApiCandidate> candidates) { }

    record GeminiApiCandidate(GeminiApiContent content) { }
}
