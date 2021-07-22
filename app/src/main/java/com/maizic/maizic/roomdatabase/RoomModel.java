package com.maizic.maizic.roomdatabase;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sanjeev on 08,June,2021
 * therealsanjeev0@gmail.com
 */
@Entity(tableName = "cameraData")
public class RoomModel {
    @PrimaryKey
    @NonNull
    private String deviceID;
    private String deviceName;
    private String devicePass;
    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDevicePass() {
        return devicePass;
    }

    public void setDevicePass(String devicePass) {
        this.devicePass = devicePass;
    }

    public RoomModel(String deviceID, String deviceName, String devicePass) {
        this.deviceID = deviceID;
        this.deviceName = deviceName;
        this.devicePass = devicePass;
    }


    public RoomModel(){

    }


}
