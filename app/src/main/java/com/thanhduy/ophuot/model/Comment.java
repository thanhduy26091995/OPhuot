package com.thanhduy.ophuot.model;

/**
 * Created by buivu on 22/03/2017.
 */

public class Comment {
    private long commentTime;
    private String commentBy;
    private String content;
    private long rating;

    public Comment() {
    }

    public Comment(long commentTime, String commentBy, String content, long rating) {
        this.commentTime = commentTime;
        this.commentBy = commentBy;
        this.content = content;
        this.rating = rating;
    }

    public long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(long commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentBy() {
        return commentBy;
    }

    public void setCommentBy(String commentBy) {
        this.commentBy = commentBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }
}
