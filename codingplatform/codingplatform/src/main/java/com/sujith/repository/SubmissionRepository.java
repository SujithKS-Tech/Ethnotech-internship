package com.sujith.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sujith.model.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
