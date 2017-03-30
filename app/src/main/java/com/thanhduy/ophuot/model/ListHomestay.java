package com.thanhduy.ophuot.model;

import java.util.List;

/**
 * Created by buivu on 27/03/2017.
 */

public class ListHomestay {
    private List<PostInfo> listHomestay;

    public ListHomestay() {
    }

    public ListHomestay(List<PostInfo> listHomestay) {
        this.listHomestay = listHomestay;
    }

    public List<PostInfo> getListHomestay() {
        return listHomestay;
    }

    public void setListHomestay(List<PostInfo> listHomestay) {
        this.listHomestay = listHomestay;
    }
}
