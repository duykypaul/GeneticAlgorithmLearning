package com.duykypaul.kltn;

public class FastCutBean {
    private Integer numberMaterial;
    private Double rateRemain;
    private int[] arrIndexStockUsed;
    private int remain;
    private int stockUsed;

    public FastCutBean(Integer numberMaterial, Double rateRemain, int[] arrIndexStockUsed, int remain, int stockUsed) {
        this.numberMaterial = numberMaterial;
        this.rateRemain = rateRemain;
        this.arrIndexStockUsed = arrIndexStockUsed;
        this.remain = remain;
        this.stockUsed = stockUsed;
    }

    public Integer getNumberMaterial() {
        return numberMaterial;
    }

    public void setNumberMaterial(Integer numberMaterial) {
        this.numberMaterial = numberMaterial;
    }

    public Double getRateRemain() {
        return rateRemain;
    }

    public void setRateRemain(Double rateRemain) {
        this.rateRemain = rateRemain;
    }

    public int[] getArrIndexStockUsed() {
        return arrIndexStockUsed;
    }

    public void setArrIndexStockUsed(int[] arrIndexStockUsed) {
        this.arrIndexStockUsed = arrIndexStockUsed;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    public int getStockUsed() {
        return stockUsed;
    }

    public void setStockUsed(int stockUsed) {
        this.stockUsed = stockUsed;
    }
}
