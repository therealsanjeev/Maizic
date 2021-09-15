package com.maizic.maizic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class WiFiAdnim {

    private List<WifiConfiguration> mWifiConfigurations;
    private WifiInfo mWifiInfo = this.mWifiManager.getConnectionInfo();
    private List<ScanResult> mWifiList;
    WifiLock mWifiLock;
    private WifiManager mWifiManager;

    private Context context;

    public WiFiAdnim(Context context) {
        this.context = context;
        this.mWifiManager = (WifiManager) context.getSystemService(String.valueOf("wifi"));
    }

    public void openWifi() {
        if (!this.mWifiManager.isWifiEnabled()) {
            this.mWifiManager.setWifiEnabled(true);
        }
    }

    public void closeWifi() {
        if (this.mWifiManager.isWifiEnabled()) {
            this.mWifiManager.setWifiEnabled(false);
        }
    }

    public int checkState() {
        return this.mWifiManager.getWifiState();
    }

    public void acquireWifiLock() {
        this.mWifiLock.acquire();
    }

    public void releaseWifiLock() {
        if (this.mWifiLock.isHeld()) {
            this.mWifiLock.acquire();
        }
    }

    public void createWifiLock() {
        this.mWifiLock = this.mWifiManager.createWifiLock("test");
    }

    public List<WifiConfiguration> getConfiguration() {
        return this.mWifiConfigurations;
    }

    public void connetionConfiguration(int index) {
        if (index <= this.mWifiConfigurations.size()) {
            this.mWifiManager.enableNetwork(((WifiConfiguration) this.mWifiConfigurations.get(index)).networkId, true);
        }
    }

    public void startScan() {
        this.mWifiManager.startScan();
        this.mWifiList = this.mWifiManager.getScanResults();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.mWifiConfigurations = this.mWifiManager.getConfiguredNetworks();
    }

    public List<ScanResult> getWifiList() {
        return this.mWifiList;
    }

    public StringBuilder lookUpScan() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.mWifiList.size(); i++) {
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            sb.append(((ScanResult) this.mWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }

    public String getMacAddress() {
        return this.mWifiInfo == null ? "NULL" : this.mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return this.mWifiInfo == null ? "NULL" : this.mWifiInfo.getBSSID();
    }

    public int getIpAddress() {
        return this.mWifiInfo == null ? 0 : this.mWifiInfo.getIpAddress();
    }

    public int getNetWordId() {
        return this.mWifiInfo == null ? 0 : this.mWifiInfo.getNetworkId();
    }

    public String getWifiInfo() {
        return this.mWifiInfo == null ? "NULL" : this.mWifiInfo.toString();
    }

    public void addNetWork(WifiConfiguration configuration) {
        this.mWifiManager.enableNetwork(this.mWifiManager.addNetwork(configuration), true);
    }

    public void disConnectionWifi(int netId) {
        this.mWifiManager.disableNetwork(netId);
        this.mWifiManager.disconnect();
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        WifiConfiguration tempConfig = IsExsits(SSID);
        if (tempConfig != null) {
            this.mWifiManager.removeNetwork(tempConfig.networkId);
            this.mWifiManager.saveConfiguration();
        }
        if (Type == 1) {
            config.priority = 200000;
            config.wepKeys[0] = "\"\"";
            config.allowedKeyManagement.set(0);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(1);
            config.allowedGroupCiphers.set(3);
            config.allowedGroupCiphers.set(2);
            config.allowedGroupCiphers.set(0);
            config.allowedGroupCiphers.set(1);
            config.allowedKeyManagement.set(3);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) {
            config.priority = 200000;
            config.preSharedKey = "\"" + Password + "\"";
            config.status = 2;
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(0);
            config.allowedGroupCiphers.set(3);
            config.allowedGroupCiphers.set(2);
            config.allowedPairwiseCiphers.set(1);
            config.allowedPairwiseCiphers.set(2);
            config.allowedProtocols.set(1);
        }
        return config;
    }


    private WifiConfiguration IsExsits(String SSID) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        for (WifiConfiguration existingConfig : this.mWifiManager.getConfiguredNetworks()) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }
}
