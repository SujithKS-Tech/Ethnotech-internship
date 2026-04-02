package com.appointment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.appointment.dtos.CreateScheduleRequest;
import com.appointment.dtos.ScheduleResponse;
import com.appointment.dtos.UpdateScheduleRequest;
import com.appointment.entities.Doctor;
import com.appointment.entities.DoctorSchedule;
import com.appointment.entities.User;
import com.appointment.repositories.DoctorRepository;
import com.appointment.repositories.DoctorScheduleRepository;
import com.appointment.repositories.UserRepository;



@Service
public class ScheduleService {
	
	@Autowired
	private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorScheduleRepository scheduleRepository;

    public String createSchedule(CreateScheduleRequest request) {

        // Step 1: Get logged-in user email from JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Step 2: Find doctor by email
        User user =userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        // Step 3: Create schedule
        DoctorSchedule schedule = new DoctorSchedule();

        schedule.setDoctor(doctor);
        schedule.setDate(request.getDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setMaxAppointments(request.getMaxAppointments());
        schedule.setCreatedAt(LocalDateTime.now());

        // Step 4: Save schedule
        scheduleRepository.save(schedule);

        return "Schedule created successfully";
    }
    
    public List<ScheduleResponse> getDoctorSchedules() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        List<DoctorSchedule> schedules = scheduleRepository.findByDoctor(doctor);

        List<ScheduleResponse> responseList = new ArrayList<>();

        for (DoctorSchedule schedule : schedules) {

            ScheduleResponse response = new ScheduleResponse();

            response.setScheduleId(schedule.getId());
            response.setDate(schedule.getDate());
            response.setStartTime(schedule.getStartTime());
            response.setEndTime(schedule.getEndTime());
            response.setMaxAppointments(schedule.getMaxAppointments());

            responseList.add(response);
        }

        return responseList;
    }
    
    public String updateSchedule(Long scheduleId, UpdateScheduleRequest request) {

        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        schedule.setDate(request.getDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setMaxAppointments(request.getMaxAppointments());

        scheduleRepository.save(schedule);

        return "Schedule updated successfully";
    }
    
    public String deleteSchedule(Long scheduleId) {

        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        scheduleRepository.delete(schedule);

        return "Schedule deleted successfully";
    }
}