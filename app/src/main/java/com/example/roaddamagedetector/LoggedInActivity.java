package com.example.roaddamagedetector;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


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
import io.realm.RealmResults;

@EActivity(R.layout.activity_logged_in)
public class LoggedInActivity extends AppCompatActivity {

    @ViewById(R.id.btnAddRoadDamage)
    Button btnAddRoadDamage;

    SharedPreferences prefs;
    Realm realm;

    @AfterViews
    public void init() {
        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        realm = Realm.getInstance(RealmUtility.getDefaultConfig());

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


    }

    @Click(R.id.btnAddRoadDamage)
    public void addRoadDamage() {
        AddRDActivity_.intent(this).start();
    }


}