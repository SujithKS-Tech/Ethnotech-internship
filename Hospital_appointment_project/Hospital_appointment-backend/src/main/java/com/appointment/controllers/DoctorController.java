package com.appointment.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @PostMapping("/create-schedule")
    @PreAuthorize("hasRole('DOCTOR')")
    public String createSchedule() {
        return "Schedule created successfully";
    }
}
