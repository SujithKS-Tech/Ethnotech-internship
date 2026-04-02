package com.appointment.controllers;


//import org.hibernate.query.Page;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appointment.dtos.AppointmentResponse;
import com.appointment.dtos.CreateAppointmentRequest;
import com.appointment.dtos.QueueStatusResponse;
import com.appointment.service.AppointmentService;



@RestController
@RequestMapping("/patient/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // Book appointment
    @PostMapping
    public String bookAppointment(@RequestBody CreateAppointmentRequest request) {

        return appointmentService.bookAppointment(request);
    }

    // Get patient appointments with pagination
    @GetMapping
    public Page<AppointmentResponse> getAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return appointmentService.getPatientAppointments(page, size);
    }

    // Cancel appointment
    @DeleteMapping("/{id}")
    public String cancelAppointment(@PathVariable Long id) {

        return appointmentService.cancelAppointment(id);
    }

    // Check queue status
    @GetMapping("/queue-status/{appointmentId}")
    public QueueStatusResponse getQueueStatus(@PathVariable Long appointmentId) {

        return appointmentService.getQueueStatus(appointmentId);
    }
}
