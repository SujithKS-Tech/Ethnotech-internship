package com.appointment.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appointment.dtos.QueueResponse;
import com.appointment.service.AppointmentService;

@RestController
@RequestMapping("/doctor/appointments")
public class DoctorQueueController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/queue/{scheduleId}")
    public List<QueueResponse> getDoctorQueue(@PathVariable Long scheduleId) {

        return appointmentService.getDoctorQueue(scheduleId);

    }
}
