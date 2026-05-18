package com.conceptclarity.concept;

public class ExplanationSection {

    private String heading;
    private String body;

    public ExplanationSection() {
    }

    public ExplanationSection(String heading, String body) {
        this.heading = heading;
        this.body = body;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
