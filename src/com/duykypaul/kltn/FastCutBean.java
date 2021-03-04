package com.duykypaul.kltn;

public class FastCutBean {
    private Integer numberMaterial;
    private Integer remain;
    private int[] arrIndexStockUsed;

    public FastCutBean(Integer numberMaterial, Integer remain, int[] arrIndexStockUsed) {
        this.numberMaterial = numberMaterial;
        this.remain = remain;
        this.arrIndexStockUsed = arrIndexStockUsed;
    }

    public Integer getNumberMaterial() {
        return numberMaterial;
    }

    public void setNumberMaterial(Integer numberMaterial) {
        this.numberMaterial = numberMaterial;
    }

    public Integer getRemain() {
        return remain;
    }

    public void setRemain(Integer remain) {
        this.remain = remain;
    }

    public int[] getArrIndexStockUsed() {
        return arrIndexStockUsed;
    }

    public void setArrIndexStockUsed(int[] arrIndexStockUsed) {
        this.arrIndexStockUsed = arrIndexStockUsed;
    }
}
