package com.sujith.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sujith.model.Problem;
import com.sujith.repository.ProblemRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProblemRepository repo;

    @PostMapping("/problem")
    public Problem addProblem(@RequestBody Problem problem){
        return repo.save(problem);
    }

    @GetMapping("/problems")
    public List<Problem> getAll(){
        return repo.findAll();
    }
}