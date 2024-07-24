package com.example.group2_final_project.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.group2_final_project.R;
import com.example.group2_final_project.databinding.ActivityDashboardBinding;
import com.example.group2_final_project.user.ui.about.AboutFragment;
import com.example.group2_final_project.user.ui.dashboard.DashboardFragment;
import com.example.group2_final_project.user.ui.profile.ProfileFragment;
import com.example.group2_final_project.user.ui.setting.SettingFragment;
import com.google.android.material.navigation.NavigationBarView;
public class Dashboard extends AppCompatActivity {

    private ActivityDashboardBinding dashboardBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        View view= dashboardBinding.getRoot();
        setContentView(view);

        loadFragment(new DashboardFragment());
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        dashboardBinding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    loadFragment(new DashboardFragment());

                }
                if( itemId==R.id.navigation_about){
                    loadFragment(new AboutFragment());
                }
                if(itemId==R.id.navigation_profile){
                    loadFragment(new ProfileFragment());
                }
                if(itemId==R.id.navigation_settings){
                    loadFragment(new SettingFragment());
                }
                return true;
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

    }
}