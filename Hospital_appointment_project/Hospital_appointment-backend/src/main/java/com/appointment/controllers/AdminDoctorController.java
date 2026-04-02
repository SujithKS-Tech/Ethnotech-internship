package com.appointment.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appointment.dtos.CreateDoctorRequest;
import com.appointment.dtos.DoctorResponse;
import com.appointment.dtos.UpdateDoctorRequest;
import com.appointment.service.DoctorService;

@RestController
@RequestMapping("/admin/doctors")
public class AdminDoctorController {

    @Autowired
    private DoctorService doctorService;

    // Add Doctor API
    @PostMapping
    public String createDoctor(@RequestBody CreateDoctorRequest request) {
        return doctorService.createDoctor(request);
    }

    @GetMapping
    public List<DoctorResponse> getAllDoctors() {

        return doctorService.getAllDoctors();

    }
    
    @PutMapping("/{id}")
    public String updateDoctor(@PathVariable Long id,
                               @RequestBody UpdateDoctorRequest request) {

        return doctorService.updateDoctor(id, request);
    }
    
    @DeleteMapping("/{id}")
    public String deleteDoctor(@PathVariable Long id) {

        return doctorService.deleteDoctor(id);

    }
}