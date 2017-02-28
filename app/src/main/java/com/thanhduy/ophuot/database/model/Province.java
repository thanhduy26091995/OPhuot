package com.thanhduy.ophuot.database.model;

/**
 * Created by buivu on 08/02/2017.
 */

public class Province {
    private int provinceId;
    private String provinceName;

    public Province() {
    }

    public Province(int provinceId, String provinceName) {
        this.provinceId = provinceId;
        this.provinceName = provinceName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
