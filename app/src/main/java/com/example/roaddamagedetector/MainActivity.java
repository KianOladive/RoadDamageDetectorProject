package com.example.roaddamagedetector;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewById
    TextView tvLogin;
    @ViewById
    TextView tvUsername;
    @ViewById
    TextView tvPassword;

    @ViewById
    EditText etUsername;
    @ViewById
    EditText etPassword;

    @ViewById
    CheckBox chkbxRememberMe;

    @ViewById
    Button btnSignIn;
    @ViewById
    Button btnAdmin;
    @ViewById
    Button btnClear;

    Realm realm;
    SharedPreferences prefs;

    @AfterViews
    public void checkPermissions()
    {

        // REQUEST PERMISSIONS for Android 6+
        // THESE PERMISSIONS SHOULD MATCH THE ONES IN THE MANIFEST
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA

                )

                .withListener(new BaseMultiplePermissionsListener()
                {
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        if (report.areAllPermissionsGranted())
                        {
                            // all permissions accepted proceed
                            init();
                        }
                        else
                        {
                            // notify about permissions
                            toastRequirePermissions();
                        }
                    }
                })
                .check();

    }


    public void toastRequirePermissions()
    {
        Toast.makeText(this, "You must provide permissions for app to run", Toast.LENGTH_LONG).show();
        finish();
    }


    public void init() {

        realm = Realm.getInstance(RealmUtility.getDefaultConfig());

        RealmResults<User> result = realm.where(User.class).findAll();

        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
    }

    @Click(R.id.btnSignIn)
    public void assessLogin() {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("isRemembered", chkbxRememberMe.isChecked());
        edit.apply();

        User result = realm.where(User.class)
                .equalTo("name", etUsername.getText().toString())
                .findFirst();

        if (result != null) {
            if (result.getPassword().equals(etPassword.getText().toString())) {
                edit.putString("uuid", result.getUuid());
                edit.apply();
                openLoggedInScreen();
            }
            else {
                showToast("Invalid Credentials");
            }
        }
        else {
            showToast("No User found");
        }
    }

    @Click(R.id.btnAdmin)
    public void goToRegister() {
        AdminActivity_.intent(this).start();
    }

    @Click(R.id.btnClear)
    public void cancel() {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("isRemembered", false);
        edit.putString("uuid", null);
        edit.apply();
        showToast("Cleared");
    }

    public void openLoggedInScreen() {
        LoggedInActivity_.intent(this).start();
    }

    public void showToast(String str) {
        Toast t  = Toast.makeText(this, str, Toast.LENGTH_LONG);
        t.show();
    }

}