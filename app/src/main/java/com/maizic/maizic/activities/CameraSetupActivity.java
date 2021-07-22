package com.maizic.maizic.activities;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.macrovideo.sdk.defines.Defines;
import com.macrovideo.sdk.defines.ResultCode;
import com.macrovideo.sdk.media.LoginHandle;
import com.macrovideo.sdk.media.LoginHelper;
import com.macrovideo.sdk.objects.DeviceInfo;
import com.macrovideo.sdk.objects.LoginParam;
import com.maizic.maizic.LocalDefines;
import com.maizic.maizic.NVPlayerPlayActivity;
import com.maizic.maizic.NVPlayerPlayFishEyeActivity;
import com.maizic.maizic.R;
import com.maizic.maizic.RecordActivity;
import com.maizic.maizic.SmartLinkQuickWifiConfigActivity;
import com.maizic.maizic.roomdatabase.LocalDataBase;
import com.maizic.maizic.roomdatabase.RoomDao;
import com.maizic.maizic.roomdatabase.RoomModel;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class CameraSetupActivity extends AppCompatActivity {

    Button btn, btn2, btn3;
    EditText etDeviceID, etDeviceUser, etDevicePwd;
    ProgressBar progress;

    static final int HANDLE_MSG_CODE_LOGIN_RESULT = 0x10;
    static final int HANDLE_MSG_CODE_LOGIN_RESULT2 = 0x11;

    private int m_loginID = 0;

    private DeviceInfo deviceTest = new DeviceInfo(-1, 32590556, "31019501",
            "192.168.1.1", 8800, "admin", "aaa111", "ABC", "32590556.nvdvr.net",
            Defines.SERVER_SAVE_TYPE_ADD);   //普通镜头

    private DeviceInfo deviceTest2 = new DeviceInfo(-1, 25908839, "25908839",
            "192.168.1.1", 8800, "admin", "aaa111", "ABC", "25908839.nvdvr.net",
            Defines.SERVER_SAVE_TYPE_ADD);

    public static DeviceInfo deviceInfo = null;
    private LoginHandle deviceParam = null;

    RoomDao roomDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalDataBase localDataBase = LocalDataBase.getInstance(this);
        roomDao = localDataBase.roomDao();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(LocalDefines.notificationID);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_camera_setup);


        findViewById(R.id.btnBackToHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        RoomModel model= new RoomModel("343535","65642424","");
//        saveCameraData(model);
        etDeviceID = findViewById(R.id.et_device_id);
        etDeviceUser = findViewById(R.id.et_device_username);
        etDevicePwd = findViewById(R.id.et_device_password);
        progress = findViewById(R.id.progressbar);

        btn = findViewById(R.id.button1);
        btn.setOnClickListener(v -> {

            Intent intent = new Intent(CameraSetupActivity.this, SmartLinkQuickWifiConfigActivity.class);
            startActivity(intent);

        });

        btn2 = findViewById(R.id.button);
        btn2.setOnClickListener(v -> {

            String deviceID = etDeviceID.getText().toString().trim();
            String deviceUser = etDeviceUser.getText().toString().trim();
            String devicePwd = etDevicePwd.getText().toString().trim();

            if (TextUtils.isEmpty(deviceID)) {
                if (deviceInfo == null) {
                    Toast.makeText(CameraSetupActivity.this, "Please enter the device ID", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (TextUtils.isEmpty(deviceUser)) {
                if (deviceInfo == null) {
                    Toast.makeText(CameraSetupActivity.this, "Please enter the device user name", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!TextUtils.isEmpty(deviceID)) {
                deviceInfo = new DeviceInfo(-1, Integer.parseInt(deviceID), deviceID,
                        "192.168.1.1", 8800, deviceUser, devicePwd, "ABC", deviceID + ".nvdvr.net",
                        Defines.SERVER_SAVE_TYPE_ADD);
            }

            progress.setVisibility(View.VISIBLE);
            btn.setClickable(false);
            btn2.setClickable(false);

            //testing...
//            RoomModel model= new RoomModel(deviceID,deviceUser,devicePwd);
//            saveCameraData(model);
            loginDevice();
        });

        btn3 = findViewById(R.id.button2);
        btn3.setOnClickListener(v -> {

            String deviceID = etDeviceID.getText().toString().trim();
            String deviceUser = etDeviceUser.getText().toString().trim();
            String devicePwd = etDevicePwd.getText().toString().trim();

            if (TextUtils.isEmpty(deviceID)) {
                if (deviceInfo == null) {
                    Toast.makeText(CameraSetupActivity.this, "Please enter the device ID", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (TextUtils.isEmpty(deviceUser)) {
                if (deviceInfo == null) {
                    Toast.makeText(CameraSetupActivity.this, "Please enter the device user name", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!TextUtils.isEmpty(deviceID)) {
                deviceInfo = new DeviceInfo(-1, Integer.parseInt(deviceID), deviceID,
                        "192.168.1.1", 8800, deviceUser, devicePwd, "ABC", deviceID + ".nvdvr.net",
                        Defines.SERVER_SAVE_TYPE_ADD);
            }


            LocalDefines._severInfoListData.clear();
            LocalDefines._severInfoListData.add(deviceInfo);

            Intent intent = new Intent(CameraSetupActivity.this, RecordActivity.class);
            startActivity(intent);
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        DeviceInfo deviceInfoFromSP = LocalDefines.getDeviceInfoFromSP(this);

        if (deviceInfoFromSP != null) {
            deviceInfo = deviceInfoFromSP;
        }

        if (deviceInfo != null) {

            if (deviceInfo.getnDevID() > 0) {
                etDeviceID.setText(String.valueOf(deviceInfo.getnDevID()));
            }

            if (!TextUtils.isEmpty(deviceInfo.getStrUsername())) {
                etDeviceUser.setText(deviceInfo.getStrUsername());
            }

            if (!TextUtils.isEmpty(deviceInfo.getStrPassword())) {
                etDevicePwd.setText(deviceInfo.getStrPassword());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (deviceInfo != null) {
            LocalDefines.setDeviceInfoToSP(this, deviceInfo);
        }
    }

    private void loginDevice() {
        m_loginID++;
        //new DeviceLoginThread(deviceInfo, m_loginID).start();
        login();
    }

    // new SDK
    private void login() {
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
    }

    //no use class
    class DeviceLoginThread extends Thread {
        DeviceInfo info = null;
        int nLoginID1 = 0;

        public DeviceLoginThread(DeviceInfo info, int nLoginID) {
            this.info = info;
            this.nLoginID1 = nLoginID;
        }

        public void run() {
            if (nLoginID1 == m_loginID) {

                LoginHandle deviceParam = null;
                if (info.getnSaveType() == Defines.SERVER_SAVE_TYPE_DEMO) {
                    deviceParam = LoginHelper.getDeviceParamEX(info,
                            info.getStrMRServer(), info.getnMRPort(), 0);
                } else {
                    deviceParam = LoginHelper.getDeviceParamEX(info, 0);
                }

                if (deviceParam != null
                        && deviceParam.getnResult() == ResultCode.RESULT_CODE_SUCCESS) {
                    Message msg = handler.obtainMessage();
                    msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
                    msg.arg2 = ResultCode.RESULT_CODE_SUCCESS;
                    Bundle data = new Bundle();
                    data.putParcelable("device_param", deviceParam);
                    msg.setData(data);
                    handler.sendMessage(msg);
                } else {
                    if (deviceParam == null) {
                        Message msg = handler.obtainMessage();
                        msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
                        msg.arg2 = ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
                        msg.arg2 = deviceParam.getnResult();
                        handler.sendMessage(msg);
                    }
                }
            }
        }
    }

    private Handler handler = new Handler() {
        // @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {

            if (msg.arg1 == HANDLE_MSG_CODE_LOGIN_RESULT) {
                progress.setVisibility(View.GONE);
                btn.setClickable(true);
                btn2.setClickable(true);

                switch (msg.arg2) {
                    case ResultCode.RESULT_CODE_SUCCESS: {

                        Bundle bundle = msg.getData();

                        LoginHandle loginHandle = bundle.getParcelable("device_param");
                        LocalDefines.Device_LoginHandle = loginHandle;
                        int camType = loginHandle.getCamType();

                        if (camType == LocalDefines.CAMTYPE_WALL
                                || camType == LocalDefines.CAMTYPE_CEIL) {
                            Intent intent = new Intent(CameraSetupActivity.this, NVPlayerPlayFishEyeActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            //MainActivity.this.finish();
                        } else {

                            String deviceID = etDeviceID.getText().toString().trim();
                            String deviceUser = etDeviceUser.getText().toString().trim();
                            String devicePwd = etDevicePwd.getText().toString().trim();
                            RoomModel model= new RoomModel(deviceID,deviceUser,devicePwd);

                            saveCameraData(model);
                            Intent intent = new Intent(CameraSetupActivity.this, NVPlayerPlayActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
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
                        progress.setVisibility(View.GONE);
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

    private void saveCameraData(final RoomModel roomModel) {


        Completable.fromAction(() ->
                roomDao.insert(roomModel)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        Log.d("TAG", "onSubscribe: Called");
                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "onComplete: Called");
//                        isLoading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", "onError: Called" + e.getMessage());
                    }
                });
    }


    private void ShowAlert(String title, String msg) {
        try {
            new AlertDialog.Builder(CameraSetupActivity.this).setTitle(title)
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.exit(0);
        }
        return super.onKeyDown(keyCode, event);


    }
}