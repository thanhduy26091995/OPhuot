package com.thanhduy.ophuot.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by buivu on 08/03/2017.
 */

public class Homestay implements Serializable {
    private Map<String, Object> address;
    private String description;
    private Map<String, Object> details;
    private String id;
    private ArrayList<String> images;
    private String name;
    private String postBy;
    private String price;
    private float rating;
    private String type;
    private long createAt;
    private int districtId;
    private int provinceId;
    private Comments comments;
    private Map<String, Boolean> favorite;

    public Homestay() {
    }

    public Homestay(Map<String, Object> address, String description, Map<String, Object> details,
                    String id, ArrayList<String> images, String name, String postBy, String price, float rating, String type) {
        this.address = address;
        this.description = description;
        this.details = details;
        this.id = id;
        this.images = images;
        this.name = name;
        this.postBy = postBy;
        this.price = price;
        this.rating = rating;
        this.type = type;
    }

    public Homestay(Map<String, Object> address, String description, Map<String, Object> details,
                    String name, String postBy, String price, float rating, String type, long createAt) {
        this.address = address;
        this.description = description;
        this.details = details;
        this.name = name;
        this.postBy = postBy;
        this.price = price;
        this.rating = rating;
        this.type = type;
        this.createAt = createAt;
    }

    //getter and setter


    public Map<String, Boolean> getFavorite() {
        return favorite;
    }

    public void setFavorite(Map<String, Boolean> favorite) {
        this.favorite = favorite;
    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
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

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public Map<String, Object> getAddress() {
        return address;
    }

    public void setAddress(Map<String, Object> address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostBy() {
        return postBy;
    }

    public void setPostBy(String postBy) {
        this.postBy = postBy;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
