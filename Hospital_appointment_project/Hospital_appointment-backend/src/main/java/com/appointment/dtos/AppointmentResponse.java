package com.appointment.dtos;


import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class AppointmentResponse {

    private Long appointmentId;
    private String doctorName;
    private LocalDate date;
    private LocalTime startTime;
    private int queueNumber;
    private String status;
	public Long getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(Long appointmentId) {
		this.appointmentId = appointmentId;
	}
	public String getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}
	public int getQueueNumber() {
		return queueNumber;
	}
	public void setQueueNumber(int queueNumber) {
		this.queueNumber = queueNumber;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
    
    

}
