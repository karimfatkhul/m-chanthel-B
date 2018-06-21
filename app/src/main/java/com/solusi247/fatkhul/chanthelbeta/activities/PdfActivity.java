package com.solusi247.fatkhul.chanthelbeta.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.solusi247.fatkhul.chanthelbeta.R;

import java.io.File;

public class PdfActivity extends AppCompatActivity {

    private String userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName = preferences.getString("username", "");
        password = preferences.getString("password", "");

        // On activity start check whether there is user previously logged in or not.
        if ((userName == "") & (password == "")) {

            // Finishing current Profile activity.
            finish();

            // If user already not log in then Redirect to LoginActivity .
            Intent intent = new Intent(PdfActivity.this, LoginActivity.class);
            startActivity(intent);

            // Showing toast message.
            Toast.makeText(PdfActivity.this, "Please Log in to continue", Toast.LENGTH_LONG).show();
        }

        //PDFVIEW SHALL DISPLAY OUR PDFS
        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
        //SACRIFICE MEMORY FOR QUALITY
        //pdfView.useBestQuality(true)

        //UNPACK OUR DATA FROM INTENT
        Intent i = this.getIntent();
        String path = i.getExtras().getString("namaFile");

//        File file = new File(getBaseContext().getCacheDir() + "/Chanthel/" + path);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getBaseContext().getCacheDir() + "/Chanthel/" + path);
//        Uri fileUri = Uri.fromFile(fileMedia);

        //GET THE PDF FILE
//        File file = new File(path);

        if (file.canRead()) {
            //LOAD IT

            pdfView.fromFile(file).defaultPage(1).onLoad(new OnLoadCompleteListener() {
                @Override
                public void loadComplete(int nbPages) {
                    Toast.makeText(PdfActivity.this, String.valueOf(nbPages), Toast.LENGTH_LONG).show();
                }
            }).load();

        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(PdfActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
