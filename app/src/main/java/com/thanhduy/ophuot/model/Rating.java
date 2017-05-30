package com.thanhduy.ophuot.model;

/**
 * Created by buivu on 30/05/2017.
 */

public class Rating {
    private String id;
    private long rating;

    public Rating() {
    }

    public Rating(String id, long rating) {
        this.id = id;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }
}
