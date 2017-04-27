package com.thanhduy.ophuot.model;

import com.thanhduy.ophuot.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 23/04/2017.
 */

public class LastMessage {
    private String partnerId;
    private long timestamp;

    public LastMessage() {
    }

    public LastMessage(String partnerId, long timestamp) {
        this.partnerId = partnerId;
        this.timestamp = timestamp;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(Constants.PARTNER_ID, partnerId);
        dataMap.put(Constants.TIMESTAMP, timestamp);
        return dataMap;
    }
}
