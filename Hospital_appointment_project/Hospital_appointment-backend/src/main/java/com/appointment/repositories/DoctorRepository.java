package com.appointment.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appointment.entities.Doctor;
import com.appointment.entities.User;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>{
	
	 Optional<Doctor> findByUser(User user);
}
