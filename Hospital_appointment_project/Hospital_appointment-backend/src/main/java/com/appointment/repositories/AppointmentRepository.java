package com.appointment.repositories;

//import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appointment.entities.Appointment;
import com.appointment.entities.Doctor;
import com.appointment.entities.DoctorSchedule;
import com.appointment.entities.User;
import com.appointment.enums.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>{
	List<Appointment> findByPatientId(Long patientId);
	List<Appointment> findByDoctorId(Long doctorId);
	List<Appointment> findByScheduleId(Long scheduleId);
	
	int countBySchedule(DoctorSchedule schedule);
	
	List<Appointment> findByPatient(User patient);
	
	List<Appointment> findByDoctorAndStatus(Doctor doctor, AppointmentStatus status);
	
	List<Appointment> findByScheduleOrderByQueueNumber(DoctorSchedule schedule);

	long countByScheduleAndQueueNumberLessThanAndStatus(
	        DoctorSchedule schedule,
	        int queueNumber,
	        AppointmentStatus status
	);
 
	Page<Appointment> findByPatient(User patient, Pageable pageable);
	

	
}
