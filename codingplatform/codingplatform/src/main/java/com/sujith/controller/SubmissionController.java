package com.sujith.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sujith.model.Submission;
import com.sujith.model.User;
import com.sujith.repository.SubmissionRepository;
import com.sujith.repository.UserRepository;
import com.sujith.service.CodeExecutor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/submit")
public class SubmissionController {

    @Autowired
    private CodeExecutor executor;

    @Autowired
    private SubmissionRepository repo;

    @Autowired
    private UserRepository userRepo;

    @PostMapping
    public Submission submit(@RequestBody Submission sub){

        String result = executor.runCode(sub.getCode());

        if(result.equals("expected_output")){
            sub.setStatus("PASS");
        } else {
            sub.setStatus("FAIL");
        }

        return repo.save(sub);
    }

    // 🏆 LEADERBOARD API
    @GetMapping("/leaderboard")
    public List<User> leaderboard(){
        return userRepo.findAll(Sort.by(Sort.Direction.DESC, "score"));
    }
}