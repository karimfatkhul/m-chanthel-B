package com.solusi247.fatkhul.chanthelbeta.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.solusi247.fatkhul.chanthelbeta.R;

import java.io.File;

public class VideoActivity extends AppCompatActivity {

    private String userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName = preferences.getString("username", "");
        password = preferences.getString("password", "");

        // On activity start check whether there is user previously logged in or not.
        if ((userName == "") & (password == "")) {

            // Finishing current Profile activity.
            finish();

            // If user already not log in then Redirect to LoginActivity .
            Intent intent = new Intent(VideoActivity.this, LoginActivity.class);
            startActivity(intent);

            // Showing toast message.
            Toast.makeText(VideoActivity.this, "Please Log in to continue", Toast.LENGTH_LONG).show();
        }

        VideoView vidView = (VideoView) findViewById(R.id.myVideo);


        Intent i = this.getIntent();
        String fileName = i.getExtras().getString("namaFile");
        File fileMedia = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getBaseContext().getCacheDir() + "/Chanthel/" + fileName);
        Log.d("VIDEO", fileMedia.getAbsolutePath());
        Uri fileUri = Uri.fromFile(fileMedia);
        vidView.setVideoURI(fileUri);
        vidView.start();
        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);
    }

    public void onBackPressed() {
        Intent intent = new Intent(VideoActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
