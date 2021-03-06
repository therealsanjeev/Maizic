package com.maizic.maizic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.macrovideo.sdk.media.LoginHandle;
import com.macrovideo.sdk.objects.DeviceInfo;
import com.macrovideo.sdk.setting.DeviceNetworkSetting;
import com.macrovideo.sdk.tools.DeviceScanner;
import com.macrovideo.sdk.tools.Functions;
import com.maizic.maizic.activities.CameraSetupActivity;
import com.maizic.maizic.activities.DeviceAPConnectActivity;
import com.maizic.maizic.animate.RadarView;

import org.json.JSONException;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * For sonic configuration
 *
 * @author Administrator
 */

public class SmartLinkQuickWifiConfigActivity extends Activity implements
        OnClickListener, OnItemClickListener {

    // //WIFI
    private WiFiAdnim mWiFiAdnim;
    private WifiManager mWiFiManager; // Define a WifiManager
    private WifiReceiver mwReceiver; // Define a WifiReceiver
    private WifiInfo mWifiInfo; // Define a wifiInfo
    private List<ScanResult> locaWifiDeiviceList = new ArrayList<ScanResult>();

    private LinearLayout lLayoutWifiInputPage; // Show or hide ready configuration, start configuration
    private ImageView btnSLBack, ivSLPwdVisible; // Back button, start sonic configuration button, reality hide wifi password button
    private Button btnSLSearchBack = null;
    private Button btnSLStartConfig; // Next step;
    private Button btnWifiQuikConfig;// AP configuration
    private EditText etSLWifiSSID, etSLWifiPassword; // wifi name, wifi password

    private View soundWaveConfigConctentView = null;// ,
    // soundWaveConfigDemoState
    // = null;
    // //????????????listView???View
    private Dialog soundWaveConfigDialog = null;// ,
    // soundWaveConfigDemoStateDialog
    // = null; //
    // DiaLog for Realistic ListView
    private ImageView ivSoundWaveConfigWifiListViewBack; // wifi list back button
    private ListView lvSoundWaveConfigWifi; // Sonic configuration wifi list

    private ProgressDialog progressDialog; // Used to determine whether the wifi list appears
    private boolean bWifiPassword = true; // Used to determine whether to hide the password

    private LinearLayout llayoutSLSearchingPage; // Show or hide ready configuration, start configuration
    private FrameLayout flayoutSLSearchingAnimate; // Show search interface
    private RadarView searchAminatView;
    private String strConnSSID; // The wifi user name of the current mobile phone connection
    private MediaPlayer soundPlayer = null;// Sound playback
    private MediaPlayer soundPlayerHint = null; //

    private static final int WIFI_CONNECT = 0x11;
    private static final int WIFI_CONNECT2 = 0x12;
    private static final int WIFI_NOT_CONNECT = 0x14;
    private static final int SEEK_DEVICE_OVERTIME = 0x13;
    private static final int MY_PERMISSION_REQUEST_LOCATION = 0;
    private boolean bWifiOpen = false;
    private AlertDialog.Builder wifiNoticeDialog = null;
    private boolean bIsNoticeShow = false; //
    private boolean bIsConfiging = false; //

    private int nTimeoutDetectID = 0;

    private TextView tvTimeLeft = null;

    private ArrayList<DeviceInfo> deviceList = null;

    private int nConfigID = 0;
    private int mWifiEnrcrypt;

    private int n_BindDeviceThreadID = 0;
    private int bindDevice_result;

    private DeviceInfo Editinfo;

    boolean bHasUpdate = false;
    boolean bNewDevFound = false;

    static final int DEVICE_IS_EXISTANCE = 10004; // Device already exists, adding failed

    private LinearLayout llWifiQuikConfig;


    private DeviceNetworkSetting deviceNetworkSetting;
    private DeviceInfo deviceInfo;
    private String TAG="netWork Check";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // add August 25, 2016 Set the screen to be always on to avoid the screen when Smartlink
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// Hide title ???
        setContentView(R.layout.activity_smartlink_wifi_config);

        deviceNetworkSetting= new DeviceNetworkSetting();
        deviceInfo=LocalDefines.getDeviceInfoFromSP(this);

        LoginHandle loginHandle= LocalDefines.Device_LoginHandle;
//        NetworkConfigInfo networkConfigInfo=deviceNetworkSetting.getNetworkConfig(deviceInfo, loginHandle);
//        List<WifiInfo> wifiInfoList = DeviceNetworkSetting. getVisibleWifiListFormDevice(loginHandle, deviceInfo, true );
        Log.d(TAG, "onCreate: "+" !!!" +loginHandle);
        initView(); // Initialize interface controls

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        initWifi(); // Initialize wifi parameters and enable wifi broadcast
        wifiChooseWindow(); // Initialize the Dialog of the real WiFi list
    }


    @Override
    public void onStop() {
        if (soundWaveConfigDialog != null && soundWaveConfigDialog.isShowing()) {
            soundWaveConfigDialog.dismiss();
        }
        try {
            if (soundPlayerHint != null) {
                soundPlayerHint.stop(); // Turn off the beep
            }

            if (soundPlayer != null) {
                soundPlayer.stop(); // Turn off the sound
            }
        } catch (Exception e) {

        }
        if (bIsConfiging) {

            DeviceScanner.stopSmartConnection();
            showInputPage();
            bIsConfiging = false;
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        soundPlayer = null;// Sound playback
        soundPlayerHint = null; //

        locaWifiDeiviceList = null;
        mWifiInfo = null;
        this.unregisterReceiver(mwReceiver);

        if (searchAminatView != null) {
            searchAminatView.recycleBitmap();
            System.gc();
        }
        super.onDestroy();

    }

    /**
     * Initialize the control
     */
    @SuppressWarnings("deprecation")
    private void initView() {
        llWifiQuikConfig = (LinearLayout) findViewById(R.id.llWifiQuikConfig);
        lLayoutWifiInputPage = (LinearLayout) findViewById(R.id.lLayoutWifiInputPage);

        btnSLBack = (ImageView) findViewById(R.id.btnSLBack);
        btnSLBack.setOnClickListener(this);

        ivSLPwdVisible = (ImageView) findViewById(R.id.ivSLPwdVisible);
        ivSLPwdVisible.setOnClickListener(this);

        btnSLStartConfig = (Button) findViewById(R.id.btnSLStartConfig);
        btnSLStartConfig.setOnClickListener(this);


        btnWifiQuikConfig = (Button) findViewById(R.id.btnWifiQuikConfig);// add
        // by
        // lin
        // 20160123
        boolean isZh = LocalDefines.isZh(this);
//        llWifiQuikConfig.setVisibility(isZh ? View.VISIBLE : View.GONE);
        btnWifiQuikConfig.setOnClickListener(this);

        etSLWifiSSID = (EditText) findViewById(R.id.etSLWifiSSID);

//        etSLWifiSSID.setInputType(InputType.TYPE_NULL);

//        etSLWifiSSID.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.length() == 0) {
//                    etSLWifiSSID.setEnabled(false);
//                } else {
//                    etSLWifiSSID.setEnabled(true);
//                }
//            }
//        });
//        etSLWifiSSID.setEnabled(true);

        etSLWifiPassword = (EditText) findViewById(R.id.etSLWifiPassword);

        lLayoutWifiInputPage.setVisibility(View.VISIBLE);
        etSLWifiPassword
                .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); // Show password by default

        llayoutSLSearchingPage = (LinearLayout) findViewById(R.id.llayoutSLSearchingPage);
        flayoutSLSearchingAnimate = (FrameLayout) findViewById(R.id.flayoutSLSearchingAnimate);

        flayoutSLSearchingAnimate.setBackgroundColor(Color.parseColor("#f9f9f9"));
        llayoutSLSearchingPage.setVisibility(View.GONE);

        btnSLSearchBack = (Button) findViewById(R.id.btnSLSearchBack);
        btnSLSearchBack.setOnClickListener(this);
        // gif
        searchAminatView = (RadarView) findViewById(R.id.searchAminatView);
        searchAminatView.setWillNotDraw(false); //
        tvTimeLeft = (TextView) findViewById(R.id.tvTimeLeft);
    }

    // Information box
    public void ShowAlert(String title, String msg) {

        if (hasWindowFocus()) {
            View view = View.inflate(this, R.layout.show_alert_dialog, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_title.setText(title);
            TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_content.setText(msg);
            new AlertDialog.Builder(this)
                    .setView(view)
                    .setPositiveButton(getString(R.string.alert_btn_OK), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            setResult(Activity.RESULT_OK);
                        }

                    }).show();
        }
    }

    /**
     * Initialize wifi
     */
    private void initWifi() {
        mWiFiAdnim = new WiFiAdnim(this);
        mWiFiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(mWiFiManager.getConnectionInfo()!=null)
        mWifiInfo = mWiFiManager.getConnectionInfo();

        // If the phone is connected to wifi then
        if (mWiFiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED && mWifiInfo != null) { // If wifi is on
            strConnSSID = mWifiInfo.getSSID();
            // System.out.println("GetSSID= "+strConnSSID+", "+strConnSSID.equalsIgnoreCase("0x"));//

            if (strConnSSID != null && strConnSSID.length() > 0
                    && !strConnSSID.equalsIgnoreCase("0x") && !strConnSSID.equalsIgnoreCase("<unknown ssid>")) {
                // System.out.println("GetSSID= "+strConnSSID.substring(0,
                // 1)+", "+strConnSSID.substring(strConnSSID.length()-1,
                // strConnSSID.length()));//add for test

                if (strConnSSID.substring(0, 1).equals("\"")
                        && strConnSSID.substring(strConnSSID.length() - 1,
                        strConnSSID.length()).equals("\"")) {
                    strConnSSID = strConnSSID.substring(1,
                            (strConnSSID.length() - 1)); // Get the name of the currently connected user, remove the double quotation marks
                }

                etSLWifiSSID.setText(strConnSSID);
            } else {
                etSLWifiSSID.setText("");
            }
            // btnSLStartConfig.setEnabled(true);
            if (lLayoutWifiInputPage.getVisibility() == View.VISIBLE) {
                if (isLanguage()) {
                    soundPlayerHint = MediaPlayer.create(this,
                            R.raw.input_wifi_pwd);
                    soundPlayerHint.setLooping(false);
                    soundPlayerHint.start();
                }
            }

        }
        else { // wifi is not turned on
            if (!bIsNoticeShow) {

                View view = View
                        .inflate(this, R.layout.show_alert_dialog, null);
                TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
                tv_title.setText(getString(R.string.wifiConnect));
                TextView tv_content = (TextView) view
                        .findViewById(R.id.tv_content);
                tv_content.setText(getString(R.string.wifi_start_bt));

                wifiNoticeDialog = new AlertDialog.Builder(this);
                wifiNoticeDialog.setView(view);
                wifiNoticeDialog.setPositiveButton(getString(R.string.wifi_is),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mWiFiAdnim.openWifi(); // ??????wifi
                                bIsNoticeShow = false;
                                progressDialog = new ProgressDialog(
                                        SmartLinkQuickWifiConfigActivity.this);
                                progressDialog
                                        .setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog
                                        .setMessage(getString(R.string.wifi_start));
                                progressDialog.show(); // Show progress bar

                            }
                        });
                wifiNoticeDialog.setNegativeButton(getString(R.string.wifi_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                bIsNoticeShow = false;
                            }
                        });
                wifiNoticeDialog.show();
                bIsNoticeShow = true;
            }

        }
        // /Register wifi broadcast receiver
        mwReceiver = new WifiReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); // Network status change
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); // wifi State change
        registerReceiver(mwReceiver, intentFilter);
        // //
        mWiFiAdnim.startScan(); // Start scanning the network

    }

    // Determine if wifi is open
    public boolean isWiFiActive() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            @SuppressWarnings("deprecation")
            NetworkInfo[] infos = connectivity.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if (ni.getTypeName().equals("WIFI") && ni.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * wifi selection window
     */
    private void wifiChooseWindow() {
        soundWaveConfigConctentView = LayoutInflater.from(getApplication())
                .inflate(R.layout.sound_wave_config_window, null);
        soundWaveConfigDialog = new Dialog(this, R.style.dialog_bg_transparent);
        soundWaveConfigDialog.setContentView(soundWaveConfigConctentView);
        soundWaveConfigDialog.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                // TODO Auto-generated method stub
                onSoundWaveConfigListViewDialogShow();
            }

        });
    }

    /**
     * Show wifi list
     */
    private void onSoundWaveConfigListViewDialogShow() {

        ivSoundWaveConfigWifiListViewBack = (ImageView) soundWaveConfigConctentView
                .findViewById(R.id.ivSoundWaveConfigBack);
        lvSoundWaveConfigWifi = (ListView) soundWaveConfigConctentView
                .findViewById(R.id.lvSoundWaveConfig);
        ivSoundWaveConfigWifiListViewBack.setOnClickListener(this);
        lvSoundWaveConfigWifi.setOnItemClickListener(this);

        // listView data is loaded and displayed
        if (locaWifiDeiviceList != null && locaWifiDeiviceList.size() > 0) {
            DeviceSoundWaveConfigAdapter deviceSoundWaveConfigAdapter = new DeviceSoundWaveConfigAdapter(
                    SmartLinkQuickWifiConfigActivity.this, locaWifiDeiviceList,
                    R.layout.time_zone_item, new String[]{"item_list"},
                    new int[]{R.id.tvTimeZone});
            if (lvSoundWaveConfigWifi != null) {
                lvSoundWaveConfigWifi.setAdapter(deviceSoundWaveConfigAdapter); //
            } else {
                lvSoundWaveConfigWifi = (ListView) soundWaveConfigConctentView
                        .findViewById(R.id.lvSoundWaveConfig);
                lvSoundWaveConfigWifi.setAdapter(deviceSoundWaveConfigAdapter); //
            }
        }

    }

    /**
     * Determine the current language
     *
     * @return
     */
    public boolean isLanguage() {
        boolean bisLanguage = false;

        String locale = Locale.getDefault().getLanguage();
        if (locale.equals("zh")) // If it's Chinese
        {
            bisLanguage = true;
        }
        return bisLanguage;
    }

    // List
    public class DeviceSoundWaveConfigAdapter extends BaseAdapter {

        private class ItemViewHolder {
            TextView tvTimeZone;
        }

        private List<ScanResult> mAppList;
        private LayoutInflater mInflater;
        private Context mContext;
        private String[] keyString;
        private int[] valueViewID;
        private ItemViewHolder holder;

        public DeviceSoundWaveConfigAdapter(Context c,
                                            List<ScanResult> appList, int resource, String[] from, int[] to) {
            mAppList = appList;
            mContext = c;
            mInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            keyString = new String[from.length];
            valueViewID = new int[to.length];
        }

        @Override
        public int getCount() {

            return mAppList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView != null) {
                holder = (ItemViewHolder) convertView.getTag();
            } else {
                convertView = mInflater.inflate(R.layout.time_zone_item, null);
                holder = new ItemViewHolder();
                holder.tvTimeZone = (TextView) convertView
                        .findViewById(R.id.tvTimeZone);
                convertView.setTag(holder);
            }

            if (mAppList != null && mAppList.size() > 0) {
                holder.tvTimeZone.setText(mAppList.get(position).SSID);
            }

            return convertView;
        }

    }

    /**
     * Used to determine whether the current search has timed out
     *
     * @author Administrator
     */
    public class TimeoutDetectThread extends Thread {

        private int nThreadID = 0;

        public TimeoutDetectThread(int nThreadID) {
            this.nThreadID = nThreadID;
        }

        @Override
        public void run() {

            int nCount = 83;
            while (nTimeoutDetectID == nThreadID) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {

                }
                nCount--;
                if (nCount <= 0) {
                    break;
                }
            }

            if (nThreadID == nTimeoutDetectID) { //

                Message msg = handler.obtainMessage();
                msg.arg1 = SEEK_DEVICE_OVERTIME;
                handler.sendMessage(msg);
            }

        }

    }

    private void showInputPage() {

        lLayoutWifiInputPage.setVisibility(View.VISIBLE);
        llayoutSLSearchingPage.setVisibility(View.GONE);
    }

    private void showSearchingPage() {
        lLayoutWifiInputPage.setVisibility(View.GONE);
        llayoutSLSearchingPage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View arg0) {

        switch (arg0.getId()) {
            case R.id.btnSLSearchBack: // Click the back button
                searchAminatView.stopAnimate();
                try {
                    if (isLanguage() && soundPlayerHint != null) {
                        soundPlayerHint.stop(); // ???????????????
                    }

                    if (soundPlayer != null) {
                        soundPlayer.stop(); // ????????????
                    }
                } catch (Exception e) {

                }

                bIsConfiging = false;

                DeviceScanner.stopSmartConnection();
                showInputPage();
                nTimeoutDetectID++;

                break;
            case R.id.btnSLBack: // Click the back button

                bIsConfiging = false;
                DeviceScanner.stopSmartConnection();

                try {
                    if (isLanguage() && soundPlayerHint != null) {
                        soundPlayerHint.stop(); // ???????????????
                    }

                    if (soundPlayer != null) {
                        soundPlayer.stop(); // ????????????
                    }
                } catch (Exception e) {

                }

                //Intent intent = new Intent(SmartLinkQuickWifiConfigActivity.this, MainActivity.class);
                //startActivity(intent);

                SmartLinkQuickWifiConfigActivity.this.finish();

                break;

            case R.id.btnSLStartConfig: // Reload the sound and click the next button

                // Reload sound

                if (isLanguage()) {
                    if (soundPlayerHint != null) {
                        soundPlayerHint.stop();
                    }

                    soundPlayerHint = MediaPlayer
                            .create(this, R.raw.device_perpare);
                    soundPlayerHint.setLooping(false);
                    soundPlayerHint.start();
                }

                String strSSID = etSLWifiSSID.getText().toString();
                String strPassword = etSLWifiPassword.getText().toString();
                nConfigID = LocalDefines.getConfigID();
                bIsConfiging = true;

                // Get the string of the currently connected SSID
                String currentConnectedSSIDName = LocalDefines.getCurrentConnectedWifiSSIDName(mWiFiManager);
                if (!(currentConnectedSSIDName.equals(strSSID)) && strSSID != null
                        && strSSID.length() > 0) {
                    connectToSpecifiedWifi(strSSID, strPassword, mWifiEnrcrypt);
                }
//                connectToSpecifiedWifi(strSSID, strPassword, mWifiEnrcrypt);

                //
                DeviceScanner.startSmartConnection(nConfigID, strSSID, strPassword); // Start sending smarkLink

                searchAminatView.startAnimate();
                showSearchingPage();

                StartSearchDevice(); // ?????????????????????
                if (isLanguage() && soundPlayerHint != null) {
                    soundPlayerHint.stop(); // ????????????
                }
                soundPlayer = MediaPlayer.create(this, R.raw.seekmusic); // ????????????
                soundPlayer.setLooping(true);
                soundPlayer.start();

                tvTimeLeft.setText("" + 80);
                nTimeoutDetectID++;

                CountDownTimer timer = new CountDownTimer(80000, 1000) {
                    int nThreadID = nTimeoutDetectID;
                    int nCount = 80;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        if (nThreadID != nTimeoutDetectID) {

                            return;
                        }
                        if (tvTimeLeft != null) {
                            nCount--;
                            if (nCount < 0)
                                nCount = 0;
                            try {
                                tvTimeLeft.setText("" + nCount);
                            } catch (Exception e) {

                            }

                        }
                    }

                    @Override
                    public void onFinish() {
                        if (nThreadID == nTimeoutDetectID) { //

                            Message msg = handler.obtainMessage();
                            msg.arg1 = SEEK_DEVICE_OVERTIME;
                            handler.sendMessage(msg);
                        }
                    }
                };
                timer.start();
                break;
            case R.id.ivSoundWaveConfigBack: // wifi????????????????????????

                if (soundWaveConfigDialog != null) {
                    soundWaveConfigDialog.dismiss();
                }

                break;

            case R.id.ivSLPwdVisible: // ?????????????????????????????????

                if (bWifiPassword) {
                    bWifiPassword = false;
                    etSLWifiPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivSLPwdVisible.setImageResource(R.drawable.netword_hide);
                    Editable etable = etSLWifiPassword.getText();
                    Selection.setSelection(etable, etable.length());
                } else {

                    bWifiPassword = true;
                    etSLWifiPassword
                            .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivSLPwdVisible
                            .setImageResource(R.drawable.netword_show_password);
                    Editable etable = etSLWifiPassword.getText();
                    Selection.setSelection(etable, etable.length());
                }

                break;

            case R.id.btnWifiQuikConfig:// add by lin 20160123 AP??????
                if (bIsSearching) {
                    StopSearchDevice(); // ????????????
                }

                if (Build.VERSION.SDK_INT >= 23) {
                    initGPS();
                } else {

                    Intent intentSeekFine = new Intent(this, DeviceAPConnectActivity.class);
                    this.startActivity(intentSeekFine);
                    this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    this.finish();
                }
                break;

            default:
                break;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION:
                if (permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Intent intentSeekFine = new Intent(
//                            SmartLinkQuickWifiConfigActivity.this,
//                            DeviceQuickConfigActivity.class);
//                    startActivity(intentSeekFine);
//                    this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
//                    this.finish();
                } else {
                    View view = View
                            .inflate(this, R.layout.show_alert_dialog, null);
                    TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
                    tv_title.setText(getString(R.string.str_permission_request));
                    TextView tv_content = (TextView) view
                            .findViewById(R.id.tv_content);
                    tv_content.setText(getString(R.string.str_permission_location));
                    new AlertDialog.Builder(this)
                            .setView(view)
                            .setNegativeButton(getResources().getString(R.string.str_permission_neglect), null)
                            .setPositiveButton(
                                    getResources().getString(R.string.str_permission_setting2),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // TODO Auto-generated method stub
                                            Intent intent = new Intent();
                                            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                            Uri uri = Uri.fromParts("package",
                                                    SmartLinkQuickWifiConfigActivity.this.getPackageName(), null);
                                            intent.setData(uri);

                                            startActivity(intent);
                                        }
                                    }).show();
                }
                break;
        }

    }

    /**
     * listView click event
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        // wifi list click event

        lvSoundWaveConfigWifi.setSelection(arg2);

        if (locaWifiDeiviceList != null && locaWifiDeiviceList.size() > 0) {
            String WifiSSID = locaWifiDeiviceList.get(arg2).SSID;
            mWifiEnrcrypt = encryCodeOfCapabilities(locaWifiDeiviceList
                    .get(arg2).capabilities);
            if (WifiSSID != null && WifiSSID.length() > 0) {
                etSLWifiSSID.setText(WifiSSID);
                etSLWifiPassword.setText("");
                Editable etable = etSLWifiSSID.getText();
                Selection.setSelection(etable, etable.length());
                if (soundWaveConfigDialog != null) {
                    soundWaveConfigDialog.dismiss();
                }
            }

        }

    }

    /**
     * Create an internal class to broadcast the hot information scanned out
     *
     * @author Administrator
     */
    private class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {

            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                // ??????????????????
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    etSLWifiSSID.setText("");
                    Log.d("TAG", "onReceive: wifi disconnected");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {

                    mWifiInfo = mWiFiManager.getConnectionInfo();

                    // ????????????wifi??????
                    strConnSSID = mWifiInfo.getSSID();
                    if (strConnSSID != null && strConnSSID.length() > 0 && !strConnSSID.equalsIgnoreCase("0x") && !strConnSSID.equalsIgnoreCase("<unknown ssid>")) {

                        if (strConnSSID.substring(0, 1).equals("\"")
                                && strConnSSID.substring(strConnSSID.length() - 1, strConnSSID.length()).equals("\"")) {
                            strConnSSID = strConnSSID.substring(1, (strConnSSID.length() - 1)); // ??????????????????????????????????????????????????????
                        }

                        etSLWifiSSID.setText(strConnSSID);
                    } else {
                        etSLWifiSSID.setText("");
                    }
                }

            } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {//wifi????????????
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);

                if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                    btnSLStartConfig.setEnabled(false);
                } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                    btnSLStartConfig.setEnabled(true);
                }
            }
            locaWifiDeiviceList = mWiFiManager.getScanResults(); // ????????????????????????

            // ????????????????????????locaWifiDeiviceList????????????
            if (!locaWifiDeiviceList.isEmpty()) {
                Collections.sort(locaWifiDeiviceList,
                        new Comparator<ScanResult>() {
                            @Override
                            public int compare(ScanResult object1,
                                               ScanResult object2) {
                                int i = Math.abs(object1.level)
                                        + "".compareTo(Math.abs(object2.level)
                                        + "");
                                return i;
                            }
                        });
            }

            if (locaWifiDeiviceList != null && locaWifiDeiviceList.size() > 0
                    && progressDialog != null) {
                progressDialog.dismiss();
                bWifiOpen = true;
            }
        }

    }

    /**
     * ????????????
     *
     * @param capabilities
     * @return
     */
    private int wifiEncrye(String capabilities) {
        int encrye = 1;

        if (capabilities.indexOf("WPA2") != -1) {
            encrye = 3;
        } else if (capabilities.indexOf("[WPA-PSK-TKIP+CCMP]") != -1
                || capabilities.indexOf("[WPA2-PSK-TKIP+CCMP]") != -1) {
            encrye = 3;
        } else if (capabilities.indexOf("[WEP]") != -1
                && capabilities.indexOf("[IBSS]") != -1) {
            encrye = 2;

        } else if (capabilities.indexOf("[WEP]") != -1) {
            encrye = 2;

        } else if (capabilities.indexOf("[WPA-PSK-CCMP]") != -1
                || capabilities.indexOf("[WPA-PSK-TKIP+CCMP]") != -1) {

            encrye = 3;

        } else if (capabilities.indexOf("[WPA2-PSK-CCMP]") != -1
                || capabilities.indexOf("[WPA2-PSK-TKIP+CCMP]") != -1) {
            encrye = 3;

        } else if (capabilities.indexOf("[ESS]") != -1) {
            encrye = 1;
        }

        return encrye;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // ??????????????????,?????????????????????
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            bIsConfiging = false;

            DeviceScanner.stopSmartConnection();
            try {
                if (soundPlayerHint != null) {
                    soundPlayerHint.stop(); // ???????????????
                }

                if (soundPlayer != null) {
                    soundPlayer.stop(); // ????????????
                }
            } catch (Exception e) {

            }

            if (lLayoutWifiInputPage.getVisibility() == View.VISIBLE) {
                //Intent intent = new Intent(this, MainActivity.class);
                //startActivity(intent);
                finish();

            } else if (llayoutSLSearchingPage.getVisibility() == View.VISIBLE) {

                DeviceScanner.stopSmartConnection();
                showInputPage();
                StopSearchDevice();
            }
        }
        return false;
    }

    // ///////////////////////////////////add by luo 20150407
    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {

            if (msg.arg1 == WIFI_CONNECT2) {
                if (!bWifiOpen) {
                    if (progressDialog != null) { // ??????wifi?????????????????????
                        progressDialog.dismiss();
                        Toast.makeText(SmartLinkQuickWifiConfigActivity.this,
                                getString(R.string.wifiListingFail),
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }

            // ??????????????????
            if (msg.arg1 == SEEK_DEVICE_OVERTIME) {
                // ????????????
                nTimeoutDetectID++;
                searchAminatView.stopAnimate();
                try {
                    if (isLanguage() && soundPlayerHint != null) {
                        soundPlayerHint.stop(); //
                    }

                    if (soundPlayer != null) {
                        soundPlayer.stop(); //
                    }
                } catch (Exception e) {

                }

                bIsConfiging = false;
                DeviceScanner.stopSmartConnection();
                showInputPage();

                ShowAlert(getString(R.string.snartLinkFailTitle),
                        getString(R.string.snartLinkFailHint));

            }

            // wifi????????????
            if (msg.arg1 == WIFI_CONNECT) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

            // wifi????????????
            if (msg.arg1 == WIFI_NOT_CONNECT) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                Toast.makeText(SmartLinkQuickWifiConfigActivity.this,
                        getString(R.string.connect_wifi_off),
                        Toast.LENGTH_SHORT).show();
            }

            // ??????????????????
            if (msg.arg1 == LocalDefines.DEVICE_SEARCH_RESULT) {
                nTimeoutDetectID++;
                bIsConfiging = false;

                DeviceScanner.stopSmartConnection();
                StopSearchDevice();
                DeviceInfo info = null;
                switch (msg.arg2) {
                    case LocalDefines.DEVICE_SEARCH_RESULT_OK:

                        if (deviceList != null && deviceList.size() > 0) {

                            // ?????????????????????
                            nTimeoutDetectID++;
                            if (soundPlayer != null) {
                                soundPlayer.stop();
                            }

                            lLayoutWifiInputPage.setVisibility(View.VISIBLE);
                            llayoutSLSearchingPage.setVisibility(View.GONE);
                            StopSearchDevice(); // ?????????????????????

                            Toast toast = Toast.makeText(SmartLinkQuickWifiConfigActivity.this,
                                    "????????????", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            CameraSetupActivity.deviceInfo = deviceList.get(0);

                            //Intent intent = new Intent(SmartLinkQuickWifiConfigActivity.this, MainActivity.class);
                            //startActivity(intent);

                            finish();

                        } else {
                            Toast toast = Toast.makeText(
                                    SmartLinkQuickWifiConfigActivity.this,
                                    getString(R.string.no_dev_found),
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            StartSearchDevice(); // ?????????????????????
                        }

                        break;
                    case LocalDefines.DEVICE_SEARCH_RESULT_FAIL:

                        Toast toast = Toast.makeText(
                                SmartLinkQuickWifiConfigActivity.this,
                                getString(R.string.no_dev_found),
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        break;
                }

            }
            else if (msg.arg1 == LocalDefines.BIND_DEVICE_RESULT_CODE) {
                String searchResultMsg = getString(R.string.add_device);
                Bundle bundle = msg.getData();
                DeviceInfo info2 = (DeviceInfo) bundle

                        .getParcelable("Bind_info");
                int index = bundle.getInt("Bind_index");
                if (msg.arg2 == 0) {

//                    if (!DatabaseManager.IsInfoExist(info2)) {
//                        bNewDevFound = true;
//                        if (DatabaseManager.AddServerInfo(info2)) {
//
//                            if (index == 0) {
//                                searchResultMsg = searchResultMsg
//                                        + info2.getStrName();
//                            } else {
//                                searchResultMsg = searchResultMsg + ", "
//                                        + info2.getStrName();
//                            }
//                        }
//                    } else {// ???????????????????????????????????????ip?????????????????????
//                        if (info2.getIsAlarmOn() == 0) {
//                            DatabaseManager.UpdateServerInfoState(info2);
//                        } else {
//                            DatabaseManager
//                                    .UpdateServerInfoStateWithAlarmState(info2);
//                        }
//                        bHasUpdate = true;
//
//                    }
                    if (deviceList.size() > 0) {

                        if (bNewDevFound) {
                            // add by luo 20150714
//                            LocalDefines.reloadDeviceInfoList();
//                            LocalDefines.isDeviceListSet = false;
//                            LocalDefines.nClientDeviceSettingThreadID++;
//                            new RegistClientWithDeviceArrayToServer(this,
//                                    LocalDefines.nClientDeviceSettingThreadID)
//                                    .start();
//
//                            LocalDefines.isAlibabaDeviceListSet = false;
//                            LocalDefines.nAlibabaClientDeviceSettingThreadID++;
//                            new AlibabaRegistClientWithDeviceArrayToServer(this, LocalDefines.nAlibabaClientDeviceSettingThreadID).start();
                            // end add by luo 20150714

                            // ?????????????????????
                            nTimeoutDetectID++;
                            if (soundPlayer != null) {
                                soundPlayer.stop();
                            }

                            lLayoutWifiInputPage.setVisibility(View.VISIBLE);
                            llayoutSLSearchingPage.setVisibility(View.GONE);
                            StopSearchDevice(); // ?????????????????????
                            //Intent intent = new Intent(SmartLinkQuickWifiConfigActivity.this, MainActivity.class);
                            //startActivity(intent);

                            finish();

                        } else {
                            Toast toast_1 = Toast.makeText(
                                    SmartLinkQuickWifiConfigActivity.this,
                                    getString(R.string.search_finish),
                                    Toast.LENGTH_SHORT);
                            toast_1.setGravity(Gravity.CENTER, 0, 0);
                            toast_1.show();

                            StartSearchDevice(); // ?????????????????????
                        }
                    }
                    // ?????????

                } else if (msg.arg2 == DEVICE_IS_EXISTANCE) {
//                    if (info2.getIsAlarmOn() == 0) {
//                        DatabaseManager.UpdateServerInfoState(info2);
//                    } else {
//                        DatabaseManager
//                                .UpdateServerInfoStateWithAlarmState(info2);
//                    }

                }
//                else if (msg.arg2 == HttpUtils.RESULT_CODE_ERROR_IDENTITY) {
//                    httpResult401();
//                }
//                else if (msg.arg2 == HttpUtils.RESULT_CODE_SERVER_ERROR) {
//                    Toast.makeText(SmartLinkQuickWifiConfigActivity.this,
//                            getString(R.string.str_server_error),
//                            Toast.LENGTH_SHORT).show();
//                }
//                else if (msg.arg2 == HttpUtils.NEWERROR) {
//                    Toast.makeText(SmartLinkQuickWifiConfigActivity.this, R.string.Network_Error, Toast.LENGTH_SHORT).show();
//                }
                else {
                    Toast.makeText(SmartLinkQuickWifiConfigActivity.this, R.string.str_bind_device_error, Toast.LENGTH_SHORT).show();
                }
            }

        }

    };

    // ////////add by mai 2015-4-10 ??????????????????\

    private int m_nSearchID = 0;//
    private boolean bIsSearching = false;
    private boolean mIsSearchingMode = false;
    DatagramSocket ipuStationudpSocket = null;
    DatagramSocket ipuAPudpSocket = null;

    // Start device search
    public boolean StartSearchDevice() {

        try {
            if (!Functions.isNetworkAvailable(this.getApplicationContext())) {// ???????????????

                Toast toast = Toast.makeText(this.getApplicationContext(),
                        getString(R.string.toast_network_unreachable),
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        startBroadCastUdpThread();

        return true;

    }

    // Stop device search
    public void StopSearchDevice() {

        bIsSearching = false;
        m_nSearchID++;
        mIsSearchingMode = false;
    }

    public void startBroadCastUdpThread() {
        m_nSearchID++;
        bIsSearching = true;
        new BroadCastUdp(m_nSearchID).start();
    }

    // Device search thread
    public class BroadCastUdp extends Thread {

        private int nTreadSearchID = 0;

        public BroadCastUdp(int nSearchID) {
            nTreadSearchID = nSearchID;

        }

        public void run() {

            while (bIsSearching && nTreadSearchID == m_nSearchID) {

                deviceList = DeviceScanner.getDeviceListFromLan(nConfigID);

                if (deviceList != null && deviceList.size() > 0) {

                    Message msg = handler.obtainMessage();
                    msg.arg1 = LocalDefines.DEVICE_SEARCH_RESULT;
                    msg.arg2 = LocalDefines.DEVICE_SEARCH_RESULT_OK;
                    handler.sendMessage(msg);
                }

            }

        }

    }

    // //end add by mai 2015-4-10

    /**
     * Determine whether to open GPS location information add May 26, 2016
     */
    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // ??????GPS??????????????????????????????????????????
        if (!locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Toast.makeText(this, "?????????GPS", Toast.LENGTH_SHORT).show();

            View view = View.inflate(this, R.layout.show_alert_dialog, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_title.setText(getString(R.string.str_permission_request));
            TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_content.setText(getString(R.string.str_hotspot));
            new AlertDialog.Builder(this)
                    //
                    .setView(view)
                    .setNegativeButton(
                            getResources().getString(
                                    R.string.str_permission_neglect), null)
                    .setPositiveButton(
                            getResources().getString(
                                    R.string.str_permission_setting2),
                            (dialog, which) -> {
                                // ???????????????????????????????????????GPS
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                // startActivityForResult(intent, 0); //
                                // ???????????????????????????????????????
                                startActivity(intent);
                            }).show();
        } else {
            Intent intentSeekFine = new Intent(this, DeviceAPConnectActivity.class);
            this.startActivity(intentSeekFine);
            this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            this.finish();
            ;
        }
    }

    /**
     * // Connect to designated wifi add June 21, 2016
     *
     * @param ssid
     * @param psw
     * @param type
     */
    private void connectToSpecifiedWifi(String ssid, String psw, int type) {
        mWiFiAdnim.addNetWork(mWiFiAdnim.CreateWifiInfo(ssid, psw, type)); // ??????wifi
    }

    /**
     * Determine the encryption method add June 21, 2016
     *
     * @param capabilities
     * @return
     */
    private int encryCodeOfCapabilities(String capabilities) {
        int nResult = 0;
        if (capabilities == null) {
            return 1;
        }
        if (capabilities.indexOf("WPA2") != -1) {
            nResult = 3;
        } else if (capabilities.indexOf("[WPA-PSK-TKIP+CCMP]") != -1
                || capabilities.indexOf("[WPA2-PSK-TKIP+CCMP]") != -1) {
            nResult = 3;
        } else if (capabilities.indexOf("[WEP]") != -1
                && capabilities.indexOf("[IBSS]") != -1) {
            nResult = 2;

        } else if (capabilities.indexOf("[WEP]") != -1) {
            nResult = 2;

        } else if (capabilities.indexOf("[WPA-PSK-CCMP]") != -1
                || capabilities.indexOf("[WPA-PSK-TKIP+CCMP]") != -1) {

            nResult = 3;

        } else if (capabilities.indexOf("[WPA2-PSK-CCMP]") != -1
                || capabilities.indexOf("[WPA2-PSK-TKIP+CCMP]") != -1) {
            nResult = 3;

        } else if (capabilities.indexOf("[ESS]") != -1) {
            nResult = 1;
        }
        return nResult;
    }


    public void StartBindDeviceThread(int DeviceId, String DeviceName,
                                      String DevicePassword, DeviceInfo info, int infoIndex) {
        n_BindDeviceThreadID++;
        new BindDeviceThread(n_BindDeviceThreadID, DeviceName, DevicePassword,
                handler, DeviceId, info, infoIndex).start();
    }

    class BindDeviceThread extends Thread {
        private int m_BindDeviceThreadID;
        private Handler handler;
        private int m_DeviceId;
        private String m_DeviceName;
        private String m_DevicePassword;
        private DeviceInfo info;
        private int infoIndex;

        public BindDeviceThread(int BindDeviceThreadID, String DeviceName,
                                String DevicePassword, Handler handler, int DeviceId,
                                DeviceInfo info, int infoIndex) {
            this.m_BindDeviceThreadID = BindDeviceThreadID;
            this.handler = handler;
            this.m_DeviceId = DeviceId;
            this.m_DeviceName = DeviceName;
            this.m_DevicePassword = DevicePassword;
            this.info = info;
            this.infoIndex = infoIndex;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            if (m_BindDeviceThreadID == n_BindDeviceThreadID) {
                try {
                    postBindDeviceData(m_DeviceId, m_DeviceName,
                            m_DevicePassword, info, infoIndex);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            Message msg = handler.obtainMessage();
            msg.arg1 = LocalDefines.BIND_DEVICE_RESULT_CODE;
            msg.arg2 = bindDevice_result;
            Bundle data = new Bundle();
            data.putParcelable("Bind_info", info);
            data.putInt("Bind_index", infoIndex);
            msg.setData(data);
            handler.sendMessage(msg);

        }
    }

    public void postBindDeviceData(int DeviceId, String DeviceName, String DevicePassword, DeviceInfo info, int infoIndex)
            throws JSONException {
//        long time = System.currentTimeMillis();
//
//        String LoginSign = "accesstoken=" + DeviceListViewFragment._Token
//                + "&deviceaccount=" + DeviceName + "&deviceid=" + DeviceId
//                + "&devicepassword=" + DevicePassword + "&timestamp=" + time
//                / 1000 + "hsshop2016";
//        String MDLoginSign = LoginActivity.md5(LoginSign);
//
//        JSONObject json = new JSONObject();
//        json.put("sign", MDLoginSign);
//        json.put("timestamp", time / 1000);
//        json.put("accesstoken", DeviceListViewFragment._Token);
//        json.put("deviceid", DeviceId);
//        json.put("deviceaccount", DeviceName);
//        json.put("devicepassword", DevicePassword);
//        String content = json.toString();
//
//        String strURL = HttpUtils.HTTP_REQUEST_PREFIX + "device/bind";
//        String Recresult = HttpUtils.HttpPostData(strURL, content);
//
//        if (Recresult != null) {
//            JSONObject Bindjson = new JSONObject(Recresult);
//            String bindresult = Bindjson.getString("result");
//            int result = Integer.valueOf(bindresult);
//            bindDevice_result = result;
//            if (result == 0) {
//                int bindTime = Integer.valueOf(Bindjson.getInt("update_timestamp"));
//                DeviceListViewFragment.SaveUpdateDeviceTime(bindTime);
//            }
//        }
    }

    private void httpResult401() {
//        View view = View.inflate(this, R.layout.show_alert_dialog, null);
//        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
//        tv_title.setText(getString(R.string.str_Notic_Close_APP));
//        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
//        // tv_content.setVisibility(View.GONE);
//        tv_content.setText(getString(R.string.str_401));
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                //
//                .setView(view)
//                .setPositiveButton(getString(R.string.alert_btn_OK),
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int whichButton) {
//                                // ??????????????????
//                                SharedPreferences shareAppMode = getSharedPreferences(
//                                        "ShareAPPMODE", Activity.MODE_PRIVATE);
//                                Editor modeEditor = shareAppMode.edit();
//                                modeEditor.putInt("GetModeNum", 0);
//                                modeEditor.commit();
//                                HomePageActivity.AppMode = 0;
//
//                                // ??????????????????0
//                                SharedPreferences shareLocalTime = getSharedPreferences("SaveTimeTamp",
//                                        Activity.MODE_PRIVATE);
//                                SharedPreferences.Editor editor = shareLocalTime.edit();
//                                editor.putInt("TimeTamp", 0);
//                                editor.commit();
//
//                                // ?????????????????????????????????????????????
//                                Intent intent = new Intent(
//                                        SmartLinkQuickWifiConfigActivity.this,
//                                        LoginActivity.class);
//                                startActivity(intent);
//                                finish();
//
//                            }
//                        }).create();
//        dialog.setCancelable(false);
//        dialog.show();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null
                    && getCurrentFocus().getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

            }
        }
        return super.onTouchEvent(event);
    }

}










