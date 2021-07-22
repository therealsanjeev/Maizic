package com.maizic.maizic.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import com.maizic.maizic.activities.CameraSetupActivity;
import com.maizic.maizic.adpter.CameraAdapter;
import com.maizic.maizic.roomdatabase.LocalDataBase;
import com.maizic.maizic.roomdatabase.RoomDao;
import com.maizic.maizic.roomdatabase.RoomModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements CameraAdapter.onClicked {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    ArrayList<RoomModel> cameraList;
    RecyclerView cameraRecycleView;
    CameraAdapter cameraAdapter;
    LinearLayout addCameraLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_home, container, false);
        view.findViewById(R.id.addCamera).setOnClickListener(v -> startActivity(new Intent(requireContext(), CameraSetupActivity.class)));
        view.findViewById(R.id.add_camera_frame).setOnClickListener(v -> startActivity(new Intent(requireContext(), CameraSetupActivity.class)));

        addCameraLayout=view.findViewById(R.id.add_camera_layout);
        //listing camera's....
        cameraRecycleView=view.findViewById(R.id.camera_recycler);
        LocalDataBase localDataBase = LocalDataBase.getInstance(requireContext());
        roomDao = localDataBase.roomDao();

        cameraRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));
        //Reading DATA from Room :
        Flowable<List<RoomModel>> listFlowableMyDeals = roomDao.allData();
        Disposable disposable = listFlowableMyDeals.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {


                    cameraAdapter = new CameraAdapter( this,list);
                    cameraRecycleView.setAdapter(cameraAdapter);
                    if(!list.isEmpty()){
                        cameraRecycleView.setVisibility(View.VISIBLE);
                        addCameraLayout.setVisibility(View.GONE);
                    }


                });

        compositeDisposable.add(disposable);
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (deviceInfo != null) {
            LocalDefines.setDeviceInfoToSP(requireActivity(), deviceInfo);
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


        int loginResult = LoginHelper.loginDevice(requireActivity(),loginParam, loginHandle -> {
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

    @Override
    public void onItemClicked(int position, List<RoomModel> list) {
        String deviceID = list.get(position).getDeviceID();
        String deviceUser = list.get(position).getDeviceName();
        String devicePwd = list.get(position).getDevicePass();
        deviceInfo = new DeviceInfo(-1, Integer.parseInt(deviceID), deviceID,
                "192.168.1.1", 8800, deviceUser, devicePwd, "ABC", deviceID + ".nvdvr.net",
                Defines.SERVER_SAVE_TYPE_ADD);
        loginDevice();
        Toast.makeText(requireContext(), "Camera "+list.get(position).getDeviceID(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemDelete(int position, List<RoomModel> list) {

        MaterialAlertDialogBuilder builder= new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Delete Camera");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteDevice(list.get(position));
            Toast.makeText(requireContext(), "Camera "+list.get(position).getDeviceID() + " has been Deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);
        builder.show();


    }

    public void deleteDevice(RoomModel roomModel) {

//        isLoading.setValue(true);
        Completable.fromAction(() -> roomDao.delete(roomModel)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("TAG", "onSubscribe: Called");
                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "onComplete: Called");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", "onError: Called" + e.getMessage());
                    }
                });
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


                switch (msg.arg2) {
                    case ResultCode.RESULT_CODE_SUCCESS: {

                        Bundle bundle = msg.getData();

                        LoginHandle loginHandle = bundle.getParcelable("device_param");
                        LocalDefines.Device_LoginHandle = loginHandle;
                        int camType = loginHandle.getCamType();

                        if (camType == LocalDefines.CAMTYPE_WALL
                                || camType == LocalDefines.CAMTYPE_CEIL) {
                            Intent intent = new Intent(requireContext(), NVPlayerPlayFishEyeActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            //MainActivity.this.finish();
                        } else {

                            Intent intent = new Intent(requireActivity(), NVPlayerPlayActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            //MainActivity.this.finish();
                        }
                        break;
                    }
                    case ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL: {

                    }
                    break;
                    case ResultCode.RESULT_CODE_FAIL_VERIFY_FAILED: {

                    }
                    break;
                    case ResultCode.RESULT_CODE_FAIL_USER_NOEXIST: {
                        progress.setVisibility(View.GONE);

                    }
                    break;
                    case ResultCode.RESULT_CODE_FAIL_PWD_ERROR: {

                    }
                    break;
                    case ResultCode.RESULT_CODE_FAIL_OLD_VERSON: {

                    }
                    break;
                    default:
                        break;

                }
            }
        }
    };




}