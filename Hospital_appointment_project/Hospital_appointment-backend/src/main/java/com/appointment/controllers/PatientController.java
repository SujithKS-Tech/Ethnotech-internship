package com.appointment.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @PostMapping("/book-appointment")
    @PreAuthorize("hasRole('PATIENT')")
    public String bookAppointment() {
        return "Appointment booked successfully";
    }

    
}
