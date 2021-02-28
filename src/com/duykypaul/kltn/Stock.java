package com.duykypaul.kltn;

import java.time.LocalDate;

public class Stock {
    private int quantity;
    private int length;
    private LocalDate importDate;

    public Stock(int quantity, int length, LocalDate importDate) {
        this.quantity = quantity;
        this.length = length;
        this.importDate = importDate;
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

    public LocalDate getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDate importDate) {
        this.importDate = importDate;
    }
}
