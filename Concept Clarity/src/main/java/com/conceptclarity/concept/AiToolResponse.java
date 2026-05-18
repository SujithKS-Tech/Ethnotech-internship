package com.conceptclarity.concept;

import java.util.List;

public class AiToolResponse {

    private String title;
    private String tool;
    private List<ExplanationSection> sections;
    private String source;

    public AiToolResponse(String title, String tool, List<ExplanationSection> sections, String source) {
        this.title = title;
        this.tool = tool;
        this.sections = sections;
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public String getTool() {
        return tool;
    }

    public List<ExplanationSection> getSections() {
        return sections;
    }

    public String getSource() {
        return source;
    }
}
