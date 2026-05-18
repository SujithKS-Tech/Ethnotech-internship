package com.conceptclarity.concept;

import java.time.LocalDateTime;
import java.util.List;

public class ConceptResponse {

    private Long id;
    private String concept;
    private String level;
    private String explanationType;
    private String title;
    private List<ExplanationSection> sections;
    private List<String> followUps;
    private String source;
    private LocalDateTime createdAt;

    public ConceptResponse(
            Long id,
            String concept,
            String level,
            String explanationType,
            String title,
            List<ExplanationSection> sections,
            List<String> followUps,
            String source,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.concept = concept;
        this.level = level;
        this.explanationType = explanationType;
        this.title = title;
        this.sections = sections;
        this.followUps = followUps;
        this.source = source;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getConcept() {
        return concept;
    }

    public String getLevel() {
        return level;
    }

    public String getExplanationType() {
        return explanationType;
    }

    public String getTitle() {
        return title;
    }

    public List<ExplanationSection> getSections() {
        return sections;
    }

    public List<String> getFollowUps() {
        return followUps;
    }

    public String getSource() {
        return source;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
