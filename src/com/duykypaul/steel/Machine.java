package com.duykypaul.steel;

import java.sql.Time;
import java.util.Date;

public class Machine {
    /**
     * time to complete one cut
     */
    private int frequency;
    /**
     * free days
     */
    private Date date;

    /**
     * number of free minutes left for the day
     */
    private int minutesRemaining;

    public Machine(int frequency, Date date, int minutesRemaining) {
        this.frequency = frequency;
        this.date = date;
        this.minutesRemaining = minutesRemaining;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getMinutesRemaining() {
        return minutesRemaining;
    }

    public void setMinutesRemaining(int minutesRemaining) {
        this.minutesRemaining = minutesRemaining;
    }
}
