package com.duykypaul.arn;

import java.time.LocalDate;

public class Material {
    private int quantity;
    private int length;

    public Material(int quantity, int length) {
        this.quantity = quantity;
        this.length = length;
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
}
