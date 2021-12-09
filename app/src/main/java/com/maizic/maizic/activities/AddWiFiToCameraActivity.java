package com.maizic.maizic.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.macrovideo.sdk.defines.Defines;
import com.macrovideo.sdk.defines.ResultCode;
import com.macrovideo.sdk.media.ILoginDeviceCallback;
import com.macrovideo.sdk.media.LoginHandle;
import com.macrovideo.sdk.media.LoginHelper;
import com.macrovideo.sdk.objects.DeviceInfo;
import com.macrovideo.sdk.objects.LoginParam;
import com.macrovideo.sdk.setting.DeviceNetworkSetting;
import com.macrovideo.sdk.setting.NetworkConfigInfo;
import com.macrovideo.sdk.tools.DeviceScanner;
import com.maizic.maizic.LocalDefines;
import com.maizic.maizic.NVPlayerPlayFishEyeActivity;
import com.maizic.maizic.R;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiScan.ScanResultsListener;

import java.util.ArrayList;
import java.util.List;

public class AddWiFiToCameraActivity extends AppCompatActivity implements ILoginDeviceCallback {
    static final int HANDLE_MSG_CODE_LOGIN_RESULT = 0x10;
    private ListView wifiDeviceList;
//    private WifiManager wifiManager;

    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;

    //    WifiReceiverBroadCast receiverWifi;
    private static final int SEEK_DEVICE_OVERTIME = 0x13;

    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;
    private TextView wifiInfo;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    ArrayList<String> deviceList;
    private ArrayList<DeviceInfo> deviceList1 = null;
    public static DeviceInfo deviceInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi_to_camera);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            init();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
        }
        findViewById(R.id.btnBackToHomeCamera).setOnClickListener(v -> onBackPressed());
        deviceInfo=getIntent().getParcelableExtra("deviceInfo");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                finish();
            }
        }
    }

    private void init() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        deviceList = new ArrayList<>();
//        wifiInfo = findViewById(R.id.wifiInfo);
        wifiDeviceList = findViewById(R.id.lvAPDeviceView);
        Button buttonScan = findViewById(R.id.btnAPDeviceRefresh);
        progressBar = findViewById(R.id.progressbarWifiScan);
        frameLayout = findViewById(R.id.wifilistcheck);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);

        } else {
            frameLayout.setVisibility(View.GONE);
        }
        buttonScan.setOnClickListener(v -> {
            frameLayout.setVisibility(View.VISIBLE);
            if (!wifiManager.isWifiEnabled()) {
                Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
                wifiManager.setWifiEnabled(true);
                deviceList.clear();
                progressBar.setVisibility(View.GONE);

                return;
            }
            frameLayout.setVisibility(View.GONE);

            progressBar.setVisibility(View.VISIBLE);
            wifiList();


        });
//        // listening to single list item on click
        wifiDeviceList.setOnItemClickListener((parent, view, position, id) -> {

            // selected item
            String ssid = ((TextView) view).getText().toString();
            connectToWifi(ssid);
//            Toast.makeText(AddWiFiToCameraActivity.this, "Wifi SSID : " + ssid, Toast.LENGTH_SHORT).show();

        });
    }


    private void finallyConnect(String networkPass, String networkSSID) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

        // remember id
//        Toast.makeText(AddWiFiToCameraActivity.this, networkPass, Toast.LENGTH_SHORT).show();
        addingWifiToCamera(networkSSID, networkPass);
    }

    private void connectToWifi(final String wifiSSID) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.connect_wifi);
        dialog.setTitle("Connect to Network");
        TextView textSSID = dialog.findViewById(R.id.textSSID1);

        Button dialogButton = dialog.findViewById(R.id.okButton);
        EditText pass = dialog.findViewById(R.id.textPassword);
        textSSID.setText(wifiSSID);

        // if button is clicked, connect to the network;
        dialogButton.setOnClickListener(v -> {
            String checkPassword = pass.getText().toString();
            finallyConnect(checkPassword, wifiSSID);
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        receiverWifi = new WifiReceiverBroadCast(wifiManager, wifiDeviceList);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        registerReceiver(receiverWifi, intentFilter);
//        Log.d("TAG", "onPostResume: "+receiverWifi);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);

        } else {
            frameLayout.setVisibility(View.GONE);
        }
        getWifi();
    }

    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(AddWiFiToCameraActivity.this, "version>=marshmallow", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(AddWiFiToCameraActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(AddWiFiToCameraActivity.this, "location turned off", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(AddWiFiToCameraActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                Toast.makeText(AddWiFiToCameraActivity.this, "location turned on", Toast.LENGTH_SHORT).show();
//                wifiManager.startScan();
                wifiList();
            }
        } else {
            Toast.makeText(AddWiFiToCameraActivity.this, "scanning", Toast.LENGTH_SHORT).show();
//            wifiManager.startScan();
            wifiList();
        }
    }

    private void wifiList() {
        WifiUtils.withContext(this).scanWifi(new ScanResultsListener() {
            @Override
            public void onScanResults(@NonNull List<ScanResult> scanResults) {
//                wifiList.setText("");
//                for (ScanResult scanResult : scanResults){
//                    wifiList.append(scanResult.SSID + ":" + scanResult.frequency
//                            +":" + scanResult.capabilities
//                            + "\n\n");
//                }
                StringBuilder sb;
                sb = new StringBuilder();
//                List<ScanResult> wifiList =scanResults;
//                Log.d("TAG", "onReceive: " + wifiList);

                for (ScanResult scanResult : scanResults) {
//                    sb.append("\n").append(scanResult.SSID).append(" - ").append(scanResult.capabilities);
                    deviceList.add(scanResult.SSID);
//                    Log.d("TAG", "onReceive: " + scanResult.SSID);
                }
//                Toast.makeText(this, sb, Toast.LENGTH_SHORT).show();
//                Log.d("TAG", "onReceive: " + deviceList);
                if (deviceList.size() != 0) progressBar.setVisibility(View.GONE);
                ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, deviceList.toArray());
                wifiDeviceList.setAdapter(arrayAdapter);
            }
        }).start();
    }

    private void addingWifiToCamera(String wifiSSID, String wifiPassword) {
//        LoginParam loginParam = new LoginParam(deviceInfo, Defines.LOGIN_FOR_SETTING);
        LoginHandle loginHandle = new LoginHandle();
//        DeviceInfo deviceInfo11=
//        ArrayList<DeviceInfo> list = DeviceScanner.getDeviceListFromLan();
//
//        deviceInfo = new DeviceInfo(-1, Integer.parseInt(deviceID), deviceID,
//                "192.168.1.1", 8800, deviceUser, devicePwd, "ABC", deviceID + ".nvdvr.net",
//                Defines.SERVER_SAVE_TYPE_ADD);
//        if (!list.isEmpty())
//            deviceInfo = list.get(0);

//        LoginParam loginParam = new LoginParam( deviceInfo, Defines.LOGIN_FOR_PLAY);//Real-time preview

        int loginResult=login();
        loginHandle.setnResult(loginResult);
//
        LoginParam loginParam = new LoginParam(deviceInfo, Defines.LOGIN_FOR_SETTING);
        LoginHandle loginHandle = LoginHelper.loginDevice(this,loginParam,);
//        LoginHandle loginHandle = new LoginHandle(121);

//        loginHandle.setVersion(2);
//        LoginHandle loginHandle1=LoginHelper.loginDevice(this,loginParam,null);


        NetworkConfigInfo networkInfo = DeviceNetworkSetting.setNetworkConfig(loginHandle, deviceInfo, 1002, wifiSSID, wifiPassword);


        Log.d("TAG", "addingWifiToCamera: " + deviceInfo);
//        LoginParam loginParam = new LoginParam(deviceInfo, Defines.LOGIN_FOR_SETTING);
//        LoginHandle loginHandle = LoginHelper.loginDevice(loginParam);

//        if (loginHandle != null && loginHandle.getnResult() == ResultCode.RESULT_CODE_SUCCESS)
//        Log.d("TAG", "addingWifiToCamera: "+loginHandle.getStrIP());
//        NetworkConfigInfo networkInfo = DeviceNetworkSetting.setNetworkConfig( null, deviceInfo, 1002,
//                wifiSSID, wifiPassword);

        Log.d("TAG", "addingWifiToCamera: " + networkInfo + "   code:" + networkInfo.getnResult());
        if (networkInfo != null && networkInfo.getnResult() == ResultCode.RESULT_CODE_SUCCESS) {
            Toast.makeText(AddWiFiToCameraActivity.this, "Connected Wifi", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(AddWiFiToCameraActivity.this, "Try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLogin(LoginHandle loginHandle) {

    }

    // new SDK
    private int login() {
        LoginParam loginParam = new LoginParam(deviceInfo, Defines.LOGIN_FOR_PLAY);


        int loginResult = LoginHelper.loginDevice(this, loginParam, loginHandle -> {
            if (loginHandle != null && loginHandle.getnResult() == ResultCode.RESULT_CODE_SUCCESS) {
                // login successful
                Message msg = handler.obtainMessage();
                msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
                msg.arg2 = ResultCode.RESULT_CODE_SUCCESS;
                Bundle data = new Bundle();
                data.putParcelable("device_param", loginHandle);
                msg.setData(data);
                handler.sendMessage(msg);
            } else if (loginHandle != null) {
                // Login failed, you can view the specific error code
                Message msg = handler.obtainMessage();
                msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
                msg.arg2 = loginHandle.getnResult();
                handler.sendMessage(msg);
            } else {
                // Login failed
                Message msg = handler.obtainMessage();
                msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
                msg.arg2 = ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL;
                handler.sendMessage(msg);
            }
        });

        if (loginResult != 0) {
            // Login failure, parameter error, etc.
            Message msg = handler.obtainMessage();
            msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
            msg.arg2 = ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL;
            handler.sendMessage(msg);
        }
        return loginResult;
    }
    private Handler handler = new Handler() {
        // @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {

            if (msg.arg1 == HANDLE_MSG_CODE_LOGIN_RESULT) {
//                progress.setVisibility(View.GONE);
//                btn.setClickable(true);
//                btn2.setClickable(true);

                switch (msg.arg2) {
                    case ResultCode.RESULT_CODE_SUCCESS: {

                        Bundle bundle = msg.getData();

                        LoginHandle loginHandle = bundle.getParcelable("device_param");
                        LocalDefines.Device_LoginHandle = loginHandle;
                        int camType = loginHandle.getCamType();

                        if (camType == LocalDefines.CAMTYPE_WALL
                                || camType == LocalDefines.CAMTYPE_CEIL) {
                            Intent intent = new Intent(AddWiFiToCameraActivity.this, NVPlayerPlayFishEyeActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            //MainActivity.this.finish();
                        } else {

//                            String deviceID = etDeviceID.getText().toString().trim();
//                            String deviceUser = etDeviceUser.getText().toString().trim();
//                            String devicePwd = etDevicePwd.getText().toString().trim();
//                            RoomModel model= new RoomModel(deviceID,deviceUser,devicePwd);
//
//                            saveCameraData(model);
//                            Intent intent = new Intent(CameraSetupActivity.this, NVPlayerPlayActivity.class);
//                            intent.putExtras(bundle);
//                            startActivity(intent);
                            //MainActivity.this.finish();
                        }
                        break;
                    }
                    case ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL: {
                        ShowAlert(
                                getString(R.string.alert_title_login_failed)
                                        + "  ("
                                        + getString(R.string.notice_Result_BadResult)
                                        + ")",
                                getString(R.string.alert_connect_tips));
                    }
                    break;
                    case ResultCode.RESULT_CODE_FAIL_VERIFY_FAILED: {
                        ShowAlert(getString(R.string.alert_title_login_failed),
                                getString(R.string.notice_Result_VerifyFailed));
                    }
                    break;
                    case ResultCode.RESULT_CODE_FAIL_USER_NOEXIST: {
//                        progress.setVisibility(View.GONE);
                        ShowAlert(getString(R.string.alert_title_login_failed),
                                getString(R.string.notice_Result_UserNoExist));
                    }
                    break;
                    case ResultCode.RESULT_CODE_FAIL_PWD_ERROR: {
                        ShowAlert(getString(R.string.alert_title_login_failed),
                                getString(R.string.notice_Result_PWDError));
                    }
                    break;
                    case ResultCode.RESULT_CODE_FAIL_OLD_VERSON: {
                        ShowAlert(getString(R.string.alert_title_login_failed),
                                getString(R.string.notice_Result_Old_Version));
                    }
                    break;
                    default:
                        ShowAlert(
                                getString(R.string.alert_title_login_failed)
                                        + "  ("
                                        + getString(R.string.notice_Result_ConnectServerFailed)
                                        + ")", "");
                        break;

                }
            }
        }
    };


    private void ShowAlert(String title, String msg) {
        try {
            new AlertDialog.Builder(this).setTitle(title)
                    .setMessage(msg)
                    .setIcon(R.drawable.logo)
                    .setPositiveButton(getString(R.string.alert_btn_OK),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {

                                    setResult(RESULT_OK);
                                }
                            }).show();

        } catch (Exception e) {

        }

    }

}
