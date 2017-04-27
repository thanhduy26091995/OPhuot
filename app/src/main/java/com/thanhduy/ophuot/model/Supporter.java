package com.thanhduy.ophuot.model;

/**
 * Created by buivu on 21/04/2017.
 */

public class Supporter {
    private String facebook;
    private String name;
    private String note;
    private String phone;
    private long provinceId;

    public Supporter() {
    }

    public Supporter(String facebook, String name, String note, String phone) {
        this.facebook = facebook;
        this.name = name;
        this.note = note;
        this.phone = phone;
    }

    public long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(long provinceId) {
        this.provinceId = provinceId;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
