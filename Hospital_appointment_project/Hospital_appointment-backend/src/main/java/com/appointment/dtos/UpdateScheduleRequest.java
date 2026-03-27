package com.appointment.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class UpdateScheduleRequest {

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxAppointments;
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
	public LocalTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}
	public int getMaxAppointments() {
		return maxAppointments;
	}
	public void setMaxAppointments(int maxAppointments) {
		this.maxAppointments = maxAppointments;
	}
    
    

}