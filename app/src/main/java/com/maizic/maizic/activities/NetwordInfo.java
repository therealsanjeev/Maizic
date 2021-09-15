package com.maizic.maizic.activities;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.macrovideo.sdk.setting.WifiInfo;

public class NetwordInfo implements Parcelable {
    public static final Creator CREATOR = new C02411();
    private Creator<com.macrovideo.sdk.setting.WifiInfo> WifiInfo;
    private boolean bWifiSet = false;
    private int nDeviceVersion = 0;
    private int nMode = 0;
    private int nResult = -1;
    private String strApName = null;
    private String strApPassword = null;
    private String strWifiName = null;
    private String strWifiPassword = null;

    static class C02411 implements Creator {
        C02411() {
        }

        public NetwordInfo createFromParcel(Parcel in) {
            return new NetwordInfo(in);
        }

        public NetwordInfo[] newArray(int size) {
            return new NetwordInfo[size];
        }
    }

    public int getnResult() {
        return this.nResult;
    }

    public void setnResult(int nResult) {
        this.nResult = nResult;
    }

    public int getnMode() {
        return this.nMode;
    }

    public void setnMode(int nMode) {
        this.nMode = nMode;
    }

    public String getStrApName() {
        return this.strApName;
    }

    public void setStrApName(String strApName) {
        this.strApName = strApName;
    }

    public String getStrApPassword() {
        return this.strApPassword;
    }

    public void setStrApPassword(String strApPassword) {
        this.strApPassword = strApPassword;
    }

    public String getStrWifiName() {
        return this.strWifiName;
    }

    public void setStrWifiName(String strWifiName) {
        this.strWifiName = strWifiName;
    }

    public String getStrWifiPassword() {
        return this.strWifiPassword;
    }

    public void setStrWifiPassword(String strWifiPassword) {
        this.strWifiPassword = strWifiPassword;
    }

    public boolean isbWifiSet() {
        return this.bWifiSet;
    }

    public void setbWifiSet(boolean bWifiSet) {
        this.bWifiSet = bWifiSet;
    }

    public int getnDeviceVersion() {
        return this.nDeviceVersion;
    }

    public void setnDeviceVersion(int nDeviceVersion) {
        this.nDeviceVersion = nDeviceVersion;
    }

    public Creator<WifiInfo> getWifiInfo() {
        return this.WifiInfo;
    }

    public void setWifiInfo(Creator<WifiInfo> wifiInfo) {
        this.WifiInfo = wifiInfo;
    }

    public int describeContents() {
        return 0;
    }

    public NetwordInfo(Parcel in) {
        boolean z = true;
        this.nResult = in.readInt();
        this.nMode = in.readInt();
        this.strApName = in.readString();
        this.strApPassword = in.readString();
        this.strWifiName = in.readString();
        this.strWifiPassword = in.readString();
        if (in.readByte() != (byte) 1) {
            z = false;
        }
        this.bWifiSet = z;
        this.nDeviceVersion = in.readInt();
    }

    public void writeToParcel(Parcel parcel, int arg1) {
        parcel.writeInt(this.nResult);
        parcel.writeString(this.strApName);
        parcel.writeString(this.strApPassword);
        parcel.writeString(this.strWifiName);
        parcel.writeString(this.strWifiPassword);
        parcel.writeByte((byte) (this.bWifiSet ? 1 : 0));
        parcel.writeInt(this.nDeviceVersion);
    }
}
