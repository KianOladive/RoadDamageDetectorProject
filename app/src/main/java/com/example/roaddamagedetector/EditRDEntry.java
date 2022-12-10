package com.example.roaddamagedetector;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_edit_rdentry)
public class EditRDEntry extends AppCompatActivity {

    @ViewById
    TextView tvEntryIdContentEdit;

    @ViewById
    EditText etLocationEdit;
    @ViewById
    EditText etLongitudeEdit;
    @ViewById
    EditText etLatitudeEdit;
    @ViewById
    EditText etDamageTypeEdit;

    @ViewById
    ImageView imgVwEditRD;
    @ViewById
    ImageView imgVwUserEdit;

    @ViewById
    Button btnAttachPhotoEdit;

    @ViewById
    Button btnEditEdit;
    @ViewById
    Button btnCancelEdit;

    Realm realm;
    SharedPreferences prefs;

    String date;

    String uuid;
    String uuidUser;
    int entryId;
    String userName;
    String path;

    boolean latAndLongAreValid;

    @AfterViews
    public void init() {

        realm = Realm.getInstance(RealmUtility.getDefaultConfig());

        RealmResults<RoadDamage> result = realm.where(RoadDamage.class).findAll();
        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

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
                    .into(imgVwUserEdit);
        }

        String uuidBeingViewed = prefs.getString("uuidRD", null);
        uuid = uuidBeingViewed;
        RoadDamage currentlyEditing = realm.where(RoadDamage.class)
                .equalTo("uuid", uuidBeingViewed)
                .findFirst();

        etDamageTypeEdit.setText(currentlyEditing.getDamageType());
        etLatitudeEdit.setText(String.valueOf(currentlyEditing.getLatitude()));
        etLongitudeEdit.setText(String.valueOf(currentlyEditing.getLongitude()));
        etLocationEdit.setText(currentlyEditing.getLocation());
        tvEntryIdContentEdit.setText(String.valueOf(currentlyEditing.getEntryId()));
        userName = currentlyEditing.getUserName();
        path = currentlyEditing.getPath();
        entryId = currentlyEditing.getEntryId();

        // get date information
        Date thisDate = new Date();
        SimpleDateFormat formattedDate = new SimpleDateFormat("MM-dd-Y hh:mm a");
        date = formattedDate.format(thisDate);

        if (!(currentlyEditing.getPath().equals(""))) {
            btnAttachPhotoEdit.setVisibility(View.GONE);
            File imageOfCurrentlyEditing = new File(getExternalCacheDir(), currentlyEditing.getPath());
            Picasso.get()
                    .load(imageOfCurrentlyEditing)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imgVwEditRD);
        }
    }

    @Click(R.id.btnEditEdit)
    public void saveOnEdit() {

        // check if all fields have value
        if (etDamageTypeEdit.getText().toString().equals("") ||
                etLatitudeEdit.getText().toString().equals("") ||
                etLongitudeEdit.getText().toString().equals("") ||
                etLocationEdit.getText().toString().equals("")
        ) {
            showToast("Text fields must not be blank.");
        }
        else {
            // check if latitude and longitude values are parseable to Double

            try {
                Double.parseDouble(etLatitudeEdit.getText().toString());
                Double.parseDouble(etLongitudeEdit.getText().toString());
                latAndLongAreValid = true;
            } catch(NumberFormatException e){
                latAndLongAreValid = false;
            }

            if (latAndLongAreValid) {
                String damageType = etDamageTypeEdit.getText().toString();
                Double latitude = Double.parseDouble(etLatitudeEdit.getText().toString());
                Double longitude = Double.parseDouble(etLongitudeEdit.getText().toString());
                String location = etLocationEdit.getText().toString();

                RoadDamage newRoadDamage =  new RoadDamage(uuid, entryId, damageType, latitude, longitude, userName, location, date, path);

                try {
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(newRoadDamage);  // save
                    realm.commitTransaction();

                    showToast("Road damage entry updated.");
                    ViewRDEntry_.intent(this).start();

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

    @Click(R.id.btnCancelEdit)
    public void cancel() {
        ViewRDEntry_.intent(this).start();
    }


    @Click(R.id.imgVwUserEdit)
    public void goToUserSettings() {
        EditActivity_.intent(this).start();
    }


    //===================================================== IMAGE STUFF
    public static int REQUEST_CODE_IMAGE_SCREEN = 0;

    @Click(R.id.imgVwEditRD)
    public void selectPic()
    {
        ImageActivity_.intent(this).startForResult(REQUEST_CODE_IMAGE_SCREEN);
    }

    @Click(R.id.btnAttachPhotoEdit)
    public void selectPicFromButton() {
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
        btnAttachPhotoEdit.setVisibility(View.GONE);
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imgVwEditRD);
    }

    public void showToast(String str) {
        Toast t  = Toast.makeText(this, str, Toast.LENGTH_LONG);
        t.show();
    }

    public void onDestroy()
    {
        super.onDestroy();
        realm.close();
    }
}