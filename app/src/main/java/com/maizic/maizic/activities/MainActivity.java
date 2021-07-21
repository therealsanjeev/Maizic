package com.maizic.maizic.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.maizic.maizic.R;
import com.maizic.maizic.fragments.MaizicFragment;
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
                fragment = new MaizicFragment();
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