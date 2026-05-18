package com.conceptclarity.concept;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ExplanationDraft {

    private final String title;
    private final List<ExplanationSection> sections;
    private final List<String> followUps;

    @JsonCreator
    public ExplanationDraft(
            @JsonProperty("title") String title,
            @JsonProperty("sections") List<ExplanationSection> sections,
            @JsonProperty("followUps") List<String> followUps
    ) {
        this.title = title;
        this.sections = sections;
        this.followUps = followUps;
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
}
