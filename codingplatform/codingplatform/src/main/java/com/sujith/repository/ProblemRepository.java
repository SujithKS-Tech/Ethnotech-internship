package com.sujith.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sujith.model.Problem;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
}
