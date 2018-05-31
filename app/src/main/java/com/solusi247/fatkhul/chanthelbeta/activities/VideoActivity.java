package com.solusi247.fatkhul.chanthelbeta.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.solusi247.fatkhul.chanthelbeta.R;

import java.io.File;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

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
}
