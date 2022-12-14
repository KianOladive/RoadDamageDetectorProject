package com.example.roaddamagedetector;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

@EActivity(R.layout.activity_logged_in)
public class LoggedInActivity extends AppCompatActivity {

    @ViewById(R.id.btnAddRoadDamage)
    Button btnAddRoadDamage;

    @ViewById
    TextView tvHiUser;

    @ViewById
    ImageView imgVwUserLoggedIn;

    @ViewById
    RecyclerView recyclerMyRecords;

    @ViewById
    BottomNavigationView bottom_navigation;

    SharedPreferences prefs;
    Realm realm;

    String uuidUser;

    @AfterViews
    public void init() {
        // initialize RecyclerView
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerMyRecords.setLayoutManager(mLayoutManager);

        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        realm = Realm.getInstance(RealmUtility.getDefaultConfig());

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
                    .into(imgVwUserLoggedIn);
        }


        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("lastRecyclerView", "login");
        edit.apply();

        // query the things to display
        RealmResults<RoadDamage> list = realm.where(RoadDamage.class).equalTo("userName", loggedInUser.getName()).findAll();

        // initialize Adapter
        RDAdapterMyRecords adapter = new RDAdapterMyRecords(this, list, true);
        recyclerMyRecords.setAdapter(adapter);

        tvHiUser.setText("Hi, " + loggedInUser.getName() + "!");


//        String uuid = prefs.getString("uuid", null);
//        User result = realm.where(User.class)
//                .equalTo("uuid", uuid)
//                .findFirst();
//
//        Boolean rememberedBool = prefs.getBoolean("isRemembered", false);
//
//        assert result != null;
//        String outputText = "Welcome " + result.getName() + "!!!";
//
//        if (rememberedBool) { outputText += " You will be remembered"; }
//
//        if (!(result.getPath().equals(""))) {
//            File imageOfLoggedInUser = new File(getExternalCacheDir(), result.getPath());
//            Picasso.get()
//                    .load(imageOfLoggedInUser)
//                    .networkPolicy(NetworkPolicy.NO_CACHE)
//                    .memoryPolicy(MemoryPolicy.NO_CACHE)
//                    .into(imageViewOnLogIn);
//        }

//        tvOutput.setText(outputText);

        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                Activity activity = null;
                Intent myIntent;
                switch (item.getItemId()){

                    case R.id.nav_dash:
                        myIntent = new Intent(LoggedInActivity.this, DashboardActivity_.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(myIntent);
//                        DashboardActivity_.intent(MainActivity.this).start();
//                        finish();
                        break;
                    case R.id.nav_settings:
                        myIntent = new Intent(LoggedInActivity.this, SettingsActivity_.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(myIntent);
//                        SettingsActivity_.intent(MainActivity.this).start();
//                        finish();
                        break;
                }
                return true;
            }
        });
    }


    @Click(R.id.imgVwUserLoggedIn)
    public void goToUserSettings() {
        EditActivity_.intent(this).start();
    }

    @Click(R.id.btnAddRoadDamage)
    public void addRoadDamage() {
        AddRDActivity_.intent(this).start();
    }

    public void goToUserSettings(RoadDamage rd) {
        //shared prefs
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("uuidRD", rd.getUuid());
        edit.apply();

        // intent
        ViewRDEntry_.intent(this).start();
    }
    public void onDestroy()
    {
        super.onDestroy();
        realm.close();
    }
}