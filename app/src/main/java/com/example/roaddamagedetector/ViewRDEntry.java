package com.example.roaddamagedetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;

@EActivity(R.layout.activity_view_rdentry)
public class ViewRDEntry extends AppCompatActivity {

    @ViewById
    TextView tvEntryIdViewContent;
    @ViewById
    TextView tvDamageTypeContentView;
    @ViewById
    TextView tvLatitudeContentView;
    @ViewById
    TextView tvLongitudeContentView;
    @ViewById
    TextView tvLocationContentView;
    @ViewById
    TextView tvCapturedByView;
    @ViewById
    TextView tvDateContentView;

    @ViewById
    Button btnEditView;
    @ViewById
    Button btnBackView;

    @ViewById
    ImageView imgVwUserView;
    @ViewById
    ImageView imgVwViewPic;

    SharedPreferences prefs;
    Realm realm;
    String uuidUser;
    String uuidRD;
    User loggedInUser;
    RoadDamage currentRD;

    @AfterViews
    public void init() {
        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        realm = Realm.getInstance(RealmUtility.getDefaultConfig());


        // gets pic from logged in user and sets it
        uuidUser = prefs.getString("uuidUser", null);
        loggedInUser = realm.where(User.class)
                .equalTo("uuid", uuidUser)
                .findFirst();
        if (!(loggedInUser.getPath().equals(""))) {
            File imageOfLoggedInUser = new File(getExternalCacheDir(), loggedInUser.getPath());
            Picasso.get()
                    .load(imageOfLoggedInUser)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imgVwUserView);
        }

        uuidRD = prefs.getString("uuidRD", null);
        currentRD = realm.where(RoadDamage.class)
                .equalTo("uuid", uuidRD)
                .findFirst();

        tvEntryIdViewContent.setText(String.valueOf(currentRD.getEntryId()));
        tvDamageTypeContentView.setText(currentRD.getDamageType());
        tvLatitudeContentView.setText(String.valueOf(currentRD.getLatitude()));
        tvLongitudeContentView.setText(String.valueOf(currentRD.getLongitude()));
        tvLocationContentView.setText(currentRD.getLocation());
        tvCapturedByView.setText(currentRD.getUserName());
        tvDateContentView.setText(currentRD.getDate());


        //===================================================== IMAGE STUFF
        File imageOfCurrentRD = new File(getExternalCacheDir(), currentRD.getPath());
        Picasso.get()
                .load(imageOfCurrentRD)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imgVwViewPic);

        if (prefs.getString("lastRecyclerView", null).equals("allrecords")) {
            btnEditView.setVisibility(View.GONE);
            btnBackView.setVisibility(View.GONE);
        }
    }


    @Click(R.id.btnEditView)
    public void editFromView() {
        EditRDEntry_.intent(this).start();
    }

    @Click(R.id.btnBackView)
    public void backFromView() {
        LoggedInActivity_.intent(this).start();
    }

    @Click(R.id.imgVwUserView)
    public void goToSettingsFromView() {
        EditActivity_.intent(this).start();
    }

    public void onDestroy()
    {
        super.onDestroy();
        realm.close();
    }
}