package com.solusi247.fatkhul.chanthelbeta.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.solusi247.fatkhul.chanthelbeta.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 247 on 28/03/2018.
 */

public class LoginActivity extends AppCompatActivity implements Serializable {
    private String urlAPI = "http://192.168.1.228/chanthelAPI/index.php";
    private String pesan;

    private String userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName = preferences.getString("username", "");
        password = preferences.getString("password", "");

        // On activity start check whether there is user previously logged in or not.
        if ((userName != "") & (password != "")) {

            // If user already log in then Redirect to HomeActivity .
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(LoginActivity.this, "Success login", Toast.LENGTH_SHORT).show();
        }

        final EditText textPassword = (EditText) findViewById(R.id.text_password);

        //method untuk mengatur password visibility
        textPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final ImageButton lockButton = (ImageButton) findViewById(R.id.image_password);
                lockButton.setImageResource(R.drawable.ic_visibility);
                lockButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        textPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        lockButton.setImageResource(R.drawable.ic_password);
                                    }
                                });
                                Looper.loop();
                            }

                        }, 2000);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //    Method ketika button login diklik
    public void loginCheck(final View view) {

        // prevent multiple click - start
        view.setClickable(false);

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setClickable(true);
            }
        }, 500);
        // prevent multiple click - end

        EditText textUserName = (EditText) findViewById(R.id.text_username);
        EditText textPassword = (EditText) findViewById(R.id.text_password);
        final String userName = textUserName.getText().toString();
        final String userPassword = textPassword.getText().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlAPI + "?u=" + userName + "&p=" + userPassword + "&act=login", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    pesan = response.getString("message");

                    if (pesan.equals("Success login")) {
                        Toast.makeText(LoginActivity.this, pesan + "", Toast.LENGTH_SHORT).show();

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username", userName.toString());
                        editor.putString("password", userPassword.toString());
                        editor.putString("urlAPI", urlAPI);
                        editor.putString("pid", "1");
                        editor.putString("rootname", "Task");
                        editor.commit();

                        finish();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    } else if (pesan.equals("Error username or password")) {
                        Toast.makeText(LoginActivity.this, "Username and password doesn't match", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, e + "", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Sorry, something wrong with your internet connection", Toast.LENGTH_SHORT).show();
                //Toast.makeText(LoginActivity.this, error+"", Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

}
