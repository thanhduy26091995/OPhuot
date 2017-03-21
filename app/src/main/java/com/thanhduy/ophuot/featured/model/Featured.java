package com.thanhduy.ophuot.featured.model;

/**
 * Created by buivu on 18/03/2017.
 */

public class Featured {
    private String provinceId;
    private String districtId;
    private int imageId;
    private String title;

    public Featured() {
    }

    public Featured(String provinceId, String districtId, int imageId, String title) {
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.imageId = imageId;
        this.title = title;
    }
    //getter and setter

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
