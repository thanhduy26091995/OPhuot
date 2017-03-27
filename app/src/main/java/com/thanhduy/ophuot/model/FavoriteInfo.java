package com.thanhduy.ophuot.model;

import java.io.Serializable;

/**
 * Created by buivu on 24/03/2017.
 */

public class FavoriteInfo implements Serializable {
    private String favoriteName;
    private long favoriteCount;

    public FavoriteInfo() {
    }

    public FavoriteInfo(String favoriteName, long favoriteCount) {
        this.favoriteName = favoriteName;
        this.favoriteCount = favoriteCount;
    }
    //getter and setter

    public String getFavoriteName() {
        return favoriteName;
    }

    public void setFavoriteName(String favoriteName) {
        this.favoriteName = favoriteName;
    }

    public long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }
}
