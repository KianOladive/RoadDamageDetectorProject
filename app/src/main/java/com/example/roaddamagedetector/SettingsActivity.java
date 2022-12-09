package com.example.roaddamagedetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends AppCompatActivity {

    @ViewById
    BottomNavigationView bottom_navigation;

    @AfterViews
    public void init(){
        bottom_navigation.getMenu().getItem(2).setChecked(true);
        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                Activity activity = null;
                Intent myIntent;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        myIntent = new Intent(SettingsActivity.this, LoggedInActivity_.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(myIntent);
                        break;
                    case R.id.nav_dash:
                        myIntent = new Intent(SettingsActivity.this, DashboardActivity_.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(myIntent);
                        break;
                }
                return true;
            }
        });

    }
}