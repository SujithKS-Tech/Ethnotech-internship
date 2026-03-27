package com.appointment.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appointment.entities.Doctor;
import com.appointment.entities.DoctorSchedule;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
	List<DoctorSchedule> findByDoctorId(Long doctorId);
	List<DoctorSchedule> findByDoctor(Doctor doctor);
}
