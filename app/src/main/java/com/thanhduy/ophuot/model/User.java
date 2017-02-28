package com.thanhduy.ophuot.model;

import java.util.HashMap;

/**
 * Created by buivu on 28/02/2017.
 */

public class User {
    private String avatar;
    private long createAt;
    private String description;
    private String email;
    private int gender;
    private String name;
    private String phone;
    private HashMap<String, Object> address = new HashMap<>();
    private HashMap<String, Boolean> favourite;
    private HashMap<String, Boolean> post;

    public User() {
    }

    public User(String avatar, long createAt, String description, String email, int gender, String name, String phone,
                HashMap<String, Object> address, HashMap<String, Boolean> favourite, HashMap<String, Boolean> post) {
        this.avatar = avatar;
        this.createAt = createAt;
        this.description = description;
        this.email = email;
        this.gender = gender;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.favourite = favourite;
        this.post = post;
    }
    //getter and setter

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public HashMap<String, Object> getAddress() {
        return address;
    }

    public void setAddress(HashMap<String, Object> address) {
        this.address = address;
    }

    public HashMap<String, Boolean> getFavourite() {
        return favourite;
    }

    public void setFavourite(HashMap<String, Boolean> favourite) {
        this.favourite = favourite;
    }

    public HashMap<String, Boolean> getPost() {
        return post;
    }

    public void setPost(HashMap<String, Boolean> post) {
        this.post = post;
    }
}
