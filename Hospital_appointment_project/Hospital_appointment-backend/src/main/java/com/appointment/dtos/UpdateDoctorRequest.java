package com.appointment.dtos;

import lombok.Data;

@Data
public class UpdateDoctorRequest {

    private String specialization;
    private int experienceYears;
    private Long consultationFee;
	public String getSpecialization() {
		return specialization;
	}
	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
	public int getExperienceYears() {
		return experienceYears;
	}
	public void setExperienceYears(int experienceYears) {
		this.experienceYears = experienceYears;
	}
	public Long getConsultationFee() {
		return consultationFee;
	}
	public void setConsultationFee(Long consultationFee) {
		this.consultationFee = consultationFee;
	}

    
    
}