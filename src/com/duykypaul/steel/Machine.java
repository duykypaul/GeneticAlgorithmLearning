package com.duykypaul.steel;

import java.time.LocalDate;

public class Machine {
    /**
     * time to complete one cut
     */
    private int frequency;
    /**
     * free days
     */
    private LocalDate freeDate;

    /**
     * number of free minutes left for the day
     */
    private int minutesRemaining;

    public Machine(int frequency, LocalDate date, int minutesRemaining) {
        this.frequency = frequency;
        this.freeDate = date;
        this.minutesRemaining = minutesRemaining;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public LocalDate getDate() {
        return freeDate;
    }

    public void setDate(LocalDate date) {
        this.freeDate = date;
    }

    public int getYearFreeDate() {
        return freeDate.getYear();
    }

    public void plusYear() {
        this.freeDate = this.freeDate.plusYears(1);
    }

    public int getMonthDate() {
        return freeDate.getMonthValue();
    }

    public void plusMonth() {
        this.freeDate = this.freeDate.plusMonths(1);
    }

    public int getDateOfFreeDate() {
        return freeDate.getDayOfMonth();
    }

    public void plusDay() {
        this.freeDate = this.freeDate.plusDays(1);
    }

    public int getMinutesRemaining() {
        return minutesRemaining;
    }

    public void setMinutesRemaining(int minutesRemaining) {
        this.minutesRemaining = minutesRemaining;
    }
}
