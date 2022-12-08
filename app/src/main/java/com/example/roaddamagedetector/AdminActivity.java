package com.example.roaddamagedetector;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_admin)
public class AdminActivity extends AppCompatActivity {

    @ViewById
    RecyclerView recyclerViewUsers;

    @ViewById
    Button btnClearOnAdmin;

    @ViewById
    Button btnAddOnAdmin;

    Realm realm;
    SharedPreferences prefs;

    @AfterViews
    public void init()
    {
        // initialize RecyclerView
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewUsers.setLayoutManager(mLayoutManager);

        // initialize Realm
        realm = Realm.getDefaultInstance();

        // query the things to display
        RealmResults<User> list = realm.where(User.class).findAll();

        // initialize Adapter
        UserAdapter adapter = new UserAdapter(this, list, true);
        recyclerViewUsers.setAdapter(adapter);

        // initialize sharedprefs
        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
    }

    @Click(R.id.btnAddOnAdmin)
    public void goToRegister() {
        RegisterActivity_.intent(this).start();
    }

    @Click(R.id.btnClearOnAdmin)
    public void deleteAllUsers() {
        RealmResults<User> list = realm.where(User.class).findAll();

        while(list.size()>0) {
            User userToDelete = list.get(0);
            if (userToDelete.isValid())
            {
                realm.beginTransaction();
                userToDelete.deleteFromRealm();
                realm.commitTransaction();
            }
        }
        showToast("Cleared");
    }

    public void delete(User c)
    {
        // need to check if previously deleted
        if (c.isValid())
        {

            File toDelete = new File(getCacheDir(), c.getPath());
            if (toDelete.exists()) {
                toDelete.delete();
            }
            realm.beginTransaction();
            c.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    public void edit(User c)
    {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("uuidBeingViewed", c.getUuid());
        edit.apply();

        EditActivity_.intent(this).start();
    }

    public void showToast(String str) {
        Toast t  = Toast.makeText(this, str, Toast.LENGTH_LONG);
        t.show();
    }

    public void onDestroy()
    {
        super.onDestroy();
        if (!realm.isClosed())
        {
            realm.close();
        }
    }
}