package com.thanhduy.ophuot.model;

import java.io.Serializable;

/**
 * Created by buivu on 22/03/2017.
 */

public class Comments implements Serializable{
    private long commentCount;

    public Comments() {
    }

    public Comments(int commentCount) {
        this.commentCount = commentCount;

    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

}
