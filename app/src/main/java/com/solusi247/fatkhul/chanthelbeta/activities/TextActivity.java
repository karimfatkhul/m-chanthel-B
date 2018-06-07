package com.solusi247.fatkhul.chanthelbeta.activities;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.solusi247.fatkhul.chanthelbeta.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        TextView tvPreview = findViewById(R.id.textViewPreview);
        tvPreview.setMovementMethod(new ScrollingMovementMethod());

        //UNPACK OUR DATA FROM INTENT
        Intent i = this.getIntent();
        String path = i.getExtras().getString("namaFile");

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getBaseContext().getCacheDir() + "/Chanthel/" + path);
        if (file.canRead()) {
            String s;
            String fileContent = "";
            try {
                FileInputStream fIn = new FileInputStream(file);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                while ((s = myReader.readLine()) != null) {
                    fileContent += s + "\n";
                }
                myReader.close();
                tvPreview.setText(fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
