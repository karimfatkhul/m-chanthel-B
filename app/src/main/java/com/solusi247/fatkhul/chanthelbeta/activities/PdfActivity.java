package com.solusi247.fatkhul.chanthelbeta.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.solusi247.fatkhul.chanthelbeta.R;

import java.io.File;

public class PdfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

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
}
