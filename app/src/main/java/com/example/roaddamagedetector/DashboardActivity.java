package com.example.roaddamagedetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_dashboard)
public class DashboardActivity extends AppCompatActivity {

    @ViewById
    ImageView imgVwUserDashboard;

    @ViewById
    TextView tvTotalEntriesContent;

    @ViewById
    TextView tvMostCommonDamageTypeContent;
    @ViewById
    TextView tvDamageTypeEntries;

    @ViewById
    TextView tvLocationWithMostEntriesContent;
    @ViewById
    TextView tvLocationMostEntriesNum;

    @ViewById
    TextView tvMostCommonUserContent;
    @ViewById
    TextView tvMostCommonUserNum;

    @ViewById
    Button btnViewAllRecords;

    @ViewById
    BottomNavigationView bottom_navigation;

    SharedPreferences prefs;
    Realm realm;

    String uuidUser;



    @AfterViews
    public void init(){
        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        realm = Realm.getInstance(RealmUtility.getDefaultConfig());


        // gets pic from logged in user and sets it
        uuidUser = prefs.getString("uuidUser", null);
        User loggedInUser = realm.where(User.class)
                .equalTo("uuid", uuidUser)
                .findFirst();
        if (!(loggedInUser.getPath().equals(""))) {
            File imageOfLoggedInUser = new File(getExternalCacheDir(), loggedInUser.getPath());
            Picasso.get()
                    .load(imageOfLoggedInUser)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imgVwUserDashboard);
        }

        // set total entries created
        RealmResults<RoadDamage> listOfRDEntries = realm.where(RoadDamage.class).findAll();
        String numOfRDEntries = String.valueOf(listOfRDEntries.size());
        tvTotalEntriesContent.setText(numOfRDEntries);

        // set most common damage type
        HashMap<String, Integer> commonDamageType = new HashMap<String, Integer>();
        for (RoadDamage rd : listOfRDEntries) {
            if (commonDamageType.containsKey(rd.getDamageType())) {
                commonDamageType.put(rd.getDamageType(), commonDamageType.get(rd.getDamageType()) + 1);
            } else {
                commonDamageType.put(rd.getDamageType(), 1);
            }
        }

        Integer numOfMostCommonDamageType = 0;
        String mostCommonDamageType = "";

        for (Map.Entry<String, Integer> set : commonDamageType.entrySet()) {
            if (set.getValue() > numOfMostCommonDamageType) {
                mostCommonDamageType = set.getKey();
                numOfMostCommonDamageType = set.getValue();
            }
        }

        tvMostCommonDamageTypeContent.setText(mostCommonDamageType);
        tvDamageTypeEntries.setText(String.valueOf(numOfMostCommonDamageType));


        // set most common location
        HashMap<String, Integer> commonLocation = new HashMap<String, Integer>();
        for (RoadDamage rd : listOfRDEntries) {
            if (commonLocation.containsKey(rd.getLocation())) {
                commonLocation.put(rd.getLocation(), commonLocation.get(rd.getLocation()) + 1);
            } else {
                commonLocation.put(rd.getLocation(), 1);
            }
        }

        Integer numOfMostLocation = 0;
        String mostCommonLocation = "";

        for (Map.Entry<String, Integer> set1 : commonLocation.entrySet()) {
            if (set1.getValue() > numOfMostLocation) {
                mostCommonLocation = set1.getKey();
                numOfMostLocation = set1.getValue();
            }
        }

        tvLocationWithMostEntriesContent.setText(mostCommonLocation);
        tvLocationMostEntriesNum.setText(String.valueOf(numOfMostLocation));


        // set most common user
        HashMap<String, Integer> commonUser = new HashMap<String, Integer>();
        for (RoadDamage rd : listOfRDEntries) {
            if (commonUser.containsKey(rd.getUserName())) {
                commonUser.put(rd.getUserName(), commonUser.get(rd.getUserName()) + 1);
            } else {
                commonUser.put(rd.getUserName(), 1);
            }
        }

        Integer numOfMostCommonUserName = 0;
        String mostCommonUserName = "";

        for (Map.Entry<String, Integer> set : commonUser.entrySet()) {
            if (set.getValue() > numOfMostCommonUserName) {
                mostCommonUserName = set.getKey();
                numOfMostCommonUserName = set.getValue();
            }
        }

        tvMostCommonUserContent.setText(mostCommonUserName);
        tvMostCommonUserNum.setText(String.valueOf(numOfMostCommonUserName));



        bottom_navigation.getMenu().getItem(1).setChecked(true);
        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                Activity activity = null;
                Intent myIntent;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        myIntent = new Intent(DashboardActivity.this, LoggedInActivity_.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(myIntent);
                        break;
                    case R.id.nav_settings:
                        myIntent = new Intent(DashboardActivity.this, SettingsActivity_.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(myIntent);
                        break;
                }
                return true;
            }
        });

    }


}