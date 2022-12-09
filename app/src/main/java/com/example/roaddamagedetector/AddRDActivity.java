package com.example.roaddamagedetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

import java.text.SimpleDateFormat;
import java.util.Date;

@EActivity(R.layout.activity_add_rdactivity)
public class AddRDActivity extends AppCompatActivity {

    @ViewById
    TextView tvEntryIdContent;

    @ViewById
    EditText etDamageType;
    @ViewById
    EditText etLatitude;
    @ViewById
    EditText etLongitude;
    @ViewById
    EditText etLocation;

    @ViewById
    Button btnAddRDPhoto;
    @ViewById
    Button btnAddRDSave;
    @ViewById
    Button btnAddRDCancel;

    @ViewById
    ImageView imgVwAddRD;

    @ViewById
    BottomNavigationView bottom_navigation;

    Realm realm;
    boolean latAndLongAreValid;
    boolean photoExists;
    int entryId;
    String userName;
    String date;
    String path = "";


    @AfterViews
    public void init() {

        realm = Realm.getInstance(RealmUtility.getDefaultConfig());

        RealmResults<User> result = realm.where(User.class).findAll();

        // get name from sharedprefs
        entryId = getNextKey();
        tvEntryIdContent.setText(String.valueOf(entryId));
        Date thisDate = new Date();
        SimpleDateFormat formattedDate = new SimpleDateFormat("MMMM dd, Y | hh:mm a");
        date = formattedDate.format(thisDate);

        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                Activity activity = null;
                Intent myIntent;
                switch (item.getItemId()){

                    case R.id.nav_dash:
                        myIntent = new Intent(AddRDActivity.this, DashboardActivity_.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(myIntent);
//                        DashboardActivity_.intent(MainActivity.this).start();
//                        finish();
                        break;
                    case R.id.nav_settings:
                        myIntent = new Intent(AddRDActivity.this, SettingsActivity_.class);
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

    @Click(R.id.btnAddRDSave)
    public void saveRDEntry() {

        // check if all fields have value
        if (etDamageType.getText().toString().equals("") ||
                etLatitude.getText().toString().equals("") ||
                etLongitude.getText().toString().equals("") ||
                etLocation.getText().toString().equals("")
        ) {
            showToast("Text fields must not be blank.");
        }
        else {
            // check if latitude and longitude values are parseable to Double

            try {
                Double.parseDouble(etLatitude.getText().toString());
                Double.parseDouble(etLongitude.getText().toString());
                latAndLongAreValid = true;
            } catch(NumberFormatException e){
                latAndLongAreValid = false;
            }

            if (latAndLongAreValid) {
                String uuid = UUID.randomUUID().toString();
                String damageType = etDamageType.getText().toString();
                Double latitude = Double.parseDouble(etLatitude.getText().toString());
                Double longitude = Double.parseDouble(etLongitude.getText().toString());
                String location = etLocation.getText().toString();

                RoadDamage newRoadDamage =  new RoadDamage(uuid, entryId, damageType, latitude, longitude, userName, location, date, path);

                long count = 0;

                try {
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(newRoadDamage);  // save
                    realm.commitTransaction();

                    showToast("New road damage entry saved.");
                    finish();
                }
                catch(Exception e)
                {
                    showToast("Error saving.");
                }
            }
            else {
                showToast("Please enter valid latitude and longitude values.");
            }
        }
    }

    @Click(R.id.btnAddRDCancel)
    public void cancel() {
        finish();
    }

    public void showToast(String str) {
        Toast t  = Toast.makeText(this, str, Toast.LENGTH_LONG);
        t.show();
    }


    public static int REQUEST_CODE_IMAGE_SCREEN = 0;

    @Click(R.id.btnAddRDPhoto)
    public void selectPic()
    {
        ImageActivity_.intent(this).startForResult(REQUEST_CODE_IMAGE_SCREEN);
    }

    @Click(R.id.imgVwAddRD)
    public void selectPicFromImgVw()
    {
        ImageActivity_.intent(this).startForResult(REQUEST_CODE_IMAGE_SCREEN);
    }


    // SINCE WE USE startForResult(), code will trigger this once the next screen calls finish()
    public void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode==REQUEST_CODE_IMAGE_SCREEN)
        {
            if (responseCode==ImageActivity.RESULT_CODE_IMAGE_TAKEN)
            {
                // receive the raw JPEG data from ImageActivity
                // this can be saved to a file or save elsewhere like Realm or online
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try {
                    // save rawImage to file
                    path = System.currentTimeMillis()+".jpeg";
                    File savedImage = saveFile(jpeg, path);

                    // load file to the image view via picasso
                    refreshImageView(savedImage);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    private File saveFile(byte[] jpeg, String name) throws IOException
    {
        // this is the root directory for the images
        File getImageDir = getExternalCacheDir();

        // just a sample, normally you have a diff image name each time
        File savedImage = new File(getImageDir, name);

        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }



    private void refreshImageView(File savedImage) {
        // this will put the image saved to the file system to the imageview
        btnAddRDPhoto.setVisibility(View.GONE);
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imgVwAddRD);
    }

    public int getNextKey() {
        try {
            Number number = realm.where(RoadDamage.class).max("entryId");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 18172;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 18172;
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        realm.close();
    }
}