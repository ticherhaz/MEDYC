package com.ashrof.medyc.model;

public class NoticesDashboard {

    private String noticesDashboardUid, title, details, onCreatedDate, imageUrl;
    private boolean status;

    public NoticesDashboard() {
    }

    public NoticesDashboard(String noticesDashboardUid, String title, String details, String onCreatedDate, String imageUrl, boolean status) {
        this.noticesDashboardUid = noticesDashboardUid;
        this.title = title;
        this.details = details;
        this.onCreatedDate = onCreatedDate;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public String getNoticesDashboardUid() {
        return noticesDashboardUid;
    }

    public void setNoticesDashboardUid(String noticesDashboardUid) {
        this.noticesDashboardUid = noticesDashboardUid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getOnCreatedDate() {
        return onCreatedDate;
    }

    public void setOnCreatedDate(String onCreatedDate) {
        this.onCreatedDate = onCreatedDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
