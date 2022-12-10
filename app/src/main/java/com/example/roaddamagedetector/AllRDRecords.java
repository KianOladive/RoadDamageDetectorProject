package com.example.roaddamagedetector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_all_rdrecords)
public class AllRDRecords extends AppCompatActivity {

    @ViewById
    RecyclerView recyclerVwAllRecords;

    @ViewById
    ImageView imgVwUserAllRecords;

    SharedPreferences prefs;
    Realm realm;

    String uuidUser;

    @AfterViews
    public void init() {
        // initialize RecyclerView
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerVwAllRecords.setLayoutManager(mLayoutManager);

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
                    .into(imgVwUserAllRecords);
        }

        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("lastRecyclerView", "allrecords");
        edit.apply();

        // query the things to display
        RealmResults<RoadDamage> list = realm.where(RoadDamage.class).findAll();

        // initialize Adapter
        RDAdapterAllRecords adapter = new RDAdapterAllRecords(this, list, true);
        recyclerVwAllRecords.setAdapter(adapter);
    }

    public void goToUserSettings(RoadDamage rd) {
        //shared prefs
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("uuidRD", rd.getUuid());
        edit.apply();

        // intent
        ViewRDEntry_.intent(this).start();
    }

    @Click(R.id.imgVwUserAllRecords)
    public void goToEditFromAllRecords() {
        EditActivity_.intent(this).start();
    }

    public void onDestroy()
    {
        super.onDestroy();
        realm.close();
    }
}