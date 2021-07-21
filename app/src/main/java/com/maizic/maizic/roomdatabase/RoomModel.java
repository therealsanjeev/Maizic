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
    private int deviceID;
    private int deviceName;
    private int devicePass;
    public RoomModel(){

    }
    public RoomModel(int deviceID, int deviceName, int devicePass) {
        this.deviceID = deviceID;
        this.deviceName = deviceName;
        this.devicePass = devicePass;
    }


    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    public int getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(int deviceName) {
        this.deviceName = deviceName;
    }

    public int getDevicePass() {
        return devicePass;
    }

    public void setDevicePass(int devicePass) {
        this.devicePass = devicePass;
    }



}
