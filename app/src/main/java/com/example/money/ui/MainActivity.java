package com.example.money.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.money.Models.DataBaseHelper;
import com.example.money.Models.MoneyController;
import com.example.money.R;
import com.example.money.databinding.ActivityMainBinding;
import com.example.money.ui.aboutMe.AboutMeFragment;
import com.example.money.ui.adding.AddingFragment;
import com.example.money.ui.history.HistoryFragment;
import com.example.money.ui.home.HomeFragment;
import com.example.money.ui.settings.SettingsFragment;
import com.example.money.ui.statistics.StatisticsFragment;
import com.example.money.ui.why.WhyFragment;
import com.shreyaspatil.material.navigationview.MaterialNavigationView;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    //region params
    private AppBarConfiguration mAppBarConfiguration;
    private int lastSelectedId;
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //region SupportClasses
        DataBaseHelper dbHelper = new DataBaseHelper(getBaseContext());
        MoneyController.getInstance().setDatabaseHelper(dbHelper);
        //endregion
        //region NavigationDrawer
        com.example.money.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        MaterialNavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder()
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id != lastSelectedId) {
                lastSelectedId = id;
                Class<?> target;
                if (R.id.nav_home == id) {
                    target = HomeFragment.class;
                } else if (R.id.nav_adding == id) {
                    target = AddingFragment.class;
                } else if (R.id.nav_history == id) {
                    target = HistoryFragment.class;
                } else if (R.id.nav_settings == id) {
                    target = SettingsFragment.class;
                } else if (R.id.nav_statistics == id) {
                    target = StatisticsFragment.class;
                } else if (R.id.nav_why == id) {
                    target = WhyFragment.class;
                } else if (R.id.nav_about_me == id) {
                    target = AboutMeFragment.class;
                } else {
                    return false;
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                try {
                    fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, (Fragment) target.newInstance()).commit();
                    Objects.requireNonNull(getSupportActionBar()).setTitle(item.getTitle());
                } catch (Throwable ignored) {
                }
            }
            onBackPressed();
            return true;
        });
        navigationView.setCheckedItem(R.id.nav_home);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, (Fragment) HomeFragment.newInstance()).commit();
        //endregion
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() % 2 == 0) {
            return true;
        }
        else return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            this.finish();
        }
    }

}