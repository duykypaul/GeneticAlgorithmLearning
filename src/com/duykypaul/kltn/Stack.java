package com.duykypaul.kltn;

import java.time.LocalDate;

public class Stack {
    private int indexConsign;
    private int indexStack;
    private int quantity;
    private int length;
    private LocalDate deliveryDate;

    public Stack(int indexConsign, int indexStack, int quantity, int length, LocalDate deliveryDate) {
        this.indexConsign = indexConsign;
        this.indexStack = indexStack;
        this.quantity = quantity;
        this.length = length;
        this.deliveryDate = deliveryDate;
    }

    public int getIndexConsign() {
        return indexConsign;
    }

    public void setIndexConsign(int indexConsign) {
        this.indexConsign = indexConsign;
    }

    public int getIndexStack() {
        return indexStack;
    }

    public void setIndexStack(int indexStack) {
        this.indexStack = indexStack;
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
