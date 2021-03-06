package com.maizic.maizic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.hyfisheyepano.GLFisheyeView;
import com.macrovideo.sdk.defines.Defines;
import com.macrovideo.sdk.defines.ResultCode;
import com.macrovideo.sdk.media.LibContext;
import com.macrovideo.sdk.media.LoginHandle;
import com.macrovideo.sdk.media.NVPanoPlayer;
import com.macrovideo.sdk.media.Player;
import com.macrovideo.sdk.objects.PTZXPoint;
import com.macrovideo.sdk.tools.Functions;
import com.macrovideo.sdk.tools.SpSaveList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("NewApi")
public class NVPlayerPlayActivity extends Activity implements OnClickListener, OnTouchListener, OnItemClickListener, SurfaceTextureListener {

    static final short SHOWCODE_LOADING = 1001;// ????????????
    static final short SHOWCODE_NEW_IMAGE = 1002;// ?????????

    // static final short SHOWCODE_NOTICE=1003;//??????

    static final short SHOWCODE_VIDEO = 1004;// ??????
    static final short SHOWCODE_STOP = 2001;// ????????????
    static final short SHOWCODE_HAS_DATA = 3001;// ?????????

    static final short STAT_CONNECTING = 2001;// ?????????????????????
    static final short STAT_LOADING = 2002;// ??????????????????
    static final short STAT_DECODE = 2003;// ??????
    static final short STAT_STOP = 2004;// ??????
    static final short STAT_DISCONNECT = 2005;// ????????????
    static final short STAT_RESTART = 2006;// ????????????
    static final short STAT_MR_BUSY = 2007;// ????????????
    static final short STAT_MR_DISCONNECT = 2008;// ????????????

    private int m_nID = 0;
    private int m_nDeviceID = 0;
    private String m_strName = "IPC";
    private int m_nAddType = 0;

    private String m_strUsername = "1";
    private String m_strPassword = "1";

    private boolean m_bSpeak = false;
    private boolean m_bPTZ = false;
    private boolean m_bPTZX = false;
    private int m_nPTZXCount = 0;

    private boolean m_bReversePRI = true;
    private int mPlayingChn = -1;
    private boolean mPlaySound = false;

    // add by xie 2016-6-6
    private boolean m_isHD = true; // ???????????????
    private static boolean m_controlPanel = false; // ????????????

    // add by xie 2016-6-6
    private LinearLayout rlvideoSetting, layout_Cloud_Terrace, layoutPresetConfig, layoutReverse;
    private Button btn_lastDevice, btn_nextDevice, button_Cloud_Terrace;
    private TextView txt_ImageGQL;

    private Button mBtnStopAndPlay;
    private Button mBtnSound;
    // private Button btnListen;
    private Button mBtnSound2; // add by mai 2015-3-25

    private LinearLayout layoutTopBar = null;
    private boolean mIsPlaying = false;
    private LinearLayout layoutBottomBar;
    private LinearLayout layoutCenter = null;
    // private LinearLayout LayoutMicroPhoneBottom = null; //
    // layoutMicroPhone=null,

    private boolean bIsLeftPressed = false, bIsRightPressed = false, bIsUpPressed = false, bIsDownPressed = false;

    // ==add by mai 2015-1-20=========
    private ImageView mBtnBack;
    private Button mBtnBack2; // add by mai 2015-3-25
    // ===end add by mai 2015-1-20====

    private boolean mIsSpeaking = false;

    private boolean m_bFinish = false;
    int mScreenWidth = 0;// ?????????
    int mScreenHeight = 0;// ?????????

    private boolean mQLHD = true;// ??????????????????
    private int mStreamType = 0;// ?????????????????????

    private Button btnHD = null, btnSmooth = null;
    private Button mBtnImageQl = null, mBtnMic = null, mBtnReverse = null;
    private Button mBtnScreenShot = null;
    // add by xie 2016-6-7
    private TextView tBtnImageQl = null, tBtnMic = null, tBtnReverse = null, tBtnSound = null;
    private Button mBtnImageQl2 = null, mBtnMic2 = null, mBtnReverse2 = null, mBtnScreenShot2 = null; // add
    // by
    // mai
    // 2015-3-25


    private LinearLayout llLandscapeDefinition; // add by mai 2015-3-24
    private LinearLayout llVertical;
    private RelativeLayout llLandscape; // add by xie 2016-5-26
    private LinearLayout RlVertical;

    NVPanoPlayer mNVPanoPlayer;

    LinearLayout mContainers;
    private View top_bottom[] = new View[2];
    private View parentContainers[] = new View[Player.PLAYER_NUM];
    private View backgroundContainers[] = new View[Player.PLAYER_NUM];
    private ImageView img_v[] = new ImageView[Player.PLAYER_NUM];
    private int nScreenOrientation = Configuration.ORIENTATION_PORTRAIT;

    private GestureDetector mGestureDetector = null;
    private ScaleGestureDetector mScaleGestureDetector = null;
    private boolean mIsReverse = false;// add by luo 20141124

    // add by luo 20141217
    private Dialog iamgeViewDialog = null;
    private View iamgeViewConctentView = null;

    // end add by luo 20141217

    private TextView tvPlayDeviceID; // add by mai 2015-3-13
    private LinearLayout llPlayTalkback; // add by mai 2015-3-14
    private RelativeLayout controlPanel; // add by mai 2015-3-14
    private Button btnPTZLeft, btnPTZRight, btnPTZUP, btnPTZDown; // add by mai
    // 2015-3-14
    private boolean bAnyway = true;
    private Dialog screenshotDialog = null;

    private int FLING_MIN_DISTANCE = 10;
    private int FLING_MIN_VELOCITY = 80;
    private int FLING_MAX_DISTANCE = FLING_MIN_DISTANCE;

    private static int BTN_SCREENSHOT = 10010; // add by mai 2015-6-25 ??????????????????

    private ListView lvPreset; // add by mai 2015-7-30 ????????????
    private PopupWindow popupWindowMore; // add by mai 2015-7-30
    private PopupWindow popupWindowCloud;
    private Button btnPresetConfig; // add by xie 2016-6-6

    private PTZXPiontAdapter ptzxAdapter = null;// add by luo 20150803

    private static final int PTZX_RESULT_OK = 1111; // add by mai 2015-7-31
    // ???????????????????????????
    private static final int PTZX_RESULT_FAIL = 1112; // add by mai 2015-7-31
    // ???????????????????????????

    private ProgressBar progressBarPresetConfig; // add by mai 2015-7-31 ????????????
    private ImageView ivPresetLandscape; // add by mai 2015-7-31 ??????????????????????????????

    private static final int MY_PERMISSIONS_REQUEST_MICROPHONE = 2;
    private static final int MY_PERMISSION_REQUEST_STORAGE = 3;
    // add by mai 20151210
    private LoginHandle deviceParam = null;
    // end add by mai 20121210

    private LinearLayout layoutMicBtn, layout_ScreenShot, layoutRecord;

    private boolean isRecording = false;

    //	private GridView mGridView;
    private Button btn_selectArea, btnSelectAllArea;
    private Button btnCanelAllArea = null;
    private LinearLayout ll_alarmArea_btn = null;
    private ArrayList<String> Viewlist;
    private SelectAreaAdapter adapter;
    private ArrayList<Integer> SelectAreaList; // ?????????????????????????????????
    private Map<Integer, Integer> LocalAreaSelectmap;
    private ArrayList<Integer> ALL_area_alarmlist; // ????????????????????????
    // private ArrayList<Integer> Select_area_alarmlist;
    private Map<Integer, ArrayList<Integer>> map_Select_area;
    private boolean isSelectArea = false;
    private boolean isAllArea = false;

    private TextureView textureView = null;


    private boolean mIsHWChangedFromPortrait = false;
    private boolean mIsHWChangedFromLandscape = false;
    private SharedPreferences mHWConfigPrefer;

    private Button btn_saveSelectArea = null;
    private LinearLayout ll_alarmArea_explain = null;

    int startX = 0, startY = 0;
    boolean isCheck = false;


    private void createDialogs() { // ??????????????? ?????????????????????

        // add by luo 20141226
        iamgeViewConctentView = LayoutInflater.from(this).inflate(R.layout.screenshotdialog, null);
        screenshotDialog = new Dialog(this, R.style.progressDialog);
        screenshotDialog.setContentView(iamgeViewConctentView);
        screenshotDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Message msg = handler.obtainMessage();
                msg.arg1 = Defines.MSG_SCREENSHOT;
                handler.sendMessage(msg);
            }

        });
        screenshotDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }

        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // ??????????????????,?????????????????????
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (isSelectArea == true) {
                NVPlayerPlayActivity.this.finish();
            } else {
                exitCurrentActivity();
            }

        }

        return false;
    }

    // ?????????????????????????????????
    private void readSystemParam() {

        SharedPreferences ap = getSharedPreferences(Defines._fileName, MODE_PRIVATE);
        mPlaySound = ap.getBoolean("sounds", true);

    }

    // ????????????????????????
    public boolean writeSystemParam() {
        SharedPreferences ap = getSharedPreferences(Defines._fileName, MODE_PRIVATE);
        Editor editer = ap.edit();
        editer.putBoolean("sounds", mPlaySound);
        editer.commit();

        return true;
    }

    // ???????????????
    // msg??????????????????
    private void ShowAlert(String title, String msg) {
        try {
            new AlertDialog.Builder(NVPlayerPlayActivity.this).setTitle(title).setMessage(msg)
                    .setIcon(R.drawable.icon).setPositiveButton(getString(R.string.alert_btn_OK), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {

                    setResult(RESULT_OK);
                }
            }).show();

        } catch (Exception e) {

        }

    }

    @Override
    public void onPause() {
        OnPlayersPuase();
        super.onPause();
    }

    @Override
    public void onResume() {
        OnPlayersResume(); // ??????????????????
        nToolsViewShowTickCount = 8;
        timerThreadID++;
        new TimerThread(timerThreadID).start();
        if (mIsPlaying) {
            startPlay(); // ????????????
        } else {
            stopPlay(true); // ????????????
        }

        m_bFinish = false;
        NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notiManager.cancel(LocalDefines.notificationID);
        nScreenOrientation = this.getResources().getConfiguration().orientation;
        super.onResume();
    }

    // ??????????????????
    public void OnPlayersResume() {

        if (mNVPanoPlayer != null) {
            mNVPanoPlayer.getGLFisheyeView().onResume();
        }
    }

    @Override
    public void onDestroy() { // ???????????????
        if (mNVPanoPlayer != null) {
            mNVPanoPlayer.release();
            mNVPanoPlayer = null;
        }
        mContainers = null;
        top_bottom = null;
        parentContainers = null;
        backgroundContainers = null;
        img_v = null;

        // end add by mai 2015-7-9
        super.onDestroy();
    }

    @Override
    public void onStop() {
        timerThreadID++;
        m_nThreadID++; // ??????????????????????????????
        if (!m_bFinish) {// ??????????????????home?????????????????????????????????????????????
            NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle(getString(R.string.app_name)).setContentText(getString(R.string.app_notice))
                    .setTicker(getString(R.string.app_name))
                    .setWhen(System.currentTimeMillis())
                    .setOngoing(false);
            if (Build.VERSION.SDK_INT >= 23) {
                builder.setSmallIcon(R.drawable.my_device_3);
                builder.setLargeIcon(Functions.readBitMap(this, R.drawable.icon));
            } else {
                builder.setSmallIcon(R.drawable.icon_1);
            }


            notiManager.notify(LocalDefines.notificationID, builder.build());

            Intent intent = new Intent(this, NVPlayerPlayActivity.class);
            Bundle data = new Bundle();
            // ????????????????????????
            data.putString("name", m_strName);
            if (mIsPlaying) {// ??????
                data.putBoolean("isplaying", true);// ???????????????
                data.putInt("playing_chn", mPlayingChn);// ???????????????
            }
            data.putParcelable("", deviceParam);
            intent.putExtras(data);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(this, LocalDefines.notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        } else {

            LibContext.ClearContext();
        }
        tBtnImageQl.setText(getString(R.string.str_recording));
        mBtnImageQl.setBackgroundResource(R.drawable.btn_recording);

        if (isRecording == true) {
            // TODO: new sdk ????????????
            mNVPanoPlayer.stopRecord();
        }
        isRecording = false;

        m_bFinish = true;

        super.onStop();
    }

    // ??????
    private void ShowLandscapeView() {

        rlvideoSetting.setVisibility(View.GONE);
        synchronized (this) {
            bAnyway = false; // add by mai 2015-3-23
            nToolsViewShowTickCount = 5;

//			mGridView.setVisibility(View.GONE);
            btnSelectAllArea.setVisibility(View.GONE);
            rlvideoSetting.setVisibility(View.GONE);

            int nWidth = mScreenWidth;
            int nHeight = mScreenHeight;
            double dWidth = nHeight * 1.777778;
            if (dWidth < nWidth)
                nWidth = (int) dWidth;


            if (popupWindowMore != null) {
                popupWindowMore.dismiss();
            }

            hideToolsViews();

            if (layoutCenter != null) {
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(nWidth, nHeight);
                rlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                layoutCenter.setLayoutParams(rlp);
                layoutCenter.setPadding(0, 0, 0, 0);
            }

            nScreenOrientation = Configuration.ORIENTATION_LANDSCAPE;

            if (mNVPanoPlayer != null) {
                mNVPanoPlayer.onOreintationChange(nScreenOrientation);
            }


        }

    }

    // ??????
    private void ShowPortrailView() {
        if (isSelectArea == false) {
            rlvideoSetting.setVisibility(View.VISIBLE);
        } else {
            rlvideoSetting.setVisibility(View.GONE);
        }

        synchronized (this) {
            if (m_bPTZX) {
                btnPresetConfig.setVisibility(View.VISIBLE);
            } else {

            }

            if (mScreenWidth > mScreenHeight) {
                if (isSelectArea == false) {
                    ShowLandscapeView();
                }

            } else {
                // dip?????????px
                int padding_in_dp = 45; // 6 dps
                final float scale = getResources().getDisplayMetrics().density;
                int padding_in_px = (int) (padding_in_dp * scale + 0.5f); // ?????????
                bAnyway = true; // add by mai 2015-3-23
                showToolsViews();

                int nWidth = mScreenWidth;
                int statusBarHeight = LocalDefines.getStatusBarHeight(this);
                int nHeight = (int) ((mScreenHeight - statusBarHeight) * 0.6);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (mScreenWidth), (int) ((mScreenHeight - statusBarHeight) * 0.4));

                RlVertical.setLayoutParams(params);

                LinearLayout.LayoutParams Btnparams = new LinearLayout.LayoutParams((int) (mScreenWidth * 0.14), (int) (mScreenWidth * 0.14));

                if (layoutCenter != null) {

                    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(nWidth, nHeight);
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    layoutCenter.setLayoutParams(rlp);
                    layoutCenter.setPadding(0, padding_in_px, 0, 0);

                }

                // ?????????
                if (layoutPresetConfig != null) {

                    btnPresetConfig.setLayoutParams(Btnparams);

                }

                // ??????
                if (layout_Cloud_Terrace != null) {

                    button_Cloud_Terrace.setLayoutParams(Btnparams);
                }

                // ??????
                if (layout_ScreenShot != null) {

                    mBtnScreenShot.setLayoutParams(Btnparams);
                }

                // //??????
                if (layoutReverse != null) {

                    mBtnReverse.setLayoutParams(Btnparams);
                }
                //
                // //??????
                if (layoutMicBtn != null) {
                    mBtnMic.setLayoutParams(Btnparams);
                }
                //
                // //??????
                if (layoutRecord != null) {

                    mBtnImageQl.setLayoutParams(Btnparams);
                }

                nScreenOrientation = Configuration.ORIENTATION_PORTRAIT;

                if (mNVPanoPlayer != null) {
                    mNVPanoPlayer.onOreintationChange(nScreenOrientation);
                }

            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {

        super.onConfigurationChanged(config);//

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;// ?????????
        mScreenHeight = dm.heightPixels;// ?????????
        LocalDefines.loadResource(getResources());// ??????????????????

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            ShowLandscapeView();


        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {

            ShowPortrailView();
        }

    }

    /**
     * ????????????????????????ID
     */
    public void InitGLContainers() {


        mContainers = findViewById(R.id.playContainer1);

    }

    void InitGLViewCloseButton() {
        img_v[Player.WND_ID_0] = (ImageView) findViewById(R.id.close_but_0);


    }

    /**
     * ?????????????????????????????????
     *
     * @param isVisible true???????????????false???????????????
     */
    void SetCloseButtonVisible(boolean isVisible) {
        if (isVisible) {
            img_v[Player.WND_ID_0].setVisibility(View.VISIBLE);
            img_v[Player.WND_ID_1].setVisibility(View.VISIBLE);
            img_v[Player.WND_ID_2].setVisibility(View.VISIBLE);
            img_v[Player.WND_ID_3].setVisibility(View.VISIBLE);
        } else {
            img_v[Player.WND_ID_0].setVisibility(View.GONE);
            img_v[Player.WND_ID_1].setVisibility(View.GONE);
            img_v[Player.WND_ID_2].setVisibility(View.GONE);
            img_v[Player.WND_ID_3].setVisibility(View.GONE);
        }
    }

    /**
     * ?????? GLES2SurfaceView?????????
     */
    public void InitGLViewPlayer() {

        if (mNVPanoPlayer == null) {
            mNVPanoPlayer = new NVPanoPlayer(this, false);

            GLFisheyeView fisheyeView = new GLFisheyeView(getApplicationContext());
            fisheyeView.setMode(NVPanoPlayer.PANO_PLAY_MODE_13);
            fisheyeView.setOnTouchListener(this);
            mNVPanoPlayer.setGlFishView(fisheyeView);
        }

    }

    /**
     * ?????? GLES2SurfaceView?????????
     */
    public void ReleaseGLViewPlayer() {

        if (mNVPanoPlayer != null) {
            mNVPanoPlayer.release();
            mNVPanoPlayer = null;
        }


    }

    // ??????
    public void OnPlayersPuase() {

        if (mNVPanoPlayer != null) {
            mNVPanoPlayer.getGLFisheyeView().onPause();
        }
        LibContext.stopAll();
    }

    /**
     * ???OpenGL????????????????????????
     */
    public void ConnectGLViewToPlayer() {

        mContainers.addView(mNVPanoPlayer.getGLFisheyeView());

    }

    /**
     * ???????????????ID
     */
    public void InitGLViewProgressbar() {
        Player.GetProgressBars((ProgressBar) findViewById(R.id.spinner_0), Player.WND_ID_0);
    }

    // ?????????
    private void InitGLViewTouchEventEX() {
        if (layoutCenter == null)
            return;
        layoutCenter.setLongClickable(true);
        layoutCenter.setOnTouchListener(this);

    }

    public void SetGLViewPlayerMessageHandler() {

        if (mNVPanoPlayer != null && handler != null) {
            mNVPanoPlayer.GetHandler(handler);
        }

    }

    // ????????????ui?????????
    private Handler handler = new Handler() {
        // @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {


            // add by mai 2015-6-25 ???????????????????????????
            if (msg.arg1 == BTN_SCREENSHOT) {
                mBtnScreenShot.setEnabled(true);
                mBtnScreenShot2.setEnabled(true);
                return;
            }
            // end add by mai 2015-6-25
            // add by mai 2015-7-31 ?????????????????????
            if (msg.arg1 == PTZX_RESULT_OK) {
                progressBarPresetConfig.setVisibility(View.GONE);
                int nPTZXID = msg.arg2;
                boolean isSetOK = false;

                if (nPTZXID >= 0 && nPTZXID < 9) {
                    Bitmap image = getPTZXImage();
                    isSetOK = LocalDefines.updatePTZXPoints(m_nDeviceID, nPTZXID, image);
                }

                Toast.makeText(NVPlayerPlayActivity.this, getString(R.string.presetOK), Toast.LENGTH_SHORT).show();

                if (ptzxAdapter != null) {
                    ptzxAdapter.notifyDataSetChanged();
                }

                return;

            } else if (msg.arg1 == PTZX_RESULT_FAIL) {
                progressBarPresetConfig.setVisibility(View.GONE);
                Toast.makeText(NVPlayerPlayActivity.this, getString(R.string.presetFail), Toast.LENGTH_SHORT).show();
                return;
            }

            // end add by mai 2015-7-21

            // add by luo 20141226
            if (msg.arg1 == Defines.MSG_SCREENSHOT) {
                ScreenShot();

                return;
            }
            // end add by luo20141226

            if (msg.arg1 == Defines.MSG_HIDE_TOOLVIEW) {
                if (!mIsSpeaking && nScreenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (popupWindowMore != null && popupWindowMore.isShowing()) {// ????????????????????????????????????
                        nToolsViewShowTickCount = 5;
                        return;
                    }
                    hideToolsViews();
                }

                return;
            }

            switch (msg.arg2) // ???????????????
            {
                case Player.WND_ID_0:
                    if (msg.arg1 == 1) {
                        Player.ShowProgressBar(Player.WND_ID_0, true);
                    } else {
                        Player.ShowProgressBar(Player.WND_ID_0, false);
                    }
                    break;
                case Player.WND_ID_1:
                    if (msg.arg1 == 1) {
                        Player.ShowProgressBar(Player.WND_ID_1, true);
                    } else {
                        Player.ShowProgressBar(Player.WND_ID_1, false);
                    }
                    break;
                case Player.WND_ID_2:
                    if (msg.arg1 == 1) {
                        Player.ShowProgressBar(Player.WND_ID_2, true);
                    } else {
                        Player.ShowProgressBar(Player.WND_ID_2, false);
                    }
                    break;
                case Player.WND_ID_3:
                    if (msg.arg1 == 1) {
                        Player.ShowProgressBar(Player.WND_ID_3, true);
                    } else {
                        Player.ShowProgressBar(Player.WND_ID_3, false);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    static final int SIZE_CMDPACKET = 128;
    static final int SEND_BUFFER_SIZE = 512;
    static final int SESSION_FRAME_BUFFER_SIZE = 65536;
    static final int SEND_BUFFER_DATA_SIZE = 504; // 512 - 8 (header)
    static final int SEND_BUFFER_HEADER_SIZE = 8;
    static final int SP_DATA = 0x7f;

    static final int CMD_REQUEST = 0x9101;
    static final int CMD_AFFIRM = 0x9102;
    static final int CMD_EXIT = 0x9103;
    static final int CMD_ACCEPT = 0x9104;
    static final int CMD_CONNECTINFO = 0x9105;
    static final int CMD_STREAMHEAD = 0x9106;
    static final int CMD_UDPSHAKE = 0x9107;
    static final int CMD_ASKFORCNLNUM = 0x9108;
    static final int CMD_CHECKPSW = 0x9109;
    // ????????????
    // ????????????

    Bitmap bm = null;
    String folderName = "iCamSeeImages";

    // add by mai 2015-6-25 ??????????????????
    private int m_nThreadID = 0;

    private class ThreadBtnScreenShot extends Thread {

        private int nThreadID = 0;

        public ThreadBtnScreenShot(int nThreadID) {

            this.nThreadID = nThreadID;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (m_nThreadID == nThreadID) // ????????????activity?????????????????????UI
            {
                Message msg = handler.obtainMessage();
                msg.arg1 = BTN_SCREENSHOT;
                handler.sendMessage(msg);
            }

        }

    }

    // end by mai 2015-6-25
    // ???????????????sd???
    private boolean saveToSDCard(Bitmap image, String strFileName) {
        boolean bResult = false;

        if (image == null)
            return bResult;

        try {

            File file = new File(Functions.GetSDPath() + File.separator + LocalDefines.SDCardPath);
            if (!file.exists())
                file.mkdir();

            File imageFile = new File(file.getAbsolutePath() + File.separator + strFileName);
            FileOutputStream out = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            bResult = true;

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return bResult;
    }

    // end add by luo 20141226

    // ??????
    private Bitmap getPTZXImage() {

        // ?????????????????????
        bm = mNVPanoPlayer.screenShot();// ??????
        // TODO: new sdk ??????

        float deltaW = Defines._PTZXWidth / Defines._capWidth;
        float deltaH = Defines._PTZXHeight / Defines._capHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(deltaW, deltaH); // ??????????????????????????????
        Bitmap resizeBmp = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        return resizeBmp;
    }

    // add by luo 20141115 ????????????
    private void ScreenShot() {

        m_nThreadID++; // ????????????????????????????????????
        mBtnScreenShot.setEnabled(false); // add by mai 2015-6-25 ???????????????????????????????????????
        mBtnScreenShot2.setEnabled(false); // add by mai 2015-6-25 ???????????????????????????????????????
        new ThreadBtnScreenShot(m_nThreadID).start(); // add by mai 2015-6-25
        // ??????????????????????????????
        // ??????SDCard
        String strSavePath = Functions.GetSDPath();
        if (strSavePath == null) {
            screenshotDialog.dismiss();
            Toast.makeText(getApplicationContext(), getString(R.string.noticeSDCardNotExist), Toast.LENGTH_SHORT).show();
            return;
        }
        // ?????????????????????
        bm = mNVPanoPlayer.screenShot();// ??????
        // TODO: new sdk ??????

        if (bm != null) {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateString = formatter.format(new Date());

            String strFileName = dateString + "(" + m_strName + ")" + ".jpg";

            if (saveToSDCard(bm, strFileName)) { // ???????????????????????? add by mai 2015-4-9

                screenshotDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.noticeFileSaveToAlbumsOK), Toast.LENGTH_SHORT).show();
            } else {

                strFileName = strSavePath + "/" + strFileName;
                if (saveToSDCard(bm, strFileName)) {

                    screenshotDialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.noticeScreenShotOK), Toast.LENGTH_SHORT).show();

                } else {

                    screenshotDialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.noticeScreenShotFail), Toast.LENGTH_SHORT).show();
                }
            }
        } else {// ????????????
            screenshotDialog.dismiss();

            Toast.makeText(getApplicationContext(), getString(R.string.noticeScreenShotFail), Toast.LENGTH_SHORT).show();
        }

    }

    // end add by luo 20141115

    @SuppressWarnings("deprecation")
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Keep the screen always on
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notiManager.cancel(LocalDefines.notificationID);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nvplayer_playview);
        SelectAreaList = new ArrayList<Integer>();
        Bundle data = this.getIntent().getExtras();
        if (data != null) {
            // Get the parameters passed by the superior

            deviceParam = data.getParcelable("device_param");
            isSelectArea = data.getBoolean("isSelectArea");

            m_bReversePRI = deviceParam.isbReversePRI();// add by luo 20141219
            m_bPTZ = deviceParam.isbPTZ();
            m_bPTZX = deviceParam.isbPTZX();
            m_nPTZXCount = deviceParam.getnPTZXCount();
            m_nDeviceID = deviceParam.getnDeviceID();
            m_strUsername = deviceParam.getStrUsername();
            m_strPassword = deviceParam.getStrPassword();

            m_bSpeak = deviceParam.isbSpeak();

            m_strName = String.valueOf(deviceParam.getnDeviceID());
        }

        if (isSelectArea == true) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
        textureView = (TextureView) findViewById(R.id.texture_view);

        registerReceiver(this);
        // Create new picture
        DisplayMetrics dm = getResources().getDisplayMetrics();
        final float scale = getResources().getDisplayMetrics().density;
        mScreenWidth = dm.widthPixels;// Screen width
        mScreenHeight = dm.heightPixels;// Screen height
        LocalDefines.loadResource(getResources());// Load image resources

        FLING_MAX_DISTANCE = (int) (mScreenWidth / 3);

        // add by xie 2016 6 6
        // Preview the layout below
        rlvideoSetting = (LinearLayout) findViewById(R.id.rlvideoSetting);
        rlvideoSetting.getBackground().setAlpha(95);

        btn_lastDevice = (Button) findViewById(R.id.btn_lastDevice);
        btn_lastDevice.setOnClickListener(this);

        txt_ImageGQL = (TextView) findViewById(R.id.txt_definition);
        txt_ImageGQL.setOnClickListener(this);

        btn_nextDevice = (Button) findViewById(R.id.btn_nextDevice);
        btn_nextDevice.setOnClickListener(this);

        // add by xie 2016-6-7
        button_Cloud_Terrace = (Button) findViewById(R.id.button_Cloud_Terrace);
        // button_Cloud_Terrace.setOnClickListener(this);
        layout_Cloud_Terrace = (LinearLayout) findViewById(R.id.layout_Cloud_Terrace);
        layout_Cloud_Terrace.setOnClickListener(this);

        layout_ScreenShot = (LinearLayout) findViewById(R.id.layout_ScreenShot);
        layout_ScreenShot.setOnClickListener(this);

        layoutRecord = (LinearLayout) findViewById(R.id.layoutRecord);

        mBtnScreenShot = (Button) findViewById(R.id.buttonScreenShot);
        // mBtnScreenShot.setOnClickListener(this);

        mBtnReverse = (Button) findViewById(R.id.buttonReverse);
        // mBtnReverse.setOnClickListener(this);

        mBtnMic = (Button) findViewById(R.id.buttonMic);
        mBtnMic.setOnTouchListener(this);

        mBtnImageQl = (Button) findViewById(R.id.buttonImageGQL);
        mBtnImageQl.setOnClickListener(this);

        btnPresetConfig = (Button) findViewById(R.id.btnPresetConfig);
        // btnPresetConfig.setOnClickListener(this);

        layoutPresetConfig = (LinearLayout) findViewById(R.id.layoutPresetConfig);
        layoutPresetConfig.setOnClickListener(this);

        layoutReverse = (LinearLayout) findViewById(R.id.layoutReverse);
        layoutReverse.setOnClickListener(this);

        layoutMicBtn = (LinearLayout) findViewById(R.id.layoutMicBtn);

        tvPlayDeviceID = (TextView) findViewById(R.id.tvPlayDeviceID); // add by
        // mai
        // 2015-3-13
        llPlayTalkback = (LinearLayout) findViewById(R.id.llPlayTalkback); // add
        // by
        // mai
        btn_saveSelectArea = (Button) findViewById(R.id.btn_saveSelectArea); // 2015-3-14
        btn_saveSelectArea.setOnClickListener(this);

        ll_alarmArea_explain = (LinearLayout) findViewById(R.id.ll_alarmArea_explain);

        mGestureDetector = new GestureDetector(this, new PTZGestureListener(this));

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureListener());
        tvPlayDeviceID.setText(m_strName);

        layoutTopBar = (LinearLayout) findViewById(R.id.linearLayoutTopBar);
        layoutCenter = (LinearLayout) findViewById(R.id.playContainer);
        layoutBottomBar = (LinearLayout) findViewById(R.id.linearLayoutBottomBarPlay);

        llLandscape = (RelativeLayout) findViewById(R.id.llLandscape);
        llLandscape.getBackground().setAlpha(95);
        RlVertical = (LinearLayout) findViewById(R.id.RlVertical);

        if (RlVertical != null) {
            int statusBarHeight =LocalDefines.getStatusBarHeight(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (mScreenWidth), (int) ((mScreenHeight - statusBarHeight) * 0.4));
            RlVertical.setLayoutParams(params);

        }

        llLandscapeDefinition = (LinearLayout) findViewById(R.id.llLandscapeDefinition);

        llLandscapeDefinition.getBackground().setAlpha(95);

        ivPresetLandscape = (ImageView) findViewById(R.id.ivPresetLandscape);
        ivPresetLandscape.setOnClickListener(this);
        // end add by mai 2015-7-30

        btn_selectArea = (Button) findViewById(R.id.btn_selectArea);
        btn_selectArea.setOnClickListener(this);

        btnSelectAllArea = (Button) findViewById(R.id.btnSelectAllArea);
        btnSelectAllArea.setOnClickListener(this);

        btnCanelAllArea = (Button) findViewById(R.id.btnCanelAllArea);
        btnCanelAllArea.setOnClickListener(this);

        ll_alarmArea_btn = (LinearLayout) findViewById(R.id.ll_alarmArea_btn);
        Viewlist = new ArrayList<String>();

        map_Select_area = new HashMap<Integer, ArrayList<Integer>>();
        ALL_area_alarmlist = new ArrayList<Integer>();

        int index2 = 1;
        for (int i = 0; i < LocalDefines.ServerAlarmAreaList.size(); i++) {
            int index = LocalDefines.ServerAlarmAreaList.get(i);

            if (index == 0) {
                index2 = index;
                isAllArea = false;
            }
            if (index2 == 1) {
                isAllArea = true;
            }
        }


        for (int i = 0; i < LocalDefines.alarmcolumns * LocalDefines.alarmrows; i++) {
            Viewlist.add("");
            // The alarm area is 1 selected by default
            ALL_area_alarmlist.add(i, 1);

        }

        LocalAreaSelectmap = new HashMap<Integer, Integer>();


        // ///////////////////////////////////////////////
        InitGLContainers();
        InitGLViewProgressbar();
        // Create an OpenGl surface and set the rendering mode to request rendering
        InitGLViewPlayer();
        // Add OpenGL ES2.0 surface to the container
        ConnectGLViewToPlayer();
        // CreateHandler();
        SetGLViewPlayerMessageHandler();


        InitGLViewCloseButton();
        InitGLViewTouchEventEX(); // Button click event, don???t add it temporarily
        // Set environment variables
        LibContext.SetContext(mNVPanoPlayer, null, null, null);

        Player.SelectWindow(Player.WND_ID_0);


        ShowPortrailView();

        mBtnBack = (ImageView) findViewById(R.id.buttonBackToLogin);
        mBtnBack.setOnClickListener(this);


        mBtnSound = (Button) findViewById(R.id.btn_Listen);
        mBtnSound.setOnClickListener(this);

        tBtnMic = (TextView) findViewById(R.id.buttonMicTxt);

        // add by mai 2015-3-25
        mBtnScreenShot2 = (Button) findViewById(R.id.buttonScreenShot2);
        mBtnScreenShot2.setOnClickListener(this);

        mBtnMic2 = (Button) findViewById(R.id.buttonMic2);
        mBtnMic2.setOnTouchListener(this);

        mBtnReverse2 = (Button) findViewById(R.id.buttonReverse2);
        mBtnReverse2.setOnClickListener(this);

        mBtnSound2 = (Button) findViewById(R.id.buttonSound2);
        mBtnSound2.setOnClickListener(this);

        mBtnBack2 = (Button) findViewById(R.id.buttonBackToLogin2);
        mBtnBack2.setOnClickListener(this);

        tBtnImageQl = (TextView) findViewById(R.id.buttonImageGQLTxt);

        mBtnImageQl2 = (Button) findViewById(R.id.buttonImageGQL2); // add by
        // mai
        // 2015-3-25
        mBtnImageQl2.setOnClickListener(this); // add by mai 2015-3-25


        btnHD = (Button) findViewById(R.id.btnHD); // add by mai 2015-3-25
        // mBtnSmooth = (RadioButton) findViewById(R.id.rBtnSmooth);
        btnSmooth = (Button) findViewById(R.id.btnSmooth); // add by mai
        // 2015-3-25

        btnHD.setOnClickListener(this); // add by mai 2015-3-25
        btnSmooth.setOnClickListener(this); // add by mai 2015-3-25

        if (mQLHD) {
            mBtnImageQl.setVisibility(View.VISIBLE);
            mBtnImageQl2.setVisibility(View.VISIBLE); // add by mai 2015-3-25
        } else {
            mBtnImageQl.setVisibility(View.GONE);
            mBtnImageQl2.setVisibility(View.GONE); // add by mai 2015-3-25
        }

        if (mStreamType == 0) {// Currently HD or SD
            btnSmooth.setTextColor(this.getResources().getColor(R.color.font_color_blue));
            btnHD.setTextColor(Color.WHITE);

        } else {
            btnSmooth.setTextColor(Color.WHITE);
            btnHD.setTextColor(this.getResources().getColor(R.color.font_color_blue));
        }
        readSystemParam(); // Get uploaded and stored
        onSoundChange(); // ?????????????????????


        // ??????????????????
        mPlayingChn = 1;
        mIsPlaying = true;
        createDialogs();


        if (isSelectArea == true) {

            btn_saveSelectArea.setVisibility(View.VISIBLE);
            ll_alarmArea_explain.setVisibility(View.VISIBLE);

            layoutPresetConfig.setVisibility(View.GONE);
            layout_Cloud_Terrace.setVisibility(View.GONE);
            layout_ScreenShot.setVisibility(View.GONE);
            layoutReverse.setVisibility(View.GONE);
            layoutMicBtn.setVisibility(View.GONE);
            layoutRecord.setVisibility(View.GONE);
            rlvideoSetting.setVisibility(View.GONE);
            btnSelectAllArea.setVisibility(View.VISIBLE);
            ll_alarmArea_btn.setVisibility(View.VISIBLE);
            mNVPanoPlayer.setHWDecodeStatus(false, false);
        } else {

            btn_saveSelectArea.setVisibility(View.GONE);
            ll_alarmArea_explain.setVisibility(View.GONE);

            layoutPresetConfig.setVisibility(View.VISIBLE);
            layout_Cloud_Terrace.setVisibility(View.VISIBLE);
            layout_ScreenShot.setVisibility(View.VISIBLE);
            layoutReverse.setVisibility(View.VISIBLE);
            layoutMicBtn.setVisibility(View.VISIBLE);
            layoutRecord.setVisibility(View.VISIBLE);
            rlvideoSetting.setVisibility(View.VISIBLE);
            btnSelectAllArea.setVisibility(View.GONE);
            ll_alarmArea_btn.setVisibility(View.GONE);
        }


        mNVPanoPlayer.setHWDecodeStatus(false, false);
        textureView.setVisibility(View.GONE);
    }


    private class HWDecodeCheckChangeListener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mIsPlaying) {
                if (isRecording) {
                    isRecording = false;
                    // TODO: new sdk ????????????
                    mNVPanoPlayer.stopRecord();
                    tBtnImageQl.setText(getString(R.string.str_recording));
                    mBtnImageQl.setBackgroundResource(R.drawable.btn_recording);
                    Toast.makeText(NVPlayerPlayActivity.this, getString(R.string.str_record_ok), Toast.LENGTH_SHORT).show();
                }

                if (mIsHWChangedFromPortrait && buttonView.getId() == R.id.CWB_Decode) {
                    mIsHWChangedFromPortrait = false;
                    return;
                }

                if (isChecked) {
                    // ???????????????
                    textureView.setVisibility(View.VISIBLE);
                    mNVPanoPlayer.setHWDecodeStatus(isChecked, isChecked);
                } else {
                    // ????????????????????????
                    textureView.setVisibility(View.GONE);
                    mNVPanoPlayer.setHWDecodeStatus(isChecked, isChecked);
                }

                mNVPanoPlayer.restartPlayVideo();
            } else {

            }
        }

    }

    private CloseActivityReceiver mReceiver;

    /**
     * ????????????????????????????????????????????????????????????Activity??????
     *
     * @param context
     */
    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocalDefines.getReceiverActionString(context));
        mReceiver = new CloseActivityReceiver();
        registerReceiver(mReceiver, filter);
    }

    /**
     * ???????????????????????????
     */
    private void unRegisterReceiver() {
        if (mReceiver != null) {
            try {
                unregisterReceiver(mReceiver);
            } catch (Exception e) {

            }
        }
    }

    /**
     * ??????????????????????????????????????????Activity?????????
     *
     * @author Administrator
     */
    class CloseActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = LocalDefines.getReceiverActionString(NVPlayerPlayActivity.this);
            if (intent != null && intent.getAction().equals(action)) {
                stopCurrentActivityFromBroadcast();
            }
        }
    }

    /**
     * ???????????????????????????????????? add 2016???10???25???
     */
    private void stopCurrentActivityFromBroadcast() {
        if (isRecording == true) {
            isRecording = false;
            mNVPanoPlayer.stopRecord();
            // TODO: new sdk ????????????

        }
        if (mIsPlaying == false) {
            stopPlay(false);
            m_bFinish = true;
            LibContext.ClearContext();
            ReleaseGLViewPlayer();
            LocalDefines.B_UPDATE_LISTVIEW = true;
            unRegisterReceiver(); // add 2016???10???25???
            NVPlayerPlayActivity.this.finish();
        } else {
            setResult(RESULT_OK);
            stopPlay(false);
            LibContext.ClearContext();
            ReleaseGLViewPlayer();
            m_bFinish = true;
          LocalDefines.B_UPDATE_LISTVIEW = true; // add by mai 2015-6-29????????????
            unRegisterReceiver(); // add 2016???10???25???
            NVPlayerPlayActivity.this.finish();
        }
    }

    /**
     * add by mai 2015-7-30 ????????????????????????
     */
    @SuppressWarnings("deprecation")
    private void presetPopuwindow(boolean showTooView) {

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.ptzx_pannel_popupwindow, null);

        lvPreset = (ListView) view.findViewById(R.id.lvPreset);
        Button btnPresetCancel = (Button) view.findViewById(R.id.btnPresetCancel);
        progressBarPresetConfig = (ProgressBar) view.findViewById(R.id.progressBarPresetConfig);
        LinearLayout llTopTitle = (LinearLayout) view.findViewById(R.id.llTopTitle);

        presetList();

        // add by luo 20150803
        if (showTooView) {
            btnPresetCancel.setVisibility(View.GONE);
            llTopTitle.setVisibility(View.VISIBLE);
        } else {
            btnPresetCancel.setVisibility(View.GONE);
            llTopTitle.setVisibility(View.GONE);
        }
        // end add by luo 20150803
        /**
         * ?????????????????????
         */
        btnPresetCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (popupWindowMore != null) {
                    popupWindowMore.dismiss();
                }
            }
        });

        int window = 225;
        int onePreset_dp = 60; //
        int title_dp = 40;

        final float scale = getResources().getDisplayMetrics().density;
        int title_px = (int) (title_dp * scale + 0.5f);
        int onePreset_px = (int) (onePreset_dp * scale + 0.5f);
        float presetHeight = onePreset_px * 6 + title_px; // ????????????????????????????????????
        if (presetHeight > window) { // ??????????????????????????????????????????????????????????????????????????????
            presetHeight = 225;
        }

        if (mScreenHeight < mScreenWidth) {
            popupWindowMore = new PopupWindow(view, mScreenHeight * 4 / 5, LayoutParams.MATCH_PARENT);
        } else {
            int statusBarHeight =LocalDefines.getStatusBarHeight(NVPlayerPlayActivity.this);
            popupWindowMore = new PopupWindow(view, LayoutParams.MATCH_PARENT, (int) ((mScreenHeight - statusBarHeight) * 0.4));
        }

        popupWindowMore.setFocusable(true); // ????????????
        popupWindowMore.setOutsideTouchable(true); // ??????????????????????????????
        popupWindowMore.setBackgroundDrawable(new BitmapDrawable()); // ???????????????Back??????????????????????????????????????????
        popupWindowMore.showAtLocation(view, Gravity.BOTTOM | Gravity.LEFT, 0, 0);

    }

    // ????????????getview?????????ListView Adapter,?????????listview????????????????????????
    private class PTZXPiontAdapter extends BaseAdapter {

        private class ItemViewHolder {
            ImageView ivPresetImage;
            RoundCornerImageView btnPTZXSet;
            TextView tvPresetText;
            TextView tvIDText;
        }

        private ArrayList<Bitmap> mAppList;
        private LayoutInflater mInflater;
        private Context mContext;
        private String[] keyString;
        private int[] valueViewID;
        private ItemViewHolder holder;

        public PTZXPiontAdapter(Context c, ArrayList<Bitmap> appList, int resource, String[] from, int[] to) {
            mAppList = appList;
            mContext = c;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            keyString = new String[from.length];
            valueViewID = new int[to.length];
            System.arraycopy(from, 0, keyString, 0, from.length);
            System.arraycopy(to, 0, valueViewID, 0, to.length);

        }

        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView != null) {
                holder = (ItemViewHolder) convertView.getTag();
            } else {
                convertView = mInflater.inflate(R.layout.preset_count_item, null);
                holder = new ItemViewHolder();
                holder.ivPresetImage = (ImageView) convertView.findViewById(valueViewID[0]);
                holder.btnPTZXSet = (RoundCornerImageView) convertView.findViewById(valueViewID[1]);
                holder.tvPresetText = (TextView) convertView.findViewById(valueViewID[2]);
                holder.tvIDText = (TextView) convertView.findViewById(valueViewID[3]);

                convertView.setTag(holder);
            }

            PTZXPoint ptzxPoint = null;
            Bitmap image = null;
            if (LocalDefines._PTZXPointDevID == m_nDeviceID) {

                ptzxPoint = LocalDefines._ptzxPointList.get(position);
            }

            if (ptzxPoint != null) {
                image = ptzxPoint.getFaceImage();

            }

            if (image != null) {
                holder.ivPresetImage.setImageBitmap(image);
                holder.tvPresetText.setVisibility(View.GONE);
            } else {
                holder.tvPresetText.setVisibility(View.VISIBLE);
                holder.ivPresetImage.setImageBitmap(null);
            }
            ListViewButtonListener listener = new ListViewButtonListener(position);

            holder.tvIDText.setText("" + (position + 1));
            holder.ivPresetImage.setOnClickListener(listener);
            holder.btnPTZXSet.setOnClickListener(listener);

            return convertView;
        }

        class ListViewButtonListener implements OnClickListener {
            private int position;

            ListViewButtonListener(int pos) {
                position = pos;
            }

            @Override
            public void onClick(View v) {

                if (v.getId() == holder.ivPresetImage.getId()) { // ?????????????????????

                    locationPTZXPoint(position);
                    if (popupWindowMore != null) {
                        popupWindowMore.dismiss();
                    }
                } else if (v.getId() == holder.btnPTZXSet.getId()) { // ???????????????????????????
                    progressBarPresetConfig.setVisibility(View.VISIBLE);

                    startPTZXConfig(deviceParam, position, Defines.NV_PRESET_ACTION_RESET, deviceParam.getnDeviceID());

                }

            }
        }
    }

    /**
     * ????????????
     */
    private void presetList() {

        if (m_nPTZXCount > 0) {
            ArrayList<Bitmap> listItem = new ArrayList<Bitmap>();
            for (int i = 0; i < m_nPTZXCount; i++) {
                listItem.add(Functions.readBitMap(this, R.drawable.alarm_icon));

            }

            ptzxAdapter = new PTZXPiontAdapter(NVPlayerPlayActivity.this, listItem, R.layout.preset_count_item,// ListItem???XML??????
                    // ???????????????ImageItem???????????????
                    new String[]{"ItemPresetImage", "ItemPresetSetImage", "ItemPresetText", "ItemIDText"},
                    // ImageItem???XML?????????????????????ImageView,??????TextView ID
                    new int[]{R.id.ivPresetImage, R.id.btnPTZXSet, R.id.tvPresetText, R.id.tvPTZXID});

            if (lvPreset != null) {
                lvPreset.setCacheColorHint(Color.TRANSPARENT);
                // ??????????????????
                lvPreset.setAdapter(ptzxAdapter);
                // ????????????
                lvPreset.setOnItemClickListener(this);

            }

        } else {

        }

    }

    // ????????????
    private void stopPlay(boolean bFlag) {

        ptzTimerThreadID++;
        // add by luo 20141007
        if (m_nAddType != Defines.SERVER_SAVE_TYPE_DEMO) {

            if (mNVPanoPlayer != null) {

                // TODO: new sdk ??????
                bm = mNVPanoPlayer.screenShot();// ??????
                if (bm != null) {
                    // Bitmap image = Functions.zoomBitmap(bm, 160, 120);
                    Bitmap image = Functions.zoomBitmap(bm, 320, 240);
                    if (m_nDeviceID > 0) {
//						DatabaseManager.setFaceForDevID(m_nDeviceID, image, m_strUsername, m_strPassword);
                    } else {
//						DatabaseManager.setFaceForID(m_nID, image);
                    }
                }
            }
        }

        // end add by luo 20141007

        mIsPlaying = false;
        tvPlayDeviceID.setText(m_strName);
        mPlayingChn = -1;


        // TODO: new sdk ??????????????????
        mNVPanoPlayer.stopPlay();// add by luo 20141008

        Player.ShowProgressBar(Player.WND_ID_0, false);
    }

    // ????????????
    private void startPlay() {
        if (isSelectArea == false) {
            if (m_strName != null && m_strName.trim().length() > 0) {
                tvPlayDeviceID.setText(getString(R.string.Notification_Playing_Chn) + " " + m_strName);
            } else {
                tvPlayDeviceID.setText(getString(R.string.Notification_Playing_Chn) + " " + m_strName);
            }
        } else {
            tvPlayDeviceID.setText(getString(R.string.str_select_area_below));
        }

        mNVPanoPlayer.setnServerID(deviceParam.getnDeviceID());
        if (Player.CurrentSelPlayer() < 4) {
            Player.setPlaying(Player.CurrentSelPlayer(), true);


            mNVPanoPlayer.EnableRender();

            // TODO: new sdk ??????????????????
            mNVPanoPlayer.startPlay(0, 0, mStreamType, mPlaySound, deviceParam);
            mNVPanoPlayer.setReverse(mIsReverse);// add by
            // luo
            // 20141219
            mNVPanoPlayer.playAudio();
        }

        ptzTimerThreadID++;
        if (m_bPTZ) {
            new PTZTimerThread(ptzTimerThreadID).start();
        }

        mIsPlaying = true;
    }

    // add by luo 20150820
    private void onStreamTypeChange(int nType) {
        if (mStreamType == nType)
            return;

        mStreamType = nType;

        if (mStreamType == 0) {
            tBtnImageQl.setText(getString(R.string.str_recording));
            mBtnImageQl.setBackgroundResource(R.drawable.btn_recording);
            if (isRecording == true) {
                mNVPanoPlayer.stopRecord();
                // TODO: new sdk ????????????
            }
            isRecording = false;
            btnSmooth.setTextColor(Color.BLUE);
            btnHD.setTextColor(Color.WHITE);
            // mBtnSmooth.setChecked(true);
        } else if (mStreamType == 1) {
            tBtnImageQl.setText(getString(R.string.str_recording));
            mBtnImageQl.setBackgroundResource(R.drawable.btn_recording);
            if (isRecording == true) {
                mNVPanoPlayer.stopRecord();
                // TODO: new sdk ????????????
            }
            isRecording = false;
            btnSmooth.setTextColor(Color.WHITE);
            btnHD.setTextColor(Color.BLUE);
            // mBtnHD.setChecked(true);
        }

        if (mIsPlaying) {
            stopPlay(false);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // Log.i("TAG", "???????????????Channel" + Channel);
            startPlay();
        }
    }

    @SuppressWarnings("deprecation")
    private void onSoundChange() { // ??????????????????

        if (mPlaySound) {

            // add by xie 2016-6-6
            mBtnSound.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_back_sound_btn_new));


            mBtnSound2.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_back_sound_btn_horziontal));

        } else {
            mBtnSound.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_back_sound_btn_new2));

            mBtnSound2.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_back_sound_btn_horziontal2));

        }

        // to do
        int nChn = Player.CurrentSelPlayer();
        if (nChn >= 0 && nChn < 4) {
            mNVPanoPlayer.enableAudio(mPlaySound);
            // TODO: new sdk ????????????

        }

        writeSystemParam(); // ??????????????????
    }

    // ????????????
    @Override
    public void onClick(View v) {
        nToolsViewShowTickCount = 5;

        // ??????????????????
        if (v == btnSelectAllArea) {
            SelectAreaList.clear();
            for (int i = 0; i < LocalDefines.alarmcolumns * LocalDefines.alarmrows; i++) {
                SelectAreaList.add(i, 1);
            }
            isAllArea = true;
        }


        // ?????????????????????
        if (v == btnCanelAllArea) {
            SelectAreaList.clear();
            for (int i = 0; i < LocalDefines.alarmcolumns * LocalDefines.alarmrows; i++) {
                SelectAreaList.add(i, 0);
            }
            btnSelectAllArea.setText(R.string.btnSelectALL);
            isAllArea = false;
        }


        // ??????????????????
        if (v == btn_saveSelectArea) {
            boolean isEffectiveArea = false;
            for (int i = 0; i < SelectAreaList.size(); i++) {
                int n = SelectAreaList.get(i);
                if (n == 1) {
                    isEffectiveArea = true;
                }
            }

            if (!isEffectiveArea) {
                Toast.makeText(getApplicationContext(), getString(R.string.str_alarm_area_tip), Toast.LENGTH_SHORT).show();
                return;
            }

            LocalDefines.shouldUpdateSelectArea = true;
            // ??????????????????????????????????????????
            LocalDefines.Localmap_Update_area.put(deviceParam.getnDeviceID(), LocalDefines.shouldUpdateSelectArea);
          LocalDefines.LocalAlarmAreaList = SelectAreaList;

            List splist = new ArrayList<Integer>();
            SharedPreferences mySharedPreferences = getSharedPreferences("scenelist", Context.MODE_PRIVATE);
            Editor edit = mySharedPreferences.edit();
            try {
                String liststr = SpSaveList.SceneList2String(LocalDefines.LocalAlarmAreaList);
                edit.putString("spArea_list_for" + deviceParam.getnDeviceID(), liststr);
                edit.commit();
                String liststr2 = mySharedPreferences.getString("spArea_list_for" + deviceParam.getnDeviceID(), "");
                splist = SpSaveList.String2SceneList(liststr2);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            // ??????id??????????????????????????????
           LocalDefines.Localmap_Select_area.put(deviceParam.getnDeviceID(), LocalDefines.LocalAlarmAreaList);

            if (LocalDefines.Localmap_Select_area != null) {
                LocalDefines.LocalAlarmAreaList = LocalDefines.Localmap_Select_area.get(deviceParam.getnDeviceID());
            }

            NVPlayerPlayActivity.this.finish();
        }


        // ??????????????????
        if (v == btn_selectArea) {
            btnSelectAllArea.setVisibility(View.VISIBLE);
            rlvideoSetting.setVisibility(View.GONE);
            return;
        }


        // add by mai 2015-7-30 ?????????????????????
        if (v == layoutPresetConfig) {
            if (popupWindowMore != null && popupWindowMore.isShowing() == true) {
                return;
            }
            if (m_bPTZX) {
                presetPopuwindow(true);
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_preset_position), Toast.LENGTH_SHORT).show();
            }
        }


        // ?????????????????????????????????
        if (v == ivPresetLandscape) {
            if (m_bPTZX) {
                presetPopuwindow(false);
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_preset_position), Toast.LENGTH_SHORT).show();
            }
        }


        // end add by mai 2015-7-30 ??????
        if (v == layout_ScreenShot) {
            if (mIsPlaying) {
                // ?????? ??????????????????
                if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_STORAGE);
                    return;
                }
                screenshotDialog.show();
            }
            return;
        } else if (v == mBtnScreenShot2) { // add by mai 2015-3-25 //??????
            if (mIsPlaying) {
                // ?????? ??????????????????
                if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_STORAGE);
                    return;
                }
                screenshotDialog.show();

            }
            return;
        } else if (v == layoutReverse) {// ???????????? //add by luo 20141124
            // modify by luo 20150106
            if (m_bReversePRI) {
                mNVPanoPlayer.setCamImageOrientation(Defines.NV_IPC_ORIENTATION_REVERSE);
                // TODO: new sdk ????????????
            } else {
                mIsReverse = !mIsReverse;// add by luo 20141124
                mNVPanoPlayer.setReverse(mIsReverse);
            }
            // end modify by luo 20150106

            return;

        } else if (v == mBtnReverse2) {// ???????????? //add by mai 2015-3-25
            // modify by luo 20150106
            if (m_bReversePRI) {
                mNVPanoPlayer.setCamImageOrientation(Defines.NV_IPC_ORIENTATION_REVERSE);
                // TODO: new sdk ????????????
            } else {
                mIsReverse = !mIsReverse;// add by luo 20141124
                mNVPanoPlayer.setReverse(mIsReverse);
            }
            // end modify by luo 20150106

            return;

        } else // end add by mai 2015-3-25
            if (v == mBtnImageQl) {// ??????????????????????????????

                // ?????? ??????????????????
                if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_STORAGE);
                    return;
                }

                SaveRecordFile();

            }

        // add by xie 2016-6-6
        if (v == txt_ImageGQL) {
            if (m_isHD == true) {
                txt_ImageGQL.setText(getResources().getString(R.string.btnHD));
                onStreamTypeChange(1);
                txt_ImageGQL.setTextColor(this.getResources().getColor(R.color.font_color_blue));
                m_isHD = false;
            } else {
                txt_ImageGQL.setText(getResources().getString(R.string.standardDefinition));
                onStreamTypeChange(0);
                m_isHD = true;
                txt_ImageGQL.setTextColor(this.getResources().getColor(R.color.font_color_white));
            }

        } else if (v == btnHD) { // ????????????
            m_isHD = false;
            onStreamTypeChange(1);
            txt_ImageGQL.setTextColor(this.getResources().getColor(R.color.font_color_blue));
            txt_ImageGQL.setText(getResources().getString(R.string.btnHD));
        } else if (v == btnSmooth) {
            m_isHD = true;
            onStreamTypeChange(0);
            txt_ImageGQL.setTextColor(this.getResources().getColor(R.color.font_color_white));
            txt_ImageGQL.setText(getResources().getString(R.string.standardDefinition));
        } else // add by mai 2015-3-25
            if (v == mBtnSound) { // ??????

                v.setEnabled(false);

                if (mPlaySound) {
                    mPlaySound = false;
                } else {
                    mPlaySound = true;
                }
                onSoundChange();
                v.setEnabled(true);
            } else if (v == mBtnSound2) { // add by mai 2015-3-25
                v.setEnabled(false);
                if (mPlaySound) {
                    mPlaySound = false;
                } else {
                    mPlaySound = true;
                }
                onSoundChange();
                v.setEnabled(true);

            } else // end add by mai 2015-3-25
                if (v == mBtnStopAndPlay) { // ??????/????????????

                    mIsPlaying = !mIsPlaying;
                    if (mIsPlaying) {
                        mPlayingChn = 1;
                        startPlay();
                    } else {
                        stopPlay(true);
                    }

                } else if (v == mBtnBack) { // ????????????

                    if (isSelectArea == true) {
                        NVPlayerPlayActivity.this.finish();
                    } else {
                        exitCurrentActivity();
                    }

                } else if (v == mBtnBack2) { // add by mai 2015-3-25

                    if (isSelectArea == true) {
                        NVPlayerPlayActivity.this.finish();
                    } else {
                        exitCurrentActivity();
                    }

                } // end add by mai 2015-3-25

        // add by xie 2016-6-6
        if (v == layout_Cloud_Terrace) {

            if (popupWindowMore != null && popupWindowMore.isShowing() == true) {
                return;
            }

            if (m_controlPanel == false) {

                ptzContorlPopuwindow(true);
                m_controlPanel = true;
            } else {

                ptzContorlPopuwindow(false);
                m_controlPanel = false;
            }

        }
    }

    // ?????????????????????
    public void ShowNotic(String title, String msg) {// add by luo 20141010

        Toast toast = Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    /**
     * ??????????????????????????????
     */
    public boolean hasPTZXPRI() {
        if (!m_bPTZ) {
            ShowNotic(getString(R.string.deviceTurn), "");
            btnPTZDown.setEnabled(false);
            btnPTZLeft.setEnabled(false);
            btnPTZRight.setEnabled(false);
            btnPTZUP.setEnabled(false);
        }

        return m_bPTZ;
    }

    // ??????????????????
    @SuppressWarnings("deprecation")
    @Override
    public boolean onTouch(View v, MotionEvent event) {//

        if (v == this.layoutCenter) {
            mScaleGestureDetector.onTouchEvent(event);

            if (System.currentTimeMillis() - lScaleTime > 500) {
                mGestureDetector.onTouchEvent(event);
            }

            return true;
        } else if (v == this.mBtnMic) {
            return intercom(event, false);

        } else if (v == this.mBtnMic2) {
            return intercom(event, true);

        } else if (v == this.btnPTZLeft) {
            setPTXMoveView(event, PTZMoveDirectEnum.LEFT);

        } else if (v == this.btnPTZRight) {
            setPTXMoveView(event, PTZMoveDirectEnum.RIGHT);

        } else if (v == this.btnPTZUP) {
            setPTXMoveView(event, PTZMoveDirectEnum.UP);

        } else if (v == this.btnPTZDown) {
            setPTXMoveView(event, PTZMoveDirectEnum.DOWN);
        }
        return false;

    }

    private boolean intercom(MotionEvent event, boolean isBtn2) {

        if (!mIsPlaying) {
            return true;
        }

        int btnMicBgId = -1;
        Button btn = isBtn2 ? mBtnMic2 : mBtnMic;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (shouldRemind())
                    break;
                btnMicBgId = isBtn2 ? R.drawable.cross_screen_btn_talk_click : R.drawable.btn_talk_click;
                setIntercomViewValue(btn, btnMicBgId, true, View.VISIBLE, true);
                mNVPanoPlayer.startSpeak();
                // TODO: new sdk ????????????
                break;

            case MotionEvent.ACTION_CANCEL:
                btnMicBgId = isBtn2 ? R.drawable.cross_screen_btn_talk : R.drawable.btn_talk;
                setIntercomViewValue(btn, btnMicBgId, false, View.GONE, true);
                mNVPanoPlayer.stopSpeak();
                // TODO: new sdk ????????????
                break;

            case MotionEvent.ACTION_MOVE:
                btnMicBgId = isBtn2 ? R.drawable.cross_screen_btn_talk_click : R.drawable.btn_talk_click;
                setIntercomViewValue(btn, btnMicBgId, true, View.VISIBLE, false);
                break;

            case MotionEvent.ACTION_UP:
                btnMicBgId = isBtn2 ? R.drawable.cross_screen_btn_talk : R.drawable.btn_talk;
                setIntercomViewValue(btn, btnMicBgId, false, View.GONE, true);
                mNVPanoPlayer.stopSpeak();
                // TODO: new sdk ????????????
                break;

            default:
                break;
        }

        return true;
    }

    private boolean shouldRemind() {
        if (!m_bSpeak) {
            // ???????????????????????????
            Toast.makeText(this, getString(R.string.str_do_not_support_mic), Toast.LENGTH_SHORT).show();
            return true;
        }

        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(NVPlayerPlayActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(NVPlayerPlayActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_MICROPHONE);
            return true;
        }
        return false;
    }

    private void setIntercomViewValue(Button btn, final int btnMicBgId, boolean isSpeaking, final int llPlayTalkbackVisible, boolean isSleep) {
        btn.setBackgroundResource(btnMicBgId);
        llPlayTalkback.setVisibility(llPlayTalkbackVisible);
        mIsSpeaking = isSpeaking;
        if (!isSleep)
            return;
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private enum PTZMoveDirectEnum {
        LEFT(R.drawable.control_btn_left_click, R.drawable.control_btn_left), RIGHT(R.drawable.control_btn_right_click, R.drawable.control_btn_right), UP(R.drawable.control_btn_up_click, R.drawable.control_btn_up), DOWN(R.drawable.control_btn_down_click, R.drawable.control_btn_down);

        private int bgId1 = -1;
        private int bgId2 = -1;

        private PTZMoveDirectEnum(final int bgId1, final int bgId2) {
            this.bgId1 = bgId1;
            this.bgId2 = bgId2;
        }

    }

    private boolean setPTXMoveView(MotionEvent event, PTZMoveDirectEnum moveDirect) {

        Button btn = null;
        if (moveDirect == PTZMoveDirectEnum.LEFT)
            btn = btnPTZLeft;
        else if (moveDirect == PTZMoveDirectEnum.RIGHT)
            btn = btnPTZRight;
        else if (moveDirect == PTZMoveDirectEnum.UP)
            btn = btnPTZUP;
        else if (moveDirect == PTZMoveDirectEnum.DOWN)
            btn = btnPTZDown;

        // ????????????????????????
        if (!hasPTZXPRI()) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (m_bPTZ)
                    btn.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, moveDirect.bgId1)));
                if (moveDirect == PTZMoveDirectEnum.LEFT)
                    bIsLeftPressed = true;
                else if (moveDirect == PTZMoveDirectEnum.RIGHT)
                    bIsRightPressed = true;
                else if (moveDirect == PTZMoveDirectEnum.UP)
                    bIsUpPressed = true;
                else if (moveDirect == PTZMoveDirectEnum.DOWN)
                    bIsDownPressed = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                btn.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, moveDirect.bgId2)));
                setIsDirectPressedFalse();
                break;

            case MotionEvent.ACTION_UP:
                btn.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, moveDirect.bgId2)));
                setIsDirectPressedFalse();
                break;
            default:
                break;
        }

        return true;
    }

    private void setIsDirectPressedFalse() {
        bIsLeftPressed = false;
        bIsRightPressed = false;
        bIsUpPressed = false;
        bIsDownPressed = false;
    }

    private void showToolsViews() {// ?????????????????????
//		System.out.println("showToolsViews");// add for test
        if (popupWindowMore != null) {
            popupWindowMore.dismiss();
        }
        nToolsViewShowTickCount = 5;
        layoutBottomBar.setVisibility(View.VISIBLE);
        if (bAnyway) {
            layoutTopBar.setVisibility(View.VISIBLE);
            RlVertical.setVisibility(View.VISIBLE);
            // rgLandscapeDefinition.setVisibility(View.VISIBLE);
            ivPresetLandscape.setVisibility(View.GONE); // add by mai 2015-7-31
            // ???????????????
            llLandscape.setVisibility(View.GONE);
            llLandscapeDefinition.setVisibility(View.GONE);

        } else {
            layoutTopBar.setVisibility(View.GONE);
            RlVertical.setVisibility(View.GONE);
            // rgLandscapeDefinition.setVisibility(View.GONE);
            llLandscape.setVisibility(View.VISIBLE);
            llLandscapeDefinition.setVisibility(View.VISIBLE);

            if (m_bPTZX) {
                ivPresetLandscape.setVisibility(View.VISIBLE);
            } else {
                ivPresetLandscape.setVisibility(View.INVISIBLE);
            }
        }

        // LayoutMicroPhoneBottom.setVisibility(layoutBottomBar.getVisibility());

    }

    private void hideToolsViews() { // ?????????????????????

        nToolsViewShowTickCount = 0;
        layoutBottomBar.setVisibility(View.GONE);
        layoutTopBar.setVisibility(View.GONE);
        // mPTZPanel.setVisibility(View.GONE);
        llLandscape.setVisibility(View.GONE);
        llLandscapeDefinition.setVisibility(View.GONE);
        ivPresetLandscape.setVisibility(View.GONE); // add by mai 2015-7-31
        // setQLViewVisible(false);
        // LayoutMicroPhoneBottom.setVisibility(layoutBottomBar.getVisibility());
    }

    // /timer task
    // ????????????????????????????????????
    private int nToolsViewShowTickCount = 8;

    private int timerThreadID = 0;

    class TimerThread extends Thread {
        int mThreadID = 0;

        public TimerThread(int nThreadID) {
            mThreadID = nThreadID;
        }

        public void run() {
            while (mThreadID == timerThreadID) {

                nToolsViewShowTickCount -= 1;
                if (nToolsViewShowTickCount <= 0 && mThreadID == timerThreadID) {
                    Message message = new Message();
                    message.arg1 = Defines.MSG_HIDE_TOOLVIEW;
                    handler.sendMessage(message);
                    nToolsViewShowTickCount = 0;
                }

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    private float fScaleSize = 1.0f;
    private long lScaleTime = 0;

    class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {// ??????????????????????????????????????????

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (detector.getScaleFactor() > 1) {
                fScaleSize = fScaleSize - 0.008f;
                if (fScaleSize < 0.2) {
                    fScaleSize = 0.2f;
                } else {
//					if (VideoDecoder.sH264HWDecodeEnable) {
                    if (mNVPanoPlayer != null
                            && mNVPanoPlayer.getHWDecodeStatus()) {
                        textureView.setScaleX(1 / fScaleSize);
                        textureView.setScaleY(1 / fScaleSize);
                    } else {

                    }
                }

            } else if (detector.getScaleFactor() < 1) {
                fScaleSize = fScaleSize + 0.05f;
                if (fScaleSize > 1) {
                    fScaleSize = 1.0f;
                } else {
//					if (VideoDecoder.sH264HWDecodeEnable) {
                    if (mNVPanoPlayer != null
                            && mNVPanoPlayer.getHWDecodeStatus()) {
                        textureView.setScaleX(1 / fScaleSize);
                        textureView.setScaleY(1 / fScaleSize);
                    } else {

                    }
                }
            }

            lScaleTime = System.currentTimeMillis();
            //
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }

    }

    private int ptzTimerThreadID = 0;

    class PTZTimerThread extends Thread {// ??????????????????????????????
        int mThreadID = 0;

        public PTZTimerThread(int nThreadID) {
            mThreadID = nThreadID;
        }

        public void run() {
            boolean bLeft = false, bRight = false, bUp = false, bDown = false;
            while (mThreadID == ptzTimerThreadID) {
                bLeft = bIsLeftPressed;
                bRight = bIsRightPressed;
                bUp = bIsUpPressed;
                bDown = bIsDownPressed;

                if (bLeft && bRight) {
                    bLeft = false;
                    bRight = false;
                }

                if (bUp && bDown) {
                    bUp = false;
                    bDown = false;
                }

                if (bLeft || bRight || bUp || bDown) {
                    mNVPanoPlayer.setPTZAction(bLeft, bRight, bUp, bDown, 0);
                    // TODO: new sdk ????????????
                    // /
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }

        }
    }

    // OnGestureListener
    class PTZGestureListener extends SimpleOnGestureListener {// ????????????????????????

        public static final int MOVE_TO_LEFT = 0;
        public static final int MOVE_TO_RIGHT = 1;
        public static final int MOVE_TO_UP = 2;
        public static final int MOVE_TO_DOWN = 3;
        private int nStep = 0;

        boolean bTouchLeft = false, bTouchRight = false, bTouchUp = false, bTouchDown = false;
        double nVelocityX = 0;// ?????????????????????
        double nMoveDistanceX = 0;// ?????????????????????

        double nVelocityY = 0;// ?????????????????????
        double nMoveDistanceY = 0;// ?????????????????????

        float x1 = 0;
        float x2 = 0;
        float y1 = 0;
        float y2 = 0;

        PTZGestureListener(Context context) {
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (bIsLeftPressed || bIsRightPressed || bIsUpPressed || bIsDownPressed) {
                return true;
            }
            bTouchLeft = false;
            bTouchRight = false;
            bTouchUp = false;
            bTouchDown = false;
            nVelocityX = Math.abs(velocityX);// ?????????????????????
            nMoveDistanceX = Math.abs(e1.getX() - e2.getX());// ?????????????????????
            nVelocityY = Math.abs(velocityY);// ?????????????????????
            nMoveDistanceY = Math.abs(e1.getY() - e2.getY());// ?????????????????????

            if (nVelocityY >= nVelocityX) {
                nVelocityX = 0;
            } else {
                nVelocityY = 0;
            }

            if (nVelocityY < nVelocityX) {
                nStep = 0;

                if (nMoveDistanceX > FLING_MIN_DISTANCE) {
                    nStep = 1;
                    if (nMoveDistanceX > FLING_MAX_DISTANCE) {
                        nStep = (int) (nMoveDistanceX / FLING_MAX_DISTANCE);
                    }
                }
                if (nVelocityX > FLING_MIN_VELOCITY && nMoveDistanceX > FLING_MIN_DISTANCE) {
                    if (e1.getX() > e2.getX()) {
                        // @@System.out.println("onFling: Left");

                        bTouchLeft = true;
                    } else {
                        // @@System.out.println("onFling: Right");
                        bTouchRight = true;
                    }
                }

            } else if (nVelocityY > nVelocityX) {

                nStep = 0;

                if (nMoveDistanceX > FLING_MIN_DISTANCE) {
                    nStep = 1;
                    if (nMoveDistanceX > FLING_MAX_DISTANCE) {
                        nStep = (int) (nMoveDistanceX / FLING_MAX_DISTANCE);
                    }
                }

                if (nVelocityY > FLING_MIN_VELOCITY && nMoveDistanceY > FLING_MIN_DISTANCE) {
                    if (e1.getY() > e2.getY()) {
                        // @@System.out.println("onFling: UP");
                        bTouchUp = true;
                    } else {
                        // @@System.out.println("onFling: Down");
                        bTouchDown = true;
                    }

                }
            } else {

                if (nMoveDistanceY >= nMoveDistanceX) {
                    if (nVelocityY > FLING_MIN_VELOCITY && nMoveDistanceY > FLING_MIN_DISTANCE) {
                        if (e1.getY() > e2.getY()) {
                            // @@System.out.println("onFling: UP");
                            bTouchUp = true;
                        } else {
                            // @@System.out.println("onFling: Down");
                            bTouchDown = true;
                        }

                    }
                } else {
                    if (nVelocityX > FLING_MIN_VELOCITY && nMoveDistanceX > FLING_MIN_DISTANCE) {
                        if (e1.getX() > e2.getX()) {
                            // @@System.out.println("onFling: Left");
                            bTouchLeft = true;
                        } else {
                            // @@System.out.println("onFling: Right");
                            bTouchRight = true;
                        }
                    }
                }

            }

            if (nStep > 5) {
                nStep = 5;
            }


            mNVPanoPlayer.setPTZAction(bTouchLeft, bTouchRight, bTouchUp, bTouchDown, nStep);
            // TODO: new sdk ????????????

            return false;
        }

        /**
         * ?????????????????????onSingleTapUp????????????GestureDetector?????????????????????????????????????????????????????????????????????????????????
         * ??????????????????????????????????????????
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (layoutBottomBar.getVisibility() == View.VISIBLE) {

                if (/* layoutImageQL.getVisibility() != View.VISIBLE && */nScreenOrientation == Configuration.ORIENTATION_LANDSCAPE) {

                    hideToolsViews();
                }

            } else {

                showToolsViews();
            }
            return false;
        }

    }

    /**
     * add by mai 2015-7-30 ???????????????
     */
    private void startPTZXConfig(LoginHandle lhandle, int nPTZXID, int nPTZXAction, int nDeviceID)// ???????????????????????????
    {
        m_nPTZXID++;
        new PTZXThread(handler, m_nPTZXID, lhandle, nPTZXID, nPTZXAction, nDeviceID).start();

    }

    private void locationPTZXPoint(int nPTZXID) {// ????????????????????????????????????

        mNVPanoPlayer.callPTZXLocation(nPTZXID, deviceParam);

        // TODO: new sdk ???????????????
        //callPTZXLocation();
    }

    /**
     * add by mai 2015-7-30 ???????????????????????????
     *
     * @author Administrator
     */

    private int m_nPTZXID = 0;

    private class PTZXThread extends Thread {// ?????????????????????

        private Handler handler;
        private int m_ThreadConfigID = 0;
        LoginHandle lhandle = null;

        private int m_ThreadPTZXID = 0; // ?????????ID
        private int m_ThreadPTZXAction = 0; // ???????????????
        private int m_nDeviceID = 0;

        // ??????????????????????????????
        public PTZXThread(Handler handler, int configID, LoginHandle lhandle, int nPTZXID, int nPTZXAction, int nDeviceID) {
            m_ThreadConfigID = configID;
            this.handler = handler;
            this.lhandle = lhandle;
            this.m_ThreadPTZXID = nPTZXID;
            this.m_ThreadPTZXAction = nPTZXAction;
            this.m_nDeviceID = nDeviceID;
        }

        public void run() {

            int nConfigResult = mNVPanoPlayer.setPTZXLocation(m_ThreadPTZXID, lhandle, m_nDeviceID);
            // TODO: new sdk ???????????????
            if (m_nPTZXID == m_ThreadConfigID && nConfigResult == ResultCode.RESULT_CODE_SUCCESS) {// ???????????????????????????????????????????????????
                Message msg = handler.obtainMessage();
                msg.arg1 = PTZX_RESULT_OK;
                msg.arg2 = m_ThreadPTZXID;

                handler.sendMessage(msg);
            } else if (m_nPTZXID == m_ThreadConfigID) {
                Message msg = handler.obtainMessage();
                msg.arg1 = PTZX_RESULT_FAIL;
                msg.arg2 = nConfigResult;
                handler.sendMessage(msg);
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        // TODO Auto-generated method stub

        // ????????????????????????????????????

        int list = SelectAreaList.get(position);

        if (list == 0) {
            // ?????????????????????????????????

            view.setBackgroundColor(Color.parseColor("#ff0000"));
            SelectAreaList.remove(position);
            SelectAreaList.add(position, 1);
            LocalAreaSelectmap.clear();
            view.getBackground().setAlpha(51);

        } else {
            // ??????????????????????????????
            view.setBackgroundColor(Color.parseColor("#000000"));
            view.getBackground().setAlpha(20);
            SelectAreaList.remove(position);
            SelectAreaList.add(position, 0);
            LocalAreaSelectmap.clear();

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_MICROPHONE) {
            if (permissions[0].equals(Manifest.permission.RECORD_AUDIO) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // //??????????????????camera
                // Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
            } else {
                // ????????????????????????????????????

                new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.str_permission_request))
                        .setMessage(getResources().getString(R.string.str_permission_microphone2)).setNegativeButton(getResources().getString(R.string.str_permission_neglect), null).setPositiveButton(getResources().getString(R.string.str_permission_setting2), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        Uri uri = Uri.fromParts("package", NVPlayerPlayActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);

                    }
                }).show();
            }
        }
        if (requestCode == MY_PERMISSION_REQUEST_STORAGE) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // ????????????????????????????????????
                new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.str_permission_request))
                        .setMessage(getResources().getString(R.string.str_permission_storage1)).setNegativeButton(getResources().getString(R.string.str_permission_neglect), null).setPositiveButton(getResources().getString(R.string.str_permission_setting2), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        Uri uri = Uri.fromParts("package", NVPlayerPlayActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);

                    }
                }).show();
            }
        }
    }

    private void SaveRecordFile() {
        String strSDcardPath = Functions.GetSDPath();
        // ????????????SD???
        if (strSDcardPath == null) {
            // ????????????SD???
            Toast.makeText(getApplicationContext(), getString(R.string.noticeSDCardNotExist), Toast.LENGTH_SHORT).show();
            return;
        }
        // sdcard ??????

        // ?????????????????????
        String strSavePath = strSDcardPath + File.separator + LocalDefines.SDCardPath;
        File file = new File(strSavePath);
        if (!file.exists())
            file.mkdir();

        if (mIsPlaying) {
            if (!isRecording) {
                // ??????UI??????
                tBtnImageQl.setText(getString(R.string.str_recording_2)); // ?????????...
                mBtnImageQl.setBackgroundResource(R.drawable.btn_recording_click);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                String dateString = formatter.format(new Date());

                String strFilePath = strSavePath + File.separator + dateString + "(" + m_strName + ")" + ".mp4";
                mNVPanoPlayer.startRecord(strFilePath);
                // TODO: new sdk ????????????
                isRecording = true;
            } else {
                // ??????UI??????
                tBtnImageQl.setText(getString(R.string.str_recording));
                mBtnImageQl.setBackgroundResource(R.drawable.btn_recording);
                Toast.makeText(this, getString(R.string.str_record_ok), Toast.LENGTH_SHORT).show();

                isRecording = false;
                mNVPanoPlayer.stopRecord();
                // TODO: new sdk ????????????
            }

        } else {
            Toast.makeText(this, getString(R.string.str_wait_to_play), Toast.LENGTH_SHORT).show();
        }
        return;

    }

    @SuppressWarnings("deprecation")
    private void ptzContorlPopuwindow(boolean showTooView) {

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popupwindow_ptz_control, null);
        controlPanel = (RelativeLayout) view.findViewById(R.id.ptzCtrlPanel);

        btnPTZLeft = (Button) view.findViewById(R.id.btnPTZLeft);
        btnPTZLeft.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.control_btn_left))); // add
        // by
        // mai
        // 2015-7-9

        // add by xie 2016-9-27
        RelativeLayout.LayoutParams paramsLeft = (RelativeLayout.LayoutParams) btnPTZLeft.getLayoutParams();

        paramsLeft.width = (int) (mScreenWidth * 0.14);
        paramsLeft.height = (int) (mScreenWidth * 0.14);
        // params.setMargins(dip2px(MainActivity.this, 1), 0, 0, 0); //
        // ???????????????????????????????????????????????????????????????
        // params.leftMargin = dip2px(MainActivity.this, 1);
        btnPTZLeft.setLayoutParams(paramsLeft);

        btnPTZLeft.setOnTouchListener(this);

        btnPTZRight = (Button) view.findViewById(R.id.btnPTZRight);
        btnPTZRight.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.control_btn_right))); // add
        // by
        // mai
        // 2015-7-9

        RelativeLayout.LayoutParams paramsRight = (RelativeLayout.LayoutParams) btnPTZRight.getLayoutParams();

        paramsRight.width = (int) (mScreenWidth * 0.14);
        paramsRight.height = (int) (mScreenWidth * 0.14);
        btnPTZRight.setLayoutParams(paramsRight);
        btnPTZRight.setOnTouchListener(this);

        btnPTZUP = (Button) view.findViewById(R.id.btnPTZUP);
        btnPTZUP.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.control_btn_up))); // add
        // by
        // mai
        // 2015-7-9


        RelativeLayout.LayoutParams paramsUP = (RelativeLayout.LayoutParams) btnPTZUP.getLayoutParams();

        paramsUP.width = (int) (mScreenWidth * 0.14);
        paramsUP.height = (int) (mScreenWidth * 0.14);
        btnPTZUP.setLayoutParams(paramsUP);
        btnPTZUP.setOnTouchListener(this);

        btnPTZDown = (Button) view.findViewById(R.id.btnPTZDown);
        btnPTZDown.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.control_btn_down))); // add
        // by
        // mai
        // 2015-7-9

        RelativeLayout.LayoutParams paramsDown = (RelativeLayout.LayoutParams) btnPTZDown.getLayoutParams();

        paramsDown.width = (int) (mScreenWidth * 0.14);
        paramsDown.height = (int) (mScreenWidth * 0.14);
        btnPTZDown.setLayoutParams(paramsDown);
        btnPTZDown.setOnTouchListener(this);

        // add by luo 20150803
        if (showTooView) {

        } else {

        }


        int window = 225;
        int onePreset_dp = 60; //
        int title_dp = 40;

        final float scale = getResources().getDisplayMetrics().density;
        int title_px = (int) (title_dp * scale + 0.5f);
        int onePreset_px = (int) (onePreset_dp * scale + 0.5f);
        float presetHeight = onePreset_px * 6 + title_px; // ????????????????????????????????????
        if (presetHeight > window) { // ??????????????????????????????????????????????????????????????????????????????
            presetHeight = 225;
        }

        int statusBarHeight = LocalDefines.getStatusBarHeight(this);
        if (mScreenHeight < mScreenWidth) {
            popupWindowMore = new PopupWindow(view, mScreenHeight * 4 / 5, LayoutParams.MATCH_PARENT);
        } else {
            popupWindowMore = new PopupWindow(view, LayoutParams.MATCH_PARENT, (int) ((mScreenHeight - statusBarHeight) * 0.4));
        }

        popupWindowMore.setFocusable(true); // ????????????
        popupWindowMore.setOutsideTouchable(true); // ??????????????????????????????
        popupWindowMore.setBackgroundDrawable(new BitmapDrawable()); // ???????????????Back??????????????????????????????????????????
        popupWindowMore.showAtLocation(view, Gravity.BOTTOM | Gravity.LEFT, 0, 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (mScreenWidth), (int) ((mScreenHeight - statusBarHeight) * 0.4));

        controlPanel.setLayoutParams(params);

    }

    public void DialogStopRecord() {
        View view = View.inflate(this, R.layout.show_alert_dialog, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        // tv_title.setText(getString(R.string.alert_stop_play));
        tv_title.setText(R.string.Stop_recording);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setVisibility(View.GONE);
        new AlertDialog.Builder(this)
                //
                .setView(view).setNegativeButton(getString(R.string.alert_btn_NO), null).setPositiveButton(getString(R.string.alert_btn_YES), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                isRecording = false;
                mNVPanoPlayer.stopRecord();
                // TODO: new sdk ????????????
                tBtnImageQl.setText(getString(R.string.str_recording));
                mBtnImageQl.setBackgroundResource(R.drawable.btn_recording);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }

        }).show();
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    private void exitCurrentActivity() {
        if (isRecording == true) {

            DialogStopRecord();
            return;
        }

        if (mIsPlaying == false) {

            stopPlay(false);

            LibContext.ClearContext();
            ReleaseGLViewPlayer();
            m_bFinish = true;

            unRegisterReceiver();
            NVPlayerPlayActivity.this.finish();
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            return;
        }

        View view = View.inflate(this, R.layout.show_alert_dialog, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.alert_stop_play));
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setVisibility(View.GONE);
        new AlertDialog.Builder(this)
                //
                .setView(view).setNegativeButton(getString(R.string.alert_btn_NO), null).setPositiveButton(getString(R.string.alert_btn_YES), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {

                try {
                    stopPlay(false);
                } catch (Exception e) {

                }

                LibContext.ClearContext();
                ReleaseGLViewPlayer();

                setResult(RESULT_OK);

                mIsPlaying = false;
                m_bFinish = true;

                LocalDefines.B_UPDATE_LISTVIEW = true;
                unRegisterReceiver();
                NVPlayerPlayActivity.this.finish();

                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }

        }).show();
    }

    public class SelectAreaAdapter extends BaseAdapter {

        GridView mGv;
        Context context;
        ArrayList<Integer> listDate;

        public SelectAreaAdapter(GridView gv, Context mContext, ArrayList<Integer> list) {
            this.context = mContext;
            this.mGv = gv;
            listDate = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.draggable_grid_item, null);

            if (listDate != null) {

                for (int i = 0; i < listDate.size(); i++) {
                    int isSelect = listDate.get(i);
                    if (position == i) {
                        if (isSelect == 1) {

                            convertView.setBackgroundColor(Color.parseColor("#ff0000"));
                            convertView.getBackground().setAlpha(51);
                        } else {
                            convertView.setBackgroundColor(Color.parseColor("#000000"));
                            convertView.getBackground().setAlpha(20);
                        }
                    }
                }
            }


            AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, mGv.getHeight() / LocalDefines.alarmrows);
            convertView.setLayoutParams(param);

            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listDate.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return listDate.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
        textureView.setSurfaceTextureListener(this);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1, int arg2) {

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {

    }

}
