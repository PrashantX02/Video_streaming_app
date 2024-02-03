package com.example.newsvid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class MainScreen extends AppCompatActivity {
    FirebaseAuth auth;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        auth = FirebaseAuth.getInstance();

        TabLayout tabLayout = findViewById(R.id.tab_layout);


        viewPager = findViewById(R.id.ViewPager);

        tabLayout.setupWithViewPager( viewPager);
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.addFragment(new frag_users());
        adapter.addFragment(new frag_vidCreate());
        adapter.addFragment(new frag_video());
        adapter.addFragment(new frag_profile());
        viewPager.setAdapter(adapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_list_alt_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_video_camera_back_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_ondemand_video_24);
        tabLayout.getTabAt(3).setIcon(R.drawable.baseline_manage_accounts_24);


        if(auth.getCurrentUser() == null){
            Intent intent  = new Intent(getApplicationContext(),LogIn.class);
            startActivity(intent);
            finish();
        }
    }
}