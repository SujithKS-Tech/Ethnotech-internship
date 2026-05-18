package com.conceptclarity.concept;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OpenAiExplanationService {

    private static final String OPENAI_SOURCE = "OpenAI API";
    private static final String LOCAL_SOURCE = "Local AI-style fallback";

    private final LocalAiExplanationService fallbackService;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public OpenAiExplanationService(
            LocalAiExplanationService fallbackService,
            ObjectMapper objectMapper,
            RestClient.Builder restClientBuilder,
            @Value("${openai.api-key:}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model
    ) {
        this.fallbackService = fallbackService;
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.model = model;
    }

    public GeneratedExplanation generate(ConceptRequest request) {
        if (!isConfigured()) {
            return new GeneratedExplanation(fallbackService.generate(request), LOCAL_SOURCE);
        }

        try {
            String input = """
                    Create a student-friendly concept explanation.
                    Concept: %s
                    Level: %s
                    Explanation type: %s
                    Learner context: %s
                    """.formatted(
                    clean(request.getConcept()),
                    clean(request.getLevel()),
                    clean(request.getExplanationType()),
                    cleanOrDefault(request.getContext(), "No extra context provided")
            );

            return new GeneratedExplanation(requestExplanation(input), OPENAI_SOURCE);
        } catch (RuntimeException error) {
            return new GeneratedExplanation(fallbackService.generate(request), LOCAL_SOURCE);
        }
    }

    public AiToolResponse runTool(AiToolRequest request) {
        if (!isConfigured()) {
            return fallbackService.runTool(request, LOCAL_SOURCE);
        }

        try {
            String input = """
                    Run this learning tool for a student.
                    Tool: %s
                    Concept: %s
                    Level: %s
                    Explanation type: %s
                    Learner context: %s
                    """.formatted(
                    clean(request.getTool()),
                    clean(request.getConcept()),
                    cleanOrDefault(request.getLevel(), "Beginner"),
                    cleanOrDefault(request.getExplanationType(), "Detailed explanation"),
                    cleanOrDefault(request.getContext(), "No extra context provided")
            );

            ExplanationDraft draft = requestExplanation(input);
            return new AiToolResponse(draft.getTitle(), clean(request.getTool()), draft.getSections(), OPENAI_SOURCE);
        } catch (RuntimeException error) {
            return fallbackService.runTool(request, LOCAL_SOURCE);
        }
    }

    private boolean isConfigured() {
        return !apiKey.isBlank();
    }

    private ExplanationDraft requestExplanation(String input) {
        Map<String, Object> payload = Map.of(
                "model", model,
                "instructions", """
                        You are Concept Clarity, an AI tutor for students.
                        Use clear language, accurate details, and practical examples.
                        Return only JSON that matches the requested schema.
                        Keep each section concise but useful.
                        """,
                "input", input,
                "temperature", 0.4,
                "max_output_tokens", 900,
                "text", Map.of("format", responseFormat())
        );

        JsonNode response = restClient.post()
                .uri("/responses")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .body(payload)
                .retrieve()
                .body(JsonNode.class);

        String outputText = extractOutputText(response);
        if (outputText.isBlank()) {
            throw new IllegalStateException("OpenAI returned an empty explanation.");
        }

        try {
            return objectMapper.readValue(outputText, ExplanationDraft.class);
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("OpenAI returned an unreadable explanation.", error);
        }
    }

    private Map<String, Object> responseFormat() {
        return Map.of(
                "type", "json_schema",
                "name", "concept_explanation",
                "strict", true,
                "schema", Map.of(
                        "type", "object",
                        "additionalProperties", false,
                        "required", List.of("title", "sections", "followUps"),
                        "properties", Map.of(
                                "title", Map.of("type", "string"),
                                "sections", Map.of(
                                        "type", "array",
                                        "minItems", 3,
                                        "maxItems", 6,
                                        "items", Map.of(
                                                "type", "object",
                                                "additionalProperties", false,
                                                "required", List.of("heading", "body"),
                                                "properties", Map.of(
                                                        "heading", Map.of("type", "string"),
                                                        "body", Map.of("type", "string")
                                                )
                                        )
                                ),
                                "followUps", Map.of(
                                        "type", "array",
                                        "minItems", 2,
                                        "maxItems", 4,
                                        "items", Map.of("type", "string")
                                )
                        )
                )
        );
    }

    private String extractOutputText(JsonNode response) {
        if (response == null || !response.has("output")) {
            return "";
        }

        List<String> parts = new ArrayList<>();
        for (JsonNode outputItem : response.get("output")) {
            JsonNode content = outputItem.get("content");
            if (content == null || !content.isArray()) {
                continue;
            }
            for (JsonNode contentItem : content) {
                if ("output_text".equals(contentItem.path("type").asText()) && contentItem.has("text")) {
                    parts.add(contentItem.get("text").asText());
                }
            }
        }

        return String.join("", parts).trim();
    }

    private String cleanOrDefault(String value, String fallback) {
        String cleaned = clean(value);
        return cleaned.isBlank() ? fallback : cleaned;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }
}
