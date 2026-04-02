package com.appointment.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.appointment.entities.Appointment;
import com.appointment.entities.Doctor;
import com.appointment.entities.User;
import com.appointment.enums.AppointmentStatus;
import com.appointment.repositories.AppointmentRepository;

@Service
public class ReminderService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void sendAppointmentReminders() {

        System.out.println("Checking appointments for reminders...");

        List<Appointment> appointments = appointmentRepository.findAll();

        for (Appointment appointment : appointments) {

            if (appointment.getStatus() == AppointmentStatus.BOOKED) {

                User patient = appointment.getPatient();
                Doctor doctor = appointment.getDoctor();

                String message = "Reminder: Your appointment with Dr. "
                        + doctor.getUser().getName()
                        + " is scheduled soon.";

                notificationService.sendNotification(patient, message);
            }
        }
    }
}
