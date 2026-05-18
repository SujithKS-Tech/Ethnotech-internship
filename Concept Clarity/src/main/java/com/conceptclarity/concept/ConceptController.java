package com.conceptclarity.concept;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/concepts")
public class ConceptController {

    private final ConceptService conceptService;

    public ConceptController(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    @PostMapping("/explain")
    public ConceptResponse explain(@Valid @RequestBody ConceptRequest request) {
        return conceptService.explain(request);
    }

    @PostMapping("/ai-tool")
    public AiToolResponse aiTool(@Valid @RequestBody AiToolRequest request) {
        return conceptService.aiTool(request);
    }

    @GetMapping("/history")
    public List<ConceptHistoryItem> history() {
        return conceptService.history();
    }
}
