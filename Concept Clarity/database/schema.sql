USE `concept clarity`;

-- Run this inside your existing MySQL schema named `concept clarity`.
-- The backticks are required because the schema name contains a space.
CREATE TABLE IF NOT EXISTS concept_explanations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    concept VARCHAR(120) NOT NULL,
    level VARCHAR(30) NOT NULL,
    explanation_type VARCHAR(40) NOT NULL,
    context VARCHAR(500),
    title VARCHAR(200) NOT NULL,
    explanation_json TEXT NOT NULL,
    source VARCHAR(120) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id)
);
