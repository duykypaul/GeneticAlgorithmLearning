package com.duykypaul.steel;

import java.time.LocalDate;

public class Machine {
    public static int MAX_MINUTES_REMAINING = 480;
    public static int MIN_MINUTES_REMAINING = 0;

    /**
     * blade thickness
     */
    private int bladeThickness;
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

    public Machine(int bladeThickness, int frequency, LocalDate date, int minutesRemaining) {
        this.bladeThickness = bladeThickness;
        this.frequency = frequency;
        this.freeDate = date;
        this.minutesRemaining = minutesRemaining;
    }

    public int getBladeThickness() {
        return bladeThickness;
    }

    public void setBladeThickness(int bladeThickness) {
        this.bladeThickness = bladeThickness;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public LocalDate getFreeDate() {
        return freeDate;
    }

    public void setFreeDate(LocalDate freeDate) {
        this.freeDate = freeDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Machine machine = (Machine) o;

        if (bladeThickness != machine.bladeThickness) return false;
        if (frequency != machine.frequency) return false;
        if (minutesRemaining != machine.minutesRemaining) return false;
        return freeDate.equals(machine.freeDate);
    }

    @Override
    public int hashCode() {
        int result = bladeThickness;
        result = 31 * result + frequency;
        result = 31 * result + freeDate.hashCode();
        result = 31 * result + minutesRemaining;
        return result;
    }
}
