package com.binarynumbers.gokozo.fcm.model;

public class FCMModel {

    private String FireBaseID;
    private String FromDevice;
    private String DeviceModel;

    public FCMModel(String fireBaseID, String fromDevice, String deviceModel) {
        FireBaseID = fireBaseID;
        FromDevice = fromDevice;
        DeviceModel = deviceModel;
    }
}
