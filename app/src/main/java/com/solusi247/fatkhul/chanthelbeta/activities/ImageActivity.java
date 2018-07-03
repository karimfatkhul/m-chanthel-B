package com.solusi247.fatkhul.chanthelbeta.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ortiz.touchview.TouchImageView;
import com.solusi247.fatkhul.chanthelbeta.R;
import com.vincent.filepicker.activity.ImagePickActivity;

import java.io.File;

public class ImageActivity extends AppCompatActivity {

    private TouchImageView image;

    private String userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName = preferences.getString("username", "");
        password = preferences.getString("password", "");

        // On activity start check whether there is user previously logged in or not.
        if ((userName == "") & (password == "")) {

            // Finishing current Profile activity.
            finish();

            // If user already not log in then Redirect to LoginActivity .
            Intent intent = new Intent(ImageActivity.this, LoginActivity.class);
            startActivity(intent);

            // Showing toast message.
            Toast.makeText(ImageActivity.this, "Please Log in to continue", Toast.LENGTH_LONG).show();
        }

        //UNPACK OUR DATA FROM INTENT
        Intent i = this.getIntent();
        String path = i.getExtras().getString("namaFile");

        File fileMedia = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getBaseContext().getCacheDir() + "/Chanthel/" + path);
        Uri fileUri = Uri.fromFile(fileMedia);

        image = findViewById(R.id.img);
        image.setImageURI(fileUri);
    }

    public void onBackPressed() {
        Intent intent = new Intent(ImageActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
