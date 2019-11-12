package com.binarynumbers.gokozo.fcm.model;

public class NotificationModel {

    private NotiSubModel data;
    private String attachment_url;

    //attachment-url

    public NotiSubModel getNotiSubModel() {
        return data;
    }

    public void setNotiSubModel(NotiSubModel data) {
        this.data = data;
    }
}
