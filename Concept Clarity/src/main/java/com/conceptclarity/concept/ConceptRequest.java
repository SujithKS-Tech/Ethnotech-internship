package com.conceptclarity.concept;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ConceptRequest {

    @NotBlank(message = "Please enter a concept to explain.")
    @Size(min = 2, max = 120, message = "Concept must be between 2 and 120 characters.")
    private String concept;

    @NotBlank(message = "Please choose an explanation level.")
    private String level;

    @NotBlank(message = "Please choose an explanation type.")
    private String explanationType;

    @Size(max = 500, message = "Context must be 500 characters or less.")
    private String context;

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
}
