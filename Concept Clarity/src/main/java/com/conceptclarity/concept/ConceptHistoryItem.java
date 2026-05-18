package com.conceptclarity.concept;

import java.time.LocalDateTime;

public class ConceptHistoryItem {

    private Long id;
    private String concept;
    private String level;
    private String explanationType;
    private LocalDateTime createdAt;

    public ConceptHistoryItem(Long id, String concept, String level, String explanationType, LocalDateTime createdAt) {
        this.id = id;
        this.concept = concept;
        this.level = level;
        this.explanationType = explanationType;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
