package com.sujith.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sujith.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}