package com.thanhduy.ophuot.model;

/**
 * Created by buivu on 05/05/2017.
 */

public class Report {
    private String userReported;
    private String reportBy;
    private String content;
    private String timestamp;

    public Report() {
    }

    public Report(String userReported, String reportBy, String content, String timestamp) {
        this.userReported = userReported;
        this.reportBy = reportBy;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUserReported() {
        return userReported;
    }

    public void setUserReported(String userReported) {
        this.userReported = userReported;
    }

    public String getReportBy() {
        return reportBy;
    }

    public void setReportBy(String reportBy) {
        this.reportBy = reportBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
