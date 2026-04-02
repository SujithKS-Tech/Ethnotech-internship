package com.appointment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.PostMapping;

import com.appointment.dtos.AppointmentResponse;
import com.appointment.dtos.CreateAppointmentRequest;
import com.appointment.dtos.QueueResponse;
import com.appointment.dtos.QueueStatusResponse;
import com.appointment.entities.Appointment;
import com.appointment.entities.Doctor;
import com.appointment.entities.DoctorSchedule;
import com.appointment.entities.User;
import com.appointment.enums.AppointmentStatus;
import com.appointment.repositories.AppointmentRepository;
import com.appointment.repositories.DoctorRepository;
import com.appointment.repositories.DoctorScheduleRepository;
import com.appointment.repositories.UserRepository;
@Service
public class AppointmentService {

	 @Autowired
	    private AppointmentRepository appointmentRepository;

	    @Autowired
	    private DoctorScheduleRepository scheduleRepository;

	    @Autowired
	    private DoctorRepository doctorRepository;

	    @Autowired
	    private UserRepository userRepository;
	    
	    @Autowired
	    private NotificationService notificationService;
	    
	    
	    
	   
	    public String bookAppointment(CreateAppointmentRequest request) {

	        // Step 1: Get logged-in patient email
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        String email = authentication.getName();

	        // Step 2: Find patient user
	        User patient = userRepository.findByEmail(email)
	                .orElseThrow(() -> new RuntimeException("Patient not found"));

	        // Step 3: Fetch doctor
	        Doctor doctor = doctorRepository.findById(request.getDoctorId())
	                .orElseThrow(() -> new RuntimeException("Doctor not found"));

	        // Step 4: Fetch schedule
	        DoctorSchedule schedule = scheduleRepository.findById(request.getScheduleId())
	                .orElseThrow(() -> new RuntimeException("Schedule not found"));

	        // Step 5: Count existing appointments
	        int currentAppointments = appointmentRepository.countBySchedule(schedule);

	        // Step 6: Check schedule capacity
	        if (currentAppointments >= schedule.getMaxAppointments()) {
	            throw new RuntimeException("Schedule is full");
	        }

	        // Step 7: Generate queue number
	        int queueNumber = currentAppointments + 1;

	        // Step 8: Create appointment
	        Appointment appointment = new Appointment();

	        appointment.setPatient(patient);
	        appointment.setDoctor(doctor);
	        appointment.setSchedule(schedule);
	        appointment.setQueueNumber(queueNumber);
	        appointment.setStatus(AppointmentStatus.BOOKED);
	        appointment.setCreatedAt(LocalDateTime.now());

	        // Step 9: Save appointment
	        appointmentRepository.save(appointment);
	        
	        notificationService.sendNotification(
	                patient,
	                "Your appointment has been booked successfully with Dr. "
	                + doctor.getUser().getName()
	        );

	        return "Appointment booked successfully. Queue Number: " + queueNumber;
	    }
	    
	    public Page<AppointmentResponse> getPatientAppointments(int page, int size) {

	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        String email = authentication.getName();

	        User patient = userRepository.findByEmail(email)
	                .orElseThrow(() -> new RuntimeException("Patient not found"));

	        Pageable pageable = PageRequest.of(page, size);

	        Page<Appointment> appointmentPage =
	                appointmentRepository.findByPatient(patient, pageable);

	        return appointmentPage.map(appointment -> {

	            AppointmentResponse response = new AppointmentResponse();

	            response.setAppointmentId(appointment.getId());
	            response.setDoctorName(appointment.getDoctor().getUser().getName());
	            response.setDate(appointment.getSchedule().getDate());
	            response.setStartTime(appointment.getSchedule().getStartTime());
	            response.setQueueNumber(appointment.getQueueNumber());
	            response.setStatus(appointment.getStatus().name());

	            return response;
	        });
	    }
	    
	    public String cancelAppointment(Long appointmentId) {

	        Appointment appointment = appointmentRepository.findById(appointmentId)
	                .orElseThrow(() -> new RuntimeException("Appointment not found"));

	        appointment.setStatus(AppointmentStatus.CANCELLED);

	        appointmentRepository.save(appointment);

	        return "Appointment cancelled successfully";
	    }
	    
	    public String startAppointment(Long appointmentId) {

	        Appointment appointment = appointmentRepository.findById(appointmentId)
	                .orElseThrow(() -> new RuntimeException("Appointment not found"));

	        appointment.setStatus(AppointmentStatus.IN_PROGRESS);

	        appointmentRepository.save(appointment);

	        return "Consultation started";
	    }

	    public String completeAppointment(Long appointmentId) {

	        Appointment appointment = appointmentRepository.findById(appointmentId)
	                .orElseThrow(() -> new RuntimeException("Appointment not found"));

	        appointment.setStatus(AppointmentStatus.COMPLETED);

	        appointmentRepository.save(appointment);

	        return "Consultation completed";
	    }
	    
	    public QueueStatusResponse getQueueStatus(Long appointmentId) {

	        Appointment appointment = appointmentRepository.findById(appointmentId)
	                .orElseThrow(() -> new RuntimeException("Appointment not found"));

	        DoctorSchedule schedule = appointment.getSchedule();

	        int queueNumber = appointment.getQueueNumber();

	        long patientsAhead =
	                appointmentRepository.countByScheduleAndQueueNumberLessThanAndStatus(
	                        schedule,
	                        queueNumber,
	                        AppointmentStatus.BOOKED
	                );

	        List<Appointment> appointments =
	                appointmentRepository.findByScheduleOrderByQueueNumber(schedule);

	        Integer currentServing = null;

	        for (Appointment a : appointments) {
	            if (a.getStatus() == AppointmentStatus.IN_PROGRESS) {
	                currentServing = a.getQueueNumber();
	                break;
	            }
	        }

	        QueueStatusResponse response = new QueueStatusResponse();

	        response.setQueueNumber(queueNumber);
	        response.setPatientsAhead(patientsAhead);
	        response.setCurrentServing(currentServing);

	        return response;
	    }
	    
	    public List<QueueResponse> getDoctorQueue(Long scheduleId) {

	        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
	                .orElseThrow(() -> new RuntimeException("Schedule not found"));

	        List<Appointment> appointments =
	                appointmentRepository.findByScheduleOrderByQueueNumber(schedule);

	        List<QueueResponse> queueList = new ArrayList<>();

	        for (Appointment appointment : appointments) {

	            QueueResponse response = new QueueResponse();

	            response.setQueueNumber(appointment.getQueueNumber());
	            response.setPatientName(appointment.getPatient().getName());
	            response.setStatus(appointment.getStatus().name());

	            queueList.add(response);
	        }

	        return queueList;
	    }


}