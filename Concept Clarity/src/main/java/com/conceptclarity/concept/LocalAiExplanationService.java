package com.conceptclarity.concept;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LocalAiExplanationService {

    public ExplanationDraft generate(ConceptRequest request) {
        String concept = titleCase(clean(request.getConcept()));
        String level = cleanOrDefault(request.getLevel(), "Beginner");
        String type = cleanOrDefault(request.getExplanationType(), "Detailed explanation");
        String context = clean(request.getContext());
        LevelProfile profile = profileFor(level);
        TopicProfile topic = topicFor(concept);

        String baseDefinition = topic.definition();
        String contextLine = context.isBlank()
                ? "This answer uses " + profile.tone() + "."
                : "This answer is tailored to your context: " + context + ".";

        if ("Definition".equalsIgnoreCase(type)) {
            return new ExplanationDraft(
                    concept + " - " + level + " Definition",
                    List.of(
                            new ExplanationSection("Meaning", baseDefinition),
                            new ExplanationSection("In Simple Words", topic.simpleWords()),
                            new ExplanationSection("Why It Matters", topic.whyItMatters()),
                            new ExplanationSection("Example", topic.example()),
                            new ExplanationSection("Learning Note", contextLine)
                    ),
                    List.of(
                            "Can you give me one real-life example of " + concept + "?",
                            "How is " + concept + " different from a related concept?",
                            "What are the most important keywords in " + concept + "?"
                    )
            );
        }

        if ("Step-by-step".equalsIgnoreCase(type)) {
            return new ExplanationDraft(
                    concept + " - " + level + " Step-by-step",
                    List.of(
                            new ExplanationSection("Step 1: Core Idea", baseDefinition),
                            new ExplanationSection("Step 2: Main Parts", topic.keyPoints()),
                            new ExplanationSection("Step 3: Example", topic.example()),
                            new ExplanationSection("Step 4: Common Confusion", topic.commonConfusion()),
                            new ExplanationSection("Step 5: Check Understanding", "Explain " + concept + " in one sentence, then add one example and one reason it matters."),
                            new ExplanationSection("Learning Note", contextLine)
                    ),
                    List.of(
                            "Teach me " + concept + " with a classroom example.",
                            "Give me practice questions on " + concept + ".",
                            "Explain the common mistakes in " + concept + "."
                    )
            );
        }

        return new ExplanationDraft(
                concept + " - " + level + " Explanation",
                List.of(
                        new ExplanationSection("Overview", baseDefinition),
                        new ExplanationSection("How To Think About It", topic.simpleWords() + " " + profile.analogy(concept)),
                        new ExplanationSection("Key Points", topic.keyPoints()),
                        new ExplanationSection("Example", topic.example()),
                        new ExplanationSection("Common Confusion", topic.commonConfusion()),
                        new ExplanationSection("Learning Note", contextLine)
                ),
                List.of(
                        "Explain " + concept + " using an analogy.",
                        "Create short notes for " + concept + ".",
                        "Quiz me on " + concept + "."
                )
        );
    }

    public AiToolResponse runTool(AiToolRequest request, String source) {
        String concept = titleCase(clean(request.getConcept()));
        String level = cleanOrDefault(request.getLevel(), "Beginner");
        String tool = cleanOrDefault(request.getTool(), "study-notes");
        String context = clean(request.getContext());
        TopicProfile topic = topicFor(concept);
        String contextHint = context.isBlank() ? "" : " This is adjusted for: " + context + ".";

        if ("simplify".equalsIgnoreCase(tool)) {
            return new AiToolResponse(
                    concept + " - Simplified Explanation",
                    "simplify",
                    List.of(
                            new ExplanationSection("One-Line Meaning", topic.definition()),
                            new ExplanationSection("Easy Version", topic.simpleWords() + contextHint),
                            new ExplanationSection("Remember This", topic.whyItMatters())
                    ),
                    source
            );
        }

        if ("examples".equalsIgnoreCase(tool)) {
            return new AiToolResponse(
                    concept + " - Real-Life Examples",
                    "examples",
                    List.of(
                            new ExplanationSection("Daily Life Example", topic.example()),
                            new ExplanationSection("Study Example", "In an exam answer, define " + concept + ", list the key points, then explain one example in your own words."),
                            new ExplanationSection("Where You See It", topic.whyItMatters())
                    ),
                    source
            );
        }

        if ("quiz".equalsIgnoreCase(tool)) {
            return new AiToolResponse(
                    "AI Practice Quiz",
                    "quiz",
                    List.of(
                            new ExplanationSection("Question 1", "What is the basic meaning of " + concept + "?"),
                            new ExplanationSection("Question 2", "Name two key points: " + topic.keyPoints()),
                            new ExplanationSection("Question 3", "Why does " + concept + " matter for a " + level.toLowerCase() + " learner?"),
                            new ExplanationSection("Challenge", "Explain this example in your own words: " + topic.example())
                    ),
                    source
            );
        }

        return new AiToolResponse(
                "Smart Study Notes",
                "study-notes",
                List.of(
                        new ExplanationSection("Definition", topic.definition()),
                        new ExplanationSection("Key Points", topic.keyPoints()),
                        new ExplanationSection("Example", topic.example()),
                        new ExplanationSection("Exam Tip", "Write your answer in this order: definition, key points, example, importance, and one common confusion."),
                        new ExplanationSection("Revision Prompt", "Before finishing, ask yourself: can I explain " + concept + " to a beginner?")
                ),
                source
        );
    }

    private TopicProfile topicFor(String concept) {
        String normalized = concept.toLowerCase();

        if (normalized.contains("protein")) {
            return new TopicProfile(
                    "Protein is a nutrient made of amino acids. Your body uses it to build and repair muscles, skin, organs, enzymes, hormones, and many other body tissues.",
                    "Think of protein as the body's building and repair material. After food is digested, protein breaks into amino acids, and the body uses those amino acids where repair or growth is needed.",
                    "Important points: proteins are made of amino acids; they help growth and tissue repair; they support enzymes and hormones; common sources include eggs, pulses, milk, fish, meat, soy, nuts, and beans.",
                    "After exercise, muscles have tiny damage. Protein from food such as eggs, dal, paneer, soy, or chicken supplies amino acids that help repair and strengthen those muscles.",
                    "Protein is not only for bodybuilders. Everyone needs protein, but the amount depends on age, health, activity level, and diet."
            );
        }

        if (normalized.contains("photosynthesis")) {
            return new TopicProfile(
                    "Photosynthesis is the process by which green plants use sunlight, carbon dioxide, and water to make glucose and release oxygen.",
                    "Plants work like natural food makers: leaves capture sunlight, take in carbon dioxide from air, absorb water from roots, and produce sugar for energy.",
                    "Important points: chlorophyll captures light; carbon dioxide enters through stomata; water comes from roots; glucose stores energy; oxygen is released as a by-product.",
                    "A money plant near a window uses sunlight during the day to make food inside its leaves, which helps it grow new stems and leaves.",
                    "Plants do not get food from soil directly. Soil provides water and minerals, but the plant makes its own glucose through photosynthesis."
            );
        }

        if (normalized.contains("java") || normalized.contains("oop") || normalized.contains("object oriented")) {
            return new TopicProfile(
                    "Java OOP means writing Java programs using objects and classes, where data and behavior are grouped together.",
                    "A class is like a design, and an object is a real item made from that design. Methods describe what the object can do, and fields store its data.",
                    "Important points: class, object, encapsulation, inheritance, polymorphism, abstraction, methods, and fields.",
                    "A Student class can store name and roll number, while methods like displayDetails() or calculateGrade() define actions for each Student object.",
                    "A class and an object are not the same. The class is the blueprint; the object is the actual created instance."
            );
        }

        if (normalized.contains("recursion")) {
            return new TopicProfile(
                    "Recursion is a programming technique where a function calls itself to solve smaller versions of the same problem.",
                    "Recursion keeps breaking a problem down until it reaches a simple stopping condition called the base case.",
                    "Important points: recursive call, base case, smaller subproblem, call stack, and return value.",
                    "To calculate factorial(5), recursion computes 5 * factorial(4), then 4 * factorial(3), and continues until factorial(1).",
                    "The most common mistake is forgetting the base case, which can cause infinite calls and a stack overflow."
            );
        }

        if (normalized.contains("blockchain")) {
            return new TopicProfile(
                    "Blockchain is a digital record system where data is stored in linked blocks and shared across many computers.",
                    "Instead of one person controlling the record, many computers keep copies. New records are added in blocks, and old blocks are hard to change.",
                    "Important points: blocks, hashes, decentralization, consensus, transactions, immutability, and transparency.",
                    "In a payment network, a transaction can be grouped into a block, verified by the network, and added to the chain as a permanent record.",
                    "Blockchain and Bitcoin are not the same. Bitcoin uses blockchain, but blockchain can be used for many other record-keeping systems."
            );
        }

        if (normalized.contains("artificial intelligence") || normalized.equals("ai")) {
            return new TopicProfile(
                    "Artificial Intelligence is the ability of computer systems to perform tasks that usually need human intelligence, such as understanding language, recognizing patterns, and making decisions.",
                    "AI learns from data and uses patterns to make predictions, answer questions, recommend actions, or automate tasks.",
                    "Important points: data, model, training, prediction, machine learning, natural language processing, and decision-making.",
                    "A chatbot that answers student questions uses AI to understand the message and generate a useful response.",
                    "AI is not magic or human thinking. It is software using data, rules, models, and probability to produce useful outputs."
            );
        }

        return new TopicProfile(
                concept + " is a topic that becomes clear when you understand its meaning, main parts, purpose, example, and common confusion points.",
                "Start by asking: what is it, why is it used, where do I see it, and what mistake should I avoid?",
                "Important points: definition, purpose, key terms, process or structure, real example, benefits, limits, and common mistakes.",
                "A good example for " + concept + " should show where it appears in real life, what problem it solves, and what result it creates.",
                "Do not memorize only the definition. Connect the definition with an example and one reason it matters."
        );
    }

    private LevelProfile profileFor(String level) {
        if ("Intermediate".equalsIgnoreCase(level)) {
            return new LevelProfile("clear terms, practical examples, and some technical detail", "Understanding %s is like knowing how a tool works and when to choose it.");
        }
        if ("Advanced".equalsIgnoreCase(level)) {
            return new LevelProfile("precise terms, deeper structure, and tradeoffs", "Understanding %s is like analyzing a system by its principles, limits, and design choices.");
        }
        return new LevelProfile("simple language, everyday examples, and no assumed background", "Understanding %s is like learning the purpose of a tool before studying every small part.");
    }

    private String cleanOrDefault(String value, String fallback) {
        String cleaned = clean(value);
        return cleaned.isBlank() ? fallback : cleaned;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    private String titleCase(String value) {
        if (value.isBlank()) {
            return value;
        }

        String[] words = value.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!result.isEmpty()) {
                result.append(" ");
            }
            result.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
        }

        return result.toString();
    }

    private record LevelProfile(String tone, String analogyTemplate) {
        String analogy(String concept) {
            return String.format(analogyTemplate, concept);
        }
    }

    private record TopicProfile(
            String definition,
            String simpleWords,
            String keyPoints,
            String example,
            String commonConfusion,
            String whyItMatters
    ) {
        TopicProfile(String definition, String simpleWords, String keyPoints, String example, String commonConfusion) {
            this(definition, simpleWords, keyPoints, example, commonConfusion, "Understanding this helps you answer exam questions, connect theory to real life, and avoid shallow memorization.");
        }
    }
}
