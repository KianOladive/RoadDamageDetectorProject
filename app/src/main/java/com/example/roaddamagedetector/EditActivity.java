package com.example.roaddamagedetector;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_edit)
public class EditActivity extends AppCompatActivity {

    @ViewById
    EditText etUserNameOnEdit;

    @ViewById
    EditText etPasswordEdit;

    @ViewById
    EditText etConfirmPasswordOnEdit;

    @ViewById
    Button btnSignOut;

    @ViewById
    ImageView imageView3;

    Realm realm;
    SharedPreferences prefs;
    String currentName = "";

    private String path = "";

    @AfterViews
    public void init() {

        realm = Realm.getInstance(RealmUtility.getDefaultConfig());

        RealmResults<User> result = realm.where(User.class).findAll();
        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        String uuidBeingViewed = prefs.getString("uuidUser", null);
        User currentlyEditing = realm.where(User.class)
                .equalTo("uuid", uuidBeingViewed)
                .findFirst();

        currentName = currentlyEditing.getName();
        etUserNameOnEdit.setText(currentlyEditing.getName());
        etPasswordEdit.setText(currentlyEditing.getPassword());
        etConfirmPasswordOnEdit.setText(currentlyEditing.getPassword());

        if (!(currentlyEditing.getPath().equals(""))) {
            File imageOfCurrentlyEditing = new File(getExternalCacheDir(), currentlyEditing.getPath());
            Picasso.get()
                    .load(imageOfCurrentlyEditing)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imageView3);
        }


    }

    @Click(R.id.btnSaveOnEdit)
    public void saveOnEdit() {
        // check if name has value, toast name must not be blank if null
        if (etUserNameOnEdit.getText().toString().equals("")) {
            showToast("Name must not be blank.");
        }
        // check if passwords match, toast confirm password does not match if false
        else if (!etPasswordEdit.getText().toString().equals(etConfirmPasswordOnEdit.getText().toString())) {
            showToast("Confirm Password does not match");
        }
        // else
        else {
            User result = realm.where(User.class)
                    .equalTo("name", etUserNameOnEdit.getText().toString())
                    .findFirst();

            if (result == null || etUserNameOnEdit.getText().toString().equals(currentName)) { // add user
                String uuid = prefs.getString("uuidBeingViewed", null);
                String name = etUserNameOnEdit.getText().toString();
                String password = etPasswordEdit.getText().toString();

                User newUser = new User(uuid, name, password, path);

                try {
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(newUser);  // save
                    realm.commitTransaction();

                    showToast("Edited");
                    finish();
                } catch (Exception e) {
                    showToast("Error saving.");
                }
            }
            else {
                showToast("User already exists");
            }
        }
    }

    @Click(R.id.btnCancelOnEdit)
    public void cancel() {
        finish();
    }


    //===================================================== IMAGE STUFF
    public static int REQUEST_CODE_IMAGE_SCREEN = 0;

    @Click(R.id.imageView3)
    public void selectPic()
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
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView3);
    }

    public void showToast(String str) {
        Toast t  = Toast.makeText(this, str, Toast.LENGTH_LONG);
        t.show();
    }

    @Click(R.id.btnSignOut)
    public void signOut() {
        MainActivity_.intent(this).start();
    }

    public void onDestroy()
    {
        super.onDestroy();
        realm.close();
    }

}

