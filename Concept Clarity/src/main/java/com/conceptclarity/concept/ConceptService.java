package com.conceptclarity.concept;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ConceptService {

    private final ConceptExplanationRepository repository;
    private final OpenAiExplanationService aiExplanationService;
    private final ObjectMapper objectMapper;

    public ConceptService(
            ConceptExplanationRepository repository,
            OpenAiExplanationService aiExplanationService,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.aiExplanationService = aiExplanationService;
        this.objectMapper = objectMapper;
    }

    public ConceptResponse explain(ConceptRequest request) {
        GeneratedExplanation generated = aiExplanationService.generate(request);
        ExplanationDraft draft = generated.draft();
        ConceptExplanation saved = new ConceptExplanation();
        saved.setConcept(clean(request.getConcept()));
        saved.setLevel(clean(request.getLevel()));
        saved.setExplanationType(clean(request.getExplanationType()));
        saved.setContext(clean(request.getContext()));
        saved.setTitle(draft.getTitle());
        saved.setExplanationJson(toJson(draft.getSections()));
        saved.setSource(generated.source());

        ConceptExplanation entity = repository.save(saved);

        return new ConceptResponse(
                entity.getId(),
                entity.getConcept(),
                entity.getLevel(),
                entity.getExplanationType(),
                entity.getTitle(),
                draft.getSections(),
                draft.getFollowUps(),
                entity.getSource(),
                entity.getCreatedAt()
        );
    }

    public List<ConceptHistoryItem> history() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(item -> new ConceptHistoryItem(
                        item.getId(),
                        item.getConcept(),
                        item.getLevel(),
                        item.getExplanationType(),
                        item.getCreatedAt()
                ))
                .toList();
    }

    public AiToolResponse aiTool(AiToolRequest request) {
        return aiExplanationService.runTool(request);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    private String toJson(List<ExplanationSection> sections) {
        try {
            return objectMapper.writeValueAsString(sections);
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("Could not save explanation.", error);
        }
    }

    public List<ExplanationSection> fromJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("Could not read explanation.", error);
        }
    }
}
