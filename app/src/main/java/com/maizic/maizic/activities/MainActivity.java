package com.maizic.maizic.activities;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.macrovideo.sdk.defines.Defines;
import com.macrovideo.sdk.defines.ResultCode;
import com.macrovideo.sdk.media.ILoginDeviceCallback;
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
import com.maizic.maizic.fragments.GrouSaleFragment;
import com.maizic.maizic.fragments.HomeFragment;
import com.maizic.maizic.fragments.MoreFragment;
import com.maizic.maizic.fragments.PlaybackFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {




//    ActivityMainBinding binding;
    private BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);
        //loading the default fragment
        loadFragment(new HomeFragment());
        navigation =  findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(this);




    }


    @Override
    public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.homeFragment:
                fragment = new HomeFragment();
                break;

            case R.id.playbackFragment:
                fragment = new PlaybackFragment();
                break;

            case R.id.grousaleFragment:
                fragment = new GrouSaleFragment();
                break;

            case R.id.moreFragment:
                fragment = new MoreFragment();
                break;
        }

        return loadFragment(fragment);
    }
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


}