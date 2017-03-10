package com.thanhduy.ophuot.model;

/**
 * Created by buivu on 09/03/2017.
 */

public class MyPost {
    private int districtId;
    private int provinceId;
    private String homestayId;

    public MyPost() {
    }

    public MyPost(int districtId, int provinceId, String homestayId) {
        this.districtId = districtId;
        this.provinceId = provinceId;
        this.homestayId = homestayId;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getHomestayId() {
        return homestayId;
    }

    public void setHomestayId(String homestayId) {
        this.homestayId = homestayId;
    }
}
