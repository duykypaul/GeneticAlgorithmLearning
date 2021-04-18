package com.duykypaul.kltn;

public class FastCutBean {
    private Integer numberMaterial;
    private Double rateRemain;
    private int[] arrIndexStockUsed;

    public FastCutBean(Integer numberMaterial, Double rateRemain, int[] arrIndexStockUsed) {
        this.numberMaterial = numberMaterial;
        this.rateRemain = rateRemain;
        this.arrIndexStockUsed = arrIndexStockUsed;
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
}
