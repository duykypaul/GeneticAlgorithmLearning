package com.duykypaul.kltn;

import java.time.LocalDate;

public class Stack {
    private int quantity;
    private int length;
    private LocalDate deliveryDate;

    public Stack(int quantity, int length, LocalDate deliveryDate) {
        this.quantity = quantity;
        this.length = length;
        this.deliveryDate = deliveryDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
