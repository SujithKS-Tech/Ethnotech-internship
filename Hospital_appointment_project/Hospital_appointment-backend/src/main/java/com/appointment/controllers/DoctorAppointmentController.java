package com.appointment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appointment.service.AppointmentService;

@RestController
@RequestMapping("/doctor/appointments")
public class DoctorAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PutMapping("/{id}/start")
    public String startAppointment(@PathVariable Long id) {

        return appointmentService.startAppointment(id);
    }

    @PutMapping("/{id}/complete")
    public String completeAppointment(@PathVariable Long id) {

        return appointmentService.completeAppointment(id);
    }
}

