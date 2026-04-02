package com.appointment.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.appointment.dtos.CreateScheduleRequest;
import com.appointment.dtos.ScheduleResponse;
import com.appointment.dtos.UpdateScheduleRequest;
import com.appointment.service.ScheduleService;

@RestController
@RequestMapping("/doctor/schedules")
public class DoctorScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping
    public String createSchedule(@RequestBody CreateScheduleRequest request) {

        return scheduleService.createSchedule(request);

    }
    
    @GetMapping
    public List<ScheduleResponse> getSchedules() {

        return scheduleService.getDoctorSchedules();

    }
    
    @PutMapping("/{id}")
    public String updateSchedule(@PathVariable Long id,
                                 @RequestBody UpdateScheduleRequest request) {

        return scheduleService.updateSchedule(id, request);
    }
    
    @DeleteMapping("/{id}")
    public String deleteSchedule(@PathVariable Long id) {

        return scheduleService.deleteSchedule(id);
    }
}