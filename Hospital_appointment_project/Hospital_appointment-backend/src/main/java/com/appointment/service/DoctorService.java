package com.appointment.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.appointment.dtos.CreateDoctorRequest;
import com.appointment.dtos.DoctorResponse;
import com.appointment.dtos.UpdateDoctorRequest;
import com.appointment.entities.Doctor;
import com.appointment.entities.User;
import com.appointment.enums.Role;
import com.appointment.repositories.DoctorRepository;
import com.appointment.repositories.UserRepository;



@Service
public class DoctorService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String createDoctor(CreateDoctorRequest request) {

        // Step 1: Create User account
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        // Encrypt password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Assign role
        user.setRole(Role.DOCTOR);

        // Save user
        User savedUser = userRepository.save(user);

        // Step 2: Create Doctor profile
        Doctor doctor = new Doctor();

        doctor.setUser(savedUser);
        doctor.setSpecialization(request.getSpecialization());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setConsultationFee(request.getConsultationFee());

        // Save doctor
        doctorRepository.save(doctor);

        return "Doctor created successfully";
        

    }

	public List<DoctorResponse> getAllDoctors() {
		List<Doctor> doctors = doctorRepository.findAll();

        List<DoctorResponse> responseList = new ArrayList<>();

        for (Doctor doctor1 : doctors) {

            DoctorResponse response = new DoctorResponse();

            response.setDoctorId(doctor1.getId());
            response.setName(doctor1.getUser().getName());
            response.setEmail(doctor1.getUser().getEmail());
            response.setSpecialization(doctor1.getSpecialization());
            response.setExperienceYears(doctor1.getExperienceYears());
            response.setConsultationFee(doctor1.getConsultationFee());

            responseList.add(response);
        }

        return responseList;
    }
	
	public String updateDoctor(Long doctorId, UpdateDoctorRequest request) {

	    Doctor doctor = doctorRepository.findById(doctorId)
	            .orElseThrow(() -> new RuntimeException("Doctor not found"));

	    doctor.setSpecialization(request.getSpecialization());
	    doctor.setExperienceYears(request.getExperienceYears());
	    doctor.setConsultationFee(request.getConsultationFee());

	    doctorRepository.save(doctor);

	    return "Doctor updated successfully";
	}
	
	public String deleteDoctor(Long doctorId) {

	    Doctor doctor = doctorRepository.findById(doctorId)
	            .orElseThrow(() -> new RuntimeException("Doctor not found"));

	    doctorRepository.delete(doctor);

	    return "Doctor deleted successfully";
	}
	
}