package com.solusi247.fatkhul.chanthelbeta.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ortiz.touchview.TouchImageView;
import com.solusi247.fatkhul.chanthelbeta.R;

import java.io.File;

public class ImageActivity extends AppCompatActivity {

    private TouchImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //UNPACK OUR DATA FROM INTENT
        Intent i = this.getIntent();
        String path = i.getExtras().getString("namaFile");

        File fileMedia = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getBaseContext().getCacheDir() + "/Chanthel/" + path);
        Uri fileUri = Uri.fromFile(fileMedia);

        image = findViewById(R.id.img);
        image.setImageURI(fileUri);
    }
}
