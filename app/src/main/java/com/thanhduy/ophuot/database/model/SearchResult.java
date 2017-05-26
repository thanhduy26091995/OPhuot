package com.thanhduy.ophuot.database.model;

/**
 * Created by buivu on 24/05/2017.
 */

public class SearchResult {
    private String name;
    private Integer provinceId, districtId;

    public SearchResult() {
    }

    public SearchResult(String name, Integer provinceId, Integer districtId) {
        this.name = name;
        this.provinceId = provinceId;
        this.districtId = districtId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }
}
