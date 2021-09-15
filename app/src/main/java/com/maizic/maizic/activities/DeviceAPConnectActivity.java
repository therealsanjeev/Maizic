package com.maizic.maizic.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.macrovideo.sdk.defines.Defines;
import com.macrovideo.sdk.defines.ResultCode;
import com.macrovideo.sdk.media.LoginHandle;
import com.macrovideo.sdk.media.LoginHelperEX;
import com.macrovideo.sdk.tools.DeviceScanner;
import com.macrovideo.sdk.tools.Functions;
import com.maizic.maizic.LocalDefines;
import com.maizic.maizic.NVPlayerPlayActivity;
import com.maizic.maizic.NVPlayerPlayFishEyeActivity;
import com.maizic.maizic.R;
import com.maizic.maizic.SmartLinkQuickWifiConfigActivity;
import com.maizic.maizic.WiFiAdnim;
import com.tencent.android.tpush.common.Constants;

import java.util.ArrayList;
import java.util.List;

public class DeviceAPConnectActivity extends Activity implements OnClickListener, OnItemClickListener {
    static final int HANDLE_MSG_CODE_LOGIN_RESULT = 16;
    private static int HANDLE_MSG_FRESH_LIST = 110;
    private static final int HANDLE_MSG_WIFI_CONNECT_SUCCEED = 4370;
    static final int LOGIN_COMMUNICATION_BUFFER_SIZE = 520;
    static final int LOGIN_RESULT_CODE_FAIL_NET_DOWN = 4097;
    static final int LOGIN_RESULT_CODE_FAIL_NET_POOL = 4098;
    static final int LOGIN_RESULT_CODE_FAIL_OLD_VERSON = 4103;
    static final int LOGIN_RESULT_CODE_FAIL_PWD_ERROR = 4102;
    static final int LOGIN_RESULT_CODE_FAIL_SERVER_OFFLINE = 4099;
    static final int LOGIN_RESULT_CODE_FAIL_USER_NOEXIST = 4101;
    static final int LOGIN_RESULT_CODE_FAIL_VERIFY_FAILED = 4100;
    static final int LOGIN_RESULT_CODE_SUCCESS = 1;
    static final int MR_LOGIN_COMMUNICATION_BUFFER_SIZE = 256;
    private int _nThreadID = 0;
    private boolean bIsPassword = true;
    private ImageView btnAPDeviceBack;
    private Button btnAPDeviceRefresh;
    private ArrayList<com.macrovideo.sdk.objects.DeviceInfo> deviceList = null;
    private Handler handler = new C02701();
    private List<ScanResult> locaWifiDeiviceList = new ArrayList();
    private ListView lvAPDeviceView;
    private ApWifiReceiver mReceiver;
    private WiFiAdnim mWiFiAdnim;
    private WifiManager mWiFiManager;
    private WifiInfo mWifiInfo;
    private int m_loginID = 0;
    private int m_nAddType = 0;
    private int m_nDevID = 0;
    private int m_nID = -1;
    private int m_nSearchID = 0;
    private String m_strIP = "127.0.0.1";
    private String m_strName = "IPC";
    private String m_strPassword = Constants.MAIN_VERSION_TAG;
    private String m_strUsername = Constants.MAIN_VERSION_TAG;
    private int nDeviceEnrcrypt = 0;
    private PopupWindow popupWindow;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogDevice;
    private ProgressDialog progressDialogLogin;
    private String strDeviceSSID = null;

    class C02701 extends Handler {
        C02701() {
        }

        public void handleMessage(Message msg) {
            if (msg.arg1 == DeviceAPConnectActivity.HANDLE_MSG_FRESH_LIST) {
                if (DeviceAPConnectActivity.this.progressDialog != null) {
                    DeviceAPConnectActivity.this.progressDialog.hide();
                    DeviceAPConnectActivity.this.progressDialog = null;
                }
                if (DeviceAPConnectActivity.this.locaWifiDeiviceList == null || DeviceAPConnectActivity.this.locaWifiDeiviceList.size() <= 0) {
                    Toast.makeText(DeviceAPConnectActivity.this, DeviceAPConnectActivity.this.getString(R.string.nearbyNotHotspot), Toast.LENGTH_LONG).show();
                } else {
                    DeviceAPConnectActivity.this.updateDeviceListView();
                }
            } else if (msg.arg1 == DeviceAPConnectActivity.HANDLE_MSG_WIFI_CONNECT_SUCCEED) {
                DeviceAPConnectActivity.this.StartSearchDevice();
                DeviceAPConnectActivity.this.progressDialogDevice.setMessage(new StringBuilder(String.valueOf(DeviceAPConnectActivity.this.getString(R.string.gainHotspotInformation))).append("...").toString());
            } else if (msg.arg1 == 1001) {
                com.macrovideo.sdk.objects.DeviceInfo infoLogin = null;
                if (msg.arg2 == 101 && DeviceAPConnectActivity.this.deviceList != null && DeviceAPConnectActivity.this.deviceList.size() > 0) {
                    boolean bNewDevFound = false;
                    String searchResultMsg = DeviceAPConnectActivity.this.getString(R.string.add_device);
                    for (int i = 0; i < DeviceAPConnectActivity.this.deviceList.size(); i++) {
                        com.macrovideo.sdk.objects.DeviceInfo info = DeviceAPConnectActivity.this.deviceList.get(i);
                        if (info != null) {
//                            if (DatabaseManager.IsInfoExist(info)) {
//                                if (DatabaseManager.UpdateServerInfo(info)) {
//                                    bNewDevFound = true;
//                                }
//                            } else if (DatabaseManager.AddServerInfo(info)) {
//                                bNewDevFound = true;
//                                if (i == 0) {
//                                    searchResultMsg = new StringBuilder(String.valueOf(searchResultMsg)).append(info.getStrName()).toString();
//                                } else {
//                                    searchResultMsg = new StringBuilder(String.valueOf(searchResultMsg)).append(", ").append(info.getStrName()).toString();
//                                }
//                            }
                            String strDeviceID = new StringBuilder(String.valueOf(info.getnDevID())).toString();
                            if (strDeviceID != null && strDeviceID.length() > 0 && strDeviceID.equals(DeviceAPConnectActivity.this.strDeviceSSID)) {
                                bNewDevFound = true;
                                infoLogin = info;
                            }
                        }
                    }
                    if (DeviceAPConnectActivity.this.progressDialogDevice != null) {
                        DeviceAPConnectActivity.this.progressDialogDevice.hide();
                        DeviceAPConnectActivity.this.progressDialogDevice = null;
                    }
                    if (bNewDevFound) {
//                        LocalDefines.reloadDeviceInfoList();
                        LocalDefines.isDeviceListSet = false;
                        LocalDefines.nClientDeviceSettingThreadID++;
//                        new RegistClientWithDeviceArrayToServer((Handler) this, LocalDefines.nClientDeviceSettingThreadID).start();
                        DeviceAPConnectActivity.this.ShowApplicationExitAlert(infoLogin);
                        return;
                    }
                    DeviceAPConnectActivity.this.StartSearchDevice();
                }
            } else if (msg.arg1 == 16) {
                DeviceAPConnectActivity.this.lvAPDeviceView.setEnabled(true);
                switch (msg.arg2) {
                    case 1:
                        Bundle data = msg.getData();
                        if (data == null) {
                            DeviceAPConnectActivity.this.ShowAlert(new StringBuilder(String.valueOf(DeviceAPConnectActivity.this.getString(R.string.alert_title_login_failed))).append("(").append(DeviceAPConnectActivity.this.getString(R.string.notice_Result_BadResult)).append(")").toString(), DeviceAPConnectActivity.this.getString(R.string.alert_connect_tips));
                            return;
                        }
                        data.putInt("id", DeviceAPConnectActivity.this.m_nID);
                        data.putInt("device_id", DeviceAPConnectActivity.this.m_nDevID);
                        data.putInt("add_type", DeviceAPConnectActivity.this.m_nAddType);
                        data.putString("name", DeviceAPConnectActivity.this.m_strName);
                        data.putString("server", DeviceAPConnectActivity.this.m_strIP);
                        data.putString("username", DeviceAPConnectActivity.this.m_strUsername);
                        data.putString("password", DeviceAPConnectActivity.this.m_strPassword);
                        if (DeviceAPConnectActivity.this.progressDialogLogin != null) {
                            DeviceAPConnectActivity.this.progressDialogLogin.hide();
                            DeviceAPConnectActivity.this.progressDialogLogin = null;
                        }
                        int camType = ((LoginHandle) data.getParcelable(Defines.RECORD_FILE_RETURN_MESSAGE)).getCamType();
                        Intent intent;
                        if (camType == 1 || camType == 2) {
                            data.putInt("camType", camType);
                            intent = new Intent(DeviceAPConnectActivity.this, NVPlayerPlayFishEyeActivity.class);
                            intent.putExtras(data);
                            DeviceAPConnectActivity.this.startActivity(intent);
                            DeviceAPConnectActivity.this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                            DeviceAPConnectActivity.this.finish();
                            return;
                        }
                        intent = new Intent(DeviceAPConnectActivity.this, NVPlayerPlayActivity.class);
                        intent.putExtras(data);
                        DeviceAPConnectActivity.this.startActivity(intent);
                        DeviceAPConnectActivity.this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                        DeviceAPConnectActivity.this.finish();
                        return;
                    case 4097:
                        DeviceAPConnectActivity.this.ShowAlert(new StringBuilder(String.valueOf(DeviceAPConnectActivity.this.getString(R.string.alert_title_login_failed))).append("  (").append(DeviceAPConnectActivity.this.getString(R.string.notice_Result_BadResult)).append(")").toString(), DeviceAPConnectActivity.this.getString(R.string.alert_connect_tips));
                        return;
                    case 4098:
                        DeviceAPConnectActivity.this.ShowAlert(new StringBuilder(String.valueOf(DeviceAPConnectActivity.this.getString(R.string.alert_title_login_failed))).append("  (").append(DeviceAPConnectActivity.this.getString(R.string.notice_Result_ConnectServerFailed)).append(")").toString(), DeviceAPConnectActivity.this.getString(R.string.alert_connect_tips));
                        return;
                    case 4099:
                        DeviceAPConnectActivity.this.ShowAlert(new StringBuilder(String.valueOf(DeviceAPConnectActivity.this.getString(R.string.alert_title_login_failed))).append("  (").append(DeviceAPConnectActivity.this.getString(R.string.notice_Result_ConnectServerFailed)).append(")").toString(), DeviceAPConnectActivity.this.getString(R.string.alert_connect_tips));
                        return;
                    case 4100:
                        DeviceAPConnectActivity.this.ShowAlert(DeviceAPConnectActivity.this.getString(R.string.alert_title_login_failed), DeviceAPConnectActivity.this.getString(R.string.notice_Result_VerifyFailed));
                        return;
                    case 4101:
                        DeviceAPConnectActivity.this.ShowAlert(DeviceAPConnectActivity.this.getString(R.string.alert_title_login_failed), DeviceAPConnectActivity.this.getString(R.string.notice_Result_UserNoExist));
                        return;
                    case 4102:
                        DeviceAPConnectActivity.this.ShowAlert(DeviceAPConnectActivity.this.getString(R.string.alert_title_login_failed), DeviceAPConnectActivity.this.getString(R.string.notice_Result_PWDError));
                        return;
                    case 4103:
                        DeviceAPConnectActivity.this.ShowAlert(DeviceAPConnectActivity.this.getString(R.string.alert_title_login_failed), DeviceAPConnectActivity.this.getString(R.string.notice_Result_Old_Version));
                        return;
                    default:
                        DeviceAPConnectActivity.this.ShowAlert(new StringBuilder(String.valueOf(DeviceAPConnectActivity.this.getString(R.string.alert_title_login_failed))).append("  (").append(DeviceAPConnectActivity.this.getString(R.string.notice_Result_ConnectServerFailed)).append(")").toString(), DeviceAPConnectActivity.this.getString(R.string.alert_connect_tips));
                        return;
                }
            }
        }
    }

    class C02712 implements DialogInterface.OnClickListener {
        C02712() {
        }

        public void onClick(DialogInterface dialog, int which) {
            DeviceAPConnectActivity.this.mWiFiAdnim.openWifi();
            DeviceAPConnectActivity.this.mReceiver = new ApWifiReceiver();
            DeviceAPConnectActivity.this.registerReceiver(DeviceAPConnectActivity.this.mReceiver, new IntentFilter("android.net.wifi.SCAN_RESULTS"));
            DeviceAPConnectActivity.this.mWiFiAdnim.startScan();
            DeviceAPConnectActivity.this.progressDialog.show();
        }
    }

    class C02723 implements DialogInterface.OnClickListener {
        C02723() {
        }

        public void onClick(DialogInterface dialog, int which) {
        }
    }

    class C02734 implements DialogInterface.OnClickListener {
        C02734() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            DeviceAPConnectActivity.this.setResult(-1);
//            DeviceAPConnectActivity.this.startActivity(new Intent(DeviceAPConnectActivity.this, HomePageActivity.class));
            DeviceAPConnectActivity.this.finish();
        }
    }

    class C02745 implements DialogInterface.OnClickListener {
        C02745() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
//            DeviceAPConnectActivity.this.startActivity(new Intent(DeviceAPConnectActivity.this, HomePageActivity.class));
            DeviceAPConnectActivity.this.finish();
        }
    }

    class C02789 implements OnClickListener {
        C02789() {
        }

        public void onClick(View arg0) {
            if (DeviceAPConnectActivity.this.popupWindow != null) {
                DeviceAPConnectActivity.this.popupWindow.dismiss();
            }
        }
    }

    private class ApWifiReceiver extends BroadcastReceiver {
        private ApWifiReceiver() {
        }

        public void onReceive(Context c, Intent intent) {
            List<ScanResult> list = DeviceAPConnectActivity.this.mWiFiManager.getScanResults();
            DeviceAPConnectActivity.this.locaWifiDeiviceList.clear();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    boolean bResult = false;
                    ScanResult scanResult = (ScanResult) list.get(i);
                    String strSSID = scanResult.SSID;
                    if (strSSID.startsWith("MV") && strSSID.length() >= 10 && strSSID.length() < 32) {
                        try {
                            if (Integer.parseInt(strSSID.substring(2)) > 0) {
                                bResult = true;
                            }
                        } catch (Exception e) {
                        }
                    }
                    if (bResult) {
                        DeviceAPConnectActivity.this.locaWifiDeiviceList.add(scanResult);
                    }
                }
            }
            Message msg = DeviceAPConnectActivity.this.handler.obtainMessage();
            msg.arg1 = DeviceAPConnectActivity.HANDLE_MSG_FRESH_LIST;
            DeviceAPConnectActivity.this.handler.sendMessage(msg);
        }
    }

    public class BroadCastUdp extends Thread {
        private int nSearchID = 0;

        public BroadCastUdp(int m_nSearchID) {
            this.nSearchID = m_nSearchID;
        }

        public void run() {
            for (int i = 0; i < 3; i++) {
                DeviceAPConnectActivity.this.deviceList =  DeviceScanner.getDeviceListFromLan();
                Message msg;
                if (DeviceAPConnectActivity.this.deviceList != null && DeviceAPConnectActivity.this.deviceList.size() > 0 && DeviceAPConnectActivity.this.m_nSearchID == this.nSearchID) {
                    msg = DeviceAPConnectActivity.this.handler.obtainMessage();
                    msg.arg1 = 1001;
                    msg.arg2 = 101;
                    DeviceAPConnectActivity.this.handler.sendMessage(msg);
                } else if (i >= 3 && DeviceAPConnectActivity.this.m_nSearchID == this.nSearchID) {
                    msg = DeviceAPConnectActivity.this.handler.obtainMessage();
                    msg.arg1 = 1001;
                    msg.arg2 = 102;
                    DeviceAPConnectActivity.this.handler.sendMessage(msg);
                }
            }
        }
    }

    private class DeviceListViewAdapter extends BaseAdapter {
        private ItemViewHolder holder;
        private String[] keyString;
        private Context mContext;
        @SuppressLint("WrongConstant")
        private LayoutInflater mInflater = ((LayoutInflater) this.mContext.getSystemService("layout_inflater"));
        private int[] valueViewID;

        private class ItemViewHolder {
            ImageView ivQuickConfigLogo;
            TextView tvName;

            private ItemViewHolder() {
            }
        }

        public DeviceListViewAdapter(Context c, int resource, String[] from, int[] to) {
            this.mContext = c;
            this.keyString = new String[from.length];
            this.valueViewID = new int[to.length];
            System.arraycopy(from, 0, this.keyString, 0, from.length);
            System.arraycopy(to, 0, this.valueViewID, 0, to.length);
        }

        public int getCount() {
            return DeviceAPConnectActivity.this.locaWifiDeiviceList.size();
        }

        public Object getItem(int position) {
            return DeviceAPConnectActivity.this.locaWifiDeiviceList.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView != null) {
                this.holder = (ItemViewHolder) convertView.getTag();
            } else {
                convertView = this.mInflater.inflate(R.layout.quick_config_item, null);
                this.holder = new ItemViewHolder();
                this.holder.tvName = (TextView) convertView.findViewById(this.valueViewID[0]);
                this.holder.ivQuickConfigLogo = (ImageView) convertView.findViewById(this.valueViewID[1]);
                convertView.setTag(this.holder);
            }
            ScanResult scanResult = (ScanResult) DeviceAPConnectActivity.this.locaWifiDeiviceList.get(position);
            if (scanResult != null) {
                this.holder.tvName.setText(scanResult.SSID);
                this.holder.ivQuickConfigLogo.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    class LoginThread extends Thread {
        private Handler handler;
        private com.macrovideo.sdk.objects.DeviceInfo info = null;
        private int m_ThreadLoginID = 0;

        public LoginThread(Handler handler, com.macrovideo.sdk.objects.DeviceInfo info, int nLoginID) {
            this.handler = handler;
            this.info = info;
            this.m_ThreadLoginID = nLoginID;
        }

        public void run() {
            if (this.m_ThreadLoginID == DeviceAPConnectActivity.this.m_loginID) {
                LoginHandle deviceParam;
                if (this.info.getnSaveType() == Defines.SERVER_SAVE_TYPE_DEMO) {
                    deviceParam = LoginHelperEX.getDeviceParamEX(this.info, this.info.getStrMRServer(), ((int) this.info.getnMRPort()),0);
                } else {
                    deviceParam = LoginHelperEX.getDeviceParamEX(this.info,0);
                }
                Message msg;
                if (deviceParam != null && deviceParam.getnResult() == 256) {
                    msg = this.handler.obtainMessage();
                    msg.arg1 = 16;
                    msg.arg2 = 256;
                    Bundle data = new Bundle();
                    data.putParcelable(Defines.RECORD_FILE_RETURN_MESSAGE, deviceParam);
                    msg.setData(data);
                    System.out.println("login result : " + deviceParam.isMRMode());
                    this.handler.sendMessage(msg);
                } else if (deviceParam != null) {
                    msg = this.handler.obtainMessage();
                    msg.arg1 = 16;
                    msg.arg2 = deviceParam.getnResult();
                    this.handler.sendMessage(msg);
                } else {
                    msg = this.handler.obtainMessage();
                    msg.arg1 = 16;
                    msg.arg2 = ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL;
                    this.handler.sendMessage(msg);
                }
            }
        }
    }

    class WifiConnectMonitorThread extends Thread {
        private int nThreadID = 0;

        public WifiConnectMonitorThread(int nThreadID) {
            this.nThreadID = nThreadID;
        }

        public void run() {
            while (DeviceAPConnectActivity.this._nThreadID == this.nThreadID) {
                DeviceAPConnectActivity.this.mWifiInfo = DeviceAPConnectActivity.this.mWiFiManager.getConnectionInfo();
                if (DeviceAPConnectActivity.this.mWifiInfo != null) {
                    String strConnSSID = DeviceAPConnectActivity.this.mWifiInfo.getSSID();
                    if (strConnSSID == null || strConnSSID.length() <= 2) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        strConnSSID = strConnSSID.substring(1, strConnSSID.length() - 1);
                        if (strConnSSID == null || strConnSSID.length() <= 0) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                        } else if (strConnSSID.equals(DeviceAPConnectActivity.this.strDeviceSSID) && DeviceAPConnectActivity.this.isWiFiActive()) {
                            Message msg = DeviceAPConnectActivity.this.handler.obtainMessage();
                            msg.arg1 = DeviceAPConnectActivity.HANDLE_MSG_WIFI_CONNECT_SUCCEED;
                            DeviceAPConnectActivity.this.handler.sendMessage(msg);
                            return;
                        } else {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e22) {
                                e22.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;

//    WifiReceiver receiverWifi;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_ap_connect);
        initView();
//        intiWifi();


    }



    private void initView() {
        this.btnAPDeviceBack = (ImageView) findViewById(R.id.btnAPDeviceBack);
        this.btnAPDeviceBack.setOnClickListener(this);
        this.lvAPDeviceView = (ListView) findViewById(R.id.lvAPDeviceView);
        this.lvAPDeviceView.setOnItemClickListener(this);
        this.btnAPDeviceRefresh = (Button) findViewById(R.id.btnAPDeviceRefresh);
        this.btnAPDeviceRefresh.setOnClickListener(this);
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setProgressStyle(0);
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.setMessage(getString(R.string.seekHotspot) + "...");
        this.progressDialogLogin = new ProgressDialog(this);
        this.progressDialogLogin.setProgressStyle(0);
        this.progressDialogLogin.setCanceledOnTouchOutside(false);
        this.progressDialogLogin.setMessage(getString(R.string.bufferPreView) + "...");
    }


    private void intiWifi() {
        this.mWiFiAdnim = new WiFiAdnim(this);
        this.mWiFiManager = (WifiManager) getSystemService(String.valueOf("wifi"));
        if (this.mWiFiManager.getWifiState() == 3) {
            this.mReceiver = new ApWifiReceiver();
            registerReceiver(this.mReceiver, new IntentFilter("android.net.wifi.SCAN_RESULTS"));
            this.mWiFiAdnim.startScan();
            this.progressDialog.show();
            return;
        }
        View view = View.inflate(this, R.layout.show_alert_dialog, null);
        ((TextView) view.findViewById(R.id.tv_title)).setText(getString(R.string.wifiConnect));
        ((TextView) view.findViewById(R.id.tv_content)).setText(getString(R.string.wifi_start_bt));
        new Builder(this).setView(view).setPositiveButton(getString(R.string.wifi_is), new C02712()).setNegativeButton(getString(R.string.wifi_no), new C02723()).show();
    }

    public void ShowAlert(String title, String msg) {
        View view = View.inflate(this, R.layout.show_alert_dialog, null);
        ((TextView) view.findViewById(R.id.tv_title)).setText(title);
        ((TextView) view.findViewById(R.id.tv_content)).setText(msg);
        new Builder(this).setView(view).setPositiveButton(getString(R.string.alert_btn_OK), new C02734()).show();
    }

    public void ShowApplicationExitAlert(final com.macrovideo.sdk.objects.DeviceInfo info) {
        View view = View.inflate(this, R.layout.show_alert_dialog, null);
        ((TextView) view.findViewById(R.id.tv_title)).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.tv_content)).setText(getString(R.string.gainHotspotInformationSucceed));
        new Builder(this).setView(view).setPositiveButton(getString(R.string.addList), new C02745()).setNegativeButton(getString(R.string.realTimePreView), (arg0, arg1) -> {
            if (info != null) {
                DeviceAPConnectActivity.this.progressDialogLogin.show();
                DeviceAPConnectActivity.this.StartLogin(info);
            }
        }).show();
    }

    private void updateDeviceListView() {
        if (this.lvAPDeviceView == null) {
            this.lvAPDeviceView = (ListView) findViewById(R.id.lvAPDeviceView);
        }
        if (this.locaWifiDeiviceList == null || this.locaWifiDeiviceList.size() <= 0) {
            this.lvAPDeviceView.setAdapter(null);
            return;
        }
        DeviceListViewAdapter deviceListItemAdapter = new DeviceListViewAdapter(this, R.layout.quick_config_item, new String[]{"ItemTvDeviceSelect", "ItemTvDeviceLogo"}, new int[]{R.id.tvQuickConfig, R.id.ivQuickConfigLogo});
        if (this.lvAPDeviceView != null) {
            this.lvAPDeviceView.setCacheColorHint(0);
            this.lvAPDeviceView.setAdapter(deviceListItemAdapter);
            this.lvAPDeviceView.setOnItemClickListener(this);
        }
    }

    @SuppressLint("WrongConstant")
    public void onItemClick(AdapterView<?> adapterView, View arg1, int index, long arg3) {
        this.mWiFiManager = (WifiManager) getSystemService("wifi");
        if (this.mWiFiManager.getWifiState() == 3) {
            this.lvAPDeviceView.setEnabled(false);
            if (this.locaWifiDeiviceList != null && this.locaWifiDeiviceList.size() > 0 && index >= 0 && index < this.locaWifiDeiviceList.size()) {
                ScanResult scanResult = (ScanResult) this.locaWifiDeiviceList.get(index);
                if (scanResult != null) {
                    String strDeviceSSID = scanResult.SSID;
                    this.strDeviceSSID = strDeviceSSID;
                    this.nDeviceEnrcrypt = wifiEncrye(scanResult.capabilities);
                    if (this.nDeviceEnrcrypt == 1) {
                        connectToAPDevice(strDeviceSSID, Constants.MAIN_VERSION_TAG, this.nDeviceEnrcrypt);
                        StartMonitor();
                        this.progressDialogDevice = new ProgressDialog(this);
                        this.progressDialogDevice.setProgressStyle(0);
                        this.progressDialogDevice.setCanceledOnTouchOutside(false);
                        this.progressDialogDevice.setMessage(getString(R.string.connect_device));
                        this.progressDialogDevice.show();
                        this.lvAPDeviceView.setEnabled(true);
                        return;
                    }
//                    connectExistPassword();
                    return;
                }
                return;
            }
            return;
        }
        Toast.makeText(this, getString(R.string.deviceWifiOpenFail), Toast.LENGTH_LONG).show();
    }

    private void connectToAPDevice(String SSID, String Password, int Type) {
        this.mWiFiAdnim.addNetWork(this.mWiFiAdnim.CreateWifiInfo(SSID, Password, Type));
    }

    public boolean isWiFiActive() {
        @SuppressLint("WrongConstant") ConnectivityManager connectivity = (ConnectivityManager) getSystemService("connectivity");
        if (connectivity == null) {
            return false;
        }
        NetworkInfo[] infos = connectivity.getAllNetworkInfo();
        if (infos == null) {
            return false;
        }
        for (NetworkInfo ni : infos) {
            if (ni.getTypeName().equals("WIFI") && ni.isConnected()) {
                return true;
            }
        }
        return false;
    }

    private void StartMonitor() {
        this._nThreadID++;
        new WifiConnectMonitorThread(this._nThreadID).start();
    }

//    public void connectExistPassword() {
////        View view = ((LayoutInflater) getSystemService(String.valueOf("layout_inflater"))).inflate(R.layout.activity_ap_connect_popupwindow, null);
//        final EditText etAPConnect = (EditText) view.findViewById(R.id.etAPConnect);
//        final ImageView ivAPConnect = (ImageView) view.findViewById(R.id.ivAPConnect);
//        Button btnAPConnect = (Button) view.findViewById(R.id.btnAPConnect);
//        Button btnAPBack = (Button) view.findViewById(R.id.btnAPBack);
//        ((TextView) view.findViewById(R.id.tvAPConnect)).setText(this.strDeviceSSID);
//        ivAPConnect.setOnClickListener(new OnClickListener() {
//            public void onClick(View arg0) {
//                if (DeviceAPConnectActivity.this.bIsPassword) {
//                    etAPConnect.setInputType(144);
//                    Editable etable = etAPConnect.getText();
//                    Selection.setSelection(etable, etable.length());
//                    ivAPConnect.setImageResource(R.drawable.delete_choose_2);
//                    DeviceAPConnectActivity.this.bIsPassword = false;
//                    return;
//                }
//                DeviceAPConnectActivity.this.bIsPassword = true;
//                etAPConnect.setInputType(Defines.NV_IP_PTZX_REQUEST);
////                etable = etAPConnect.getText();
////                Selection.setSelection(etable, etable.length());
//                ivAPConnect.setImageResource(R.drawable.delete_choose_1);
//            }
//        });
//        btnAPConnect.setOnClickListener(new OnClickListener() {
//            public void onClick(View arg0) {
//                String strPassword = etAPConnect.getText().toString();
//                if (strPassword != null && strPassword.length() > 0) {
//                    DeviceAPConnectActivity.this.connectToAPDevice(DeviceAPConnectActivity.this.strDeviceSSID, strPassword, 3);
//                    DeviceAPConnectActivity.this.StartMonitor();
//                    DeviceAPConnectActivity.this.progressDialogDevice = new ProgressDialog(DeviceAPConnectActivity.this);
//                    DeviceAPConnectActivity.this.progressDialogDevice.setProgressStyle(0);
//                    DeviceAPConnectActivity.this.progressDialogDevice.setCanceledOnTouchOutside(false);
//                    DeviceAPConnectActivity.this.progressDialogDevice.setMessage(new StringBuilder(String.valueOf(DeviceAPConnectActivity.this.getString(R.string.connectDeviceHotspot))).append("...").toString());
//                    DeviceAPConnectActivity.this.progressDialogDevice.show();
//                    if (DeviceAPConnectActivity.this.popupWindow != null) {
//                        DeviceAPConnectActivity.this.popupWindow.dismiss();
//                    }
//                }
//            }
//        });
//        btnAPBack.setOnClickListener(new C02789());
//        float scale = getResources().getDisplayMetrics().density;
//        this.popupWindow = new PopupWindow(view, (int) ((((float) 300) * scale) + 0.5f), (int) ((((float) 300) * scale) + 0.5f));
//        this.popupWindow.setFocusable(true);
//        this.popupWindow.setOutsideTouchable(true);
//        this.popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        this.popupWindow.showAtLocation(view, 16, 0, 0);
//    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btnAPDeviceBack:
                startActivity(new Intent(this, SmartLinkQuickWifiConfigActivity.class));
                finish();
                return;
            case R.id.btnAPDeviceRefresh:
                this.mWiFiAdnim.startScan();
                return;
            default:
                return;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            startActivity(new Intent(this, SmartLinkQuickWifiConfigActivity.class));
            finish();
        }
        return false;
    }

    private int wifiEncrye(String capabilities) {
        if (capabilities.indexOf("WPA2") != -1) {
            return 3;
        }
        if (capabilities.indexOf("[WPA-PSK-TKIP+CCMP]") != -1 || capabilities.indexOf("[WPA2-PSK-TKIP+CCMP]") != -1) {
            return 3;
        }
        if (capabilities.indexOf("[WEP]") != -1 && capabilities.indexOf("[IBSS]") != -1) {
            return 2;
        }
        if (capabilities.indexOf("[WEP]") != -1) {
            return 2;
        }
        if (capabilities.indexOf("[WPA-PSK-CCMP]") != -1 || capabilities.indexOf("[WPA-PSK-TKIP+CCMP]") != -1) {
            return 3;
        }
        if (capabilities.indexOf("[WPA2-PSK-CCMP]") != -1 || capabilities.indexOf("[WPA2-PSK-TKIP+CCMP]") != -1) {
            return 3;
        }
        if (capabilities.indexOf("[ESS]") != -1) {
            return 1;
        }
        return 1;
    }

    public void StartLogin(com.macrovideo.sdk.objects.DeviceInfo info) {
        if (info != null) {
            try {
                if (!Functions.isNetworkAvailable(getApplicationContext())) {
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.toast_network_unreachable), Toast.LENGTH_LONG);
                    toast.setGravity(17, 0, 0);
                    toast.show();
                    return;
                }
            } catch (Exception e) {
            }
            this.m_loginID++;
            if (info.getnSaveType() == LocalDefines.SERVER_SAVE_TYPE_ADD) {
                if (!Functions.isIpAddress(info.getStrIP())) {
                    if (!Functions.hasDot(info.getStrIP())) {
                        info.setStrIP(info.getStrIP() + Defines.MV_DOMAIN_SUFFIX);
                    }
                }
            }
            this.m_strIP = info.getStrIP();
            this.m_nID = info.getnID();
            this.m_nDevID = info.getnDevID();
            if (info.getStrUsername() == null || info.getStrUsername().length() <= 0) {
                this.m_strUsername = "admin";
                this.m_strPassword = Constants.MAIN_VERSION_TAG;
            } else {
                this.m_strUsername = info.getStrUsername();
                this.m_strPassword = info.getStrPassword();
            }
            this.m_strName = info.getStrName();
            this.m_nAddType = info.getnSaveType();
            new LoginThread(this.handler, info, this.m_loginID).start();
        }
    }

    public boolean StartSearchDevice() {
        try {
            if (Functions.isNetworkAvailable(this)) {
                this.m_nSearchID++;
                new BroadCastUdp(this.m_nSearchID).start();
                return true;
            }
            Toast toast = Toast.makeText(this, getString(R.string.toast_network_unreachable), Toast.LENGTH_LONG);
            toast.setGravity(17, 0, 0);
            toast.show();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void StopSearchDevice() {
        this.m_nSearchID++;
        DeviceScanner.reset();
    }
}
