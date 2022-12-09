package com.example.roaddamagedetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends AppCompatActivity {

    @ViewById
    Button btnClearRecords;

    @ViewById
    BottomNavigationView bottom_navigation;

    SharedPreferences prefs;
    Realm realm;

    String uuidUser;
    User loggedInUser;

    @AfterViews
    public void init(){
        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        realm = Realm.getInstance(RealmUtility.getDefaultConfig());

        uuidUser = prefs.getString("uuidUser", null);
        loggedInUser = realm.where(User.class)
                .equalTo("uuid", uuidUser)
                .findFirst();

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

    @Click(R.id.btnClearRecords)
    public void clearMyRecords() {

        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete all your entries?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RealmResults<RoadDamage> listOfRDEntries = realm.where(RoadDamage.class).findAll();
                        int count = 0;
                        for (RoadDamage rd : listOfRDEntries) {
                            if (rd.getUserName().equals(loggedInUser.getName())) {
                                realm.beginTransaction();
                                rd.deleteFromRealm();
                                realm.commitTransaction();
                                count++;
                            }
                        }
                        showToast("Removed " + String.valueOf(count) + " entries.");
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    public void showToast(String str) {
        Toast t  = Toast.makeText(this, str, Toast.LENGTH_LONG);
        t.show();
    }
}