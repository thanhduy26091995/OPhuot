package com.thanhduy.ophuot.model;

import com.thanhduy.ophuot.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 06/04/2017.
 */

public class Message {
    private String content;
    private boolean isImage;
    private boolean isMine;
    private long timestamp;
    private String sendBy;
    private String receiveBy;

    public Message() {
    }

    public Message(String content, boolean isImage, long timestamp, String sendBy) {
        this.content = content;
        this.isImage = isImage;
        this.timestamp = timestamp;
        this.sendBy = sendBy;
    }

    public String getReceiveBy() {
        return receiveBy;
    }

    public void setReceiveBy(String receiveBy) {
        this.receiveBy = receiveBy;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getIsImage() {
        return isImage;
    }

    public void setIsImage(boolean image) {
        isImage = image;
    }

    public boolean getIsMine() {
        return isMine;
    }

    public void setIsMine(boolean mine) {
        isMine = mine;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> mapDataMessage = new HashMap<String, Object>();
        mapDataMessage.put(Constants.CONTENT, content);
        mapDataMessage.put(Constants.IS_IMAGE, isImage);
        mapDataMessage.put(Constants.IS_MINE, isMine);
        mapDataMessage.put(Constants.SEND_BY, sendBy);
        mapDataMessage.put(Constants.TIMESTAMP, timestamp);
        mapDataMessage.put(Constants.RECEIVE_BY, receiveBy);
        return mapDataMessage;
    }
}
