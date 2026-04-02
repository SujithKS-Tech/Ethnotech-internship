package com.appointment.dtos;

import lombok.Data;

@Data
public class CreateAppointmentRequest {

    private Long doctorId;
    private Long scheduleId;
	public Long getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}
	public Long getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}
    
    

}
