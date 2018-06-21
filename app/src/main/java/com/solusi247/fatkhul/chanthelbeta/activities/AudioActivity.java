package com.solusi247.fatkhul.chanthelbeta.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.solusi247.fatkhul.chanthelbeta.R;

import java.io.File;
import java.io.IOException;

public class AudioActivity extends AppCompatActivity {

    Button playButton;
    TextView elapsedText, remainingText;
    SeekBar seekBar;
    int duration;
    MediaPlayer mediaPlayer = new MediaPlayer();

    String TAG = "Audio Error";

    private String userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName = preferences.getString("username", "");
        password = preferences.getString("password", "");

        // On activity start check whether there is user previously logged in or not.
        if ((userName == "") & (password == "")) {

            // Finishing current Profile activity.
            finish();

            // If user already not log in then Redirect to LoginActivity .
            Intent intent = new Intent(AudioActivity.this, LoginActivity.class);
            startActivity(intent);

            // Showing toast message.
            Toast.makeText(AudioActivity.this, "Please Log in to continue", Toast.LENGTH_LONG).show();
        }

        Intent inten = this.getIntent();

        String namaFile = inten.getExtras().getString("namaFile");

        playButton = findViewById(R.id.playAudioButton);
        elapsedText = findViewById(R.id.elapsedTimeLabel);
        remainingText = findViewById(R.id.remainingTimeLabel);

        File fileMedia = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getBaseContext().getCacheDir() + "/Chanthel/" + namaFile);
        Uri fileUri = Uri.fromFile(fileMedia);
        mediaPlayer = MediaPlayer.create(getBaseContext(), fileUri);

//        try {
////            MediaPlayer mp = new MediaPlayer();
//            mediaPlayer.setDataSource(this, fileUri);
//        } catch (IllegalStateException e) {
//            Log.d(TAG, "IllegalStateException: " + e.getMessage());
//        } catch (IOException e) {
//            Log.d(TAG, "IOException: " + e.getMessage());
//        } catch (IllegalArgumentException e) {
//            Log.d(TAG, "IllegalArgumentException: " + e.getMessage());
//        } catch (SecurityException e) {
//            Log.d(TAG, "SecurityException: " + e.getMessage());
//        }

//        MediaPlayer mediaPlayer = new MediaPlayer();
//        try {
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.setDataSource(getApplicationContext(), fileUri);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        } catch (IllegalStateException e) {
//            Log.d(TAG, "IllegalStateException: " + e.getMessage());
//        } catch (IOException e) {
//            Log.d(TAG, "IOException: " + e.getMessage());
//        } catch (IllegalArgumentException e) {
//            Log.d(TAG, "IllegalArgumentException: " + e.getMessage());
//        } catch (SecurityException e) {
//            Log.d(TAG, "SecurityException: " + e.getMessage());
//        }

        mediaPlayer.setLooping(false);

//        if (mediaPlayer.isPlaying()) {
//            mediaPlayer.seekTo(0);
//            duration = mediaPlayer.getDuration();
//        }
//        mediaPlayer.seekTo(0);
//        duration = mediaPlayer.getDuration();

        mediaPlayer.seekTo(0);
        duration = mediaPlayer.getDuration();


        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(duration);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.seekTo(progress);
                seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();

        Button button = findViewById(R.id.playAudioButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();

                    // pindah tempat
//                    mediaPlayer.seekTo(0);
//                    duration = mediaPlayer.getDuration();

                    playButton.setBackgroundResource(R.drawable.stop);
                } else {
                    mediaPlayer.pause();
                    playButton.setBackgroundResource(R.drawable.play);
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            seekBar.setProgress(currentPosition);

            String elapsedTime = createTimeLabel(currentPosition);
            elapsedText.setText(elapsedTime);

            String remainingTime = createTimeLabel(duration - currentPosition);
            remainingText.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    public void onBackPressed() {
        Intent intent = new Intent(AudioActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
