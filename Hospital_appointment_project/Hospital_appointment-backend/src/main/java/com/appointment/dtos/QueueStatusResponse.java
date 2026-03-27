package com.appointment.dtos;

public class QueueStatusResponse {

    private int queueNumber;
    private long patientsAhead;
    private Integer currentServing;

    public int getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(int queueNumber) {
        this.queueNumber = queueNumber;
    }

    public long getPatientsAhead() {
        return patientsAhead;
    }

    public void setPatientsAhead(long patientsAhead) {
        this.patientsAhead = patientsAhead;
    }

    public Integer getCurrentServing() {
        return currentServing;
    }

    public void setCurrentServing(Integer currentServing) {
        this.currentServing = currentServing;
    }
}

