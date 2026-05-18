package com.conceptclarity.concept;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "concept_explanations")
public class ConceptExplanation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String concept;

    @Column(nullable = false, length = 30)
    private String level;

    @Column(name = "explanation_type", nullable = false, length = 40)
    private String explanationType;

    @Column(length = 500)
    private String context;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String explanationJson;

    @Column(nullable = false, length = 120)
    private String source;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void beforeInsert() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getExplanationType() {
        return explanationType;
    }

    public void setExplanationType(String explanationType) {
        this.explanationType = explanationType;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplanationJson() {
        return explanationJson;
    }

    public void setExplanationJson(String explanationJson) {
        this.explanationJson = explanationJson;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
