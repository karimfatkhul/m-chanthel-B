package com.solusi247.fatkhul.chanthelbeta.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.solusi247.fatkhul.chanthelbeta.R;
import com.solusi247.fatkhul.chanthelbeta.adapter.ContentAdapter;
import com.solusi247.fatkhul.chanthelbeta.data.ContentData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 247 on 02/04/2018.
 */

public class SearchActivity extends AppCompatActivity {
    public ArrayList<ContentData> listData;
    private RecyclerView recyclerView;
    private ContentAdapter contentAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String userName, password, id,pid,foldername;

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName = preferences.getString("username", "");
        password = preferences.getString("password","");

        // On activity start check whether there is user previously logged in or not.
        if ((userName == "") & (password == "")) {

            // Finishing current Profile activity.
            finish();

            // If user already not log in then Redirect to LoginActivity .
            Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
            startActivity(intent);

            // Showing toast message.
            Toast.makeText(SearchActivity.this, "Please Log in to continue", Toast.LENGTH_LONG).show();
        }

        pid = preferences.getString("pid","");
        Toast.makeText(SearchActivity.this, pid, Toast.LENGTH_LONG).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_arrow));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                Toast.makeText(SearchActivity.this, "back", Toast.LENGTH_LONG).show();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("pid", pid);
                editor.commit();
                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        //inisialisasi recyclerview untuk menampung data
        recyclerView = (RecyclerView) findViewById(R.id.search_recycler_content);
        recyclerView.setHasFixedSize(true);
        
        //set resycler view mode ke mode grid
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //inisialisasi adapter untuk binding data dari adapter ke recycler
        listData = new ArrayList<ContentData>();
        contentAdapter = new ContentAdapter(this, listData,layoutManager);
        recyclerView.setAdapter(contentAdapter);
        contentAdapter.notifyDataSetChanged();

        TextView pesan = (TextView)findViewById(R.id.search_message);
        pesan.setText("Please type what you want to search");
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.search_list);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            boolean firstquery = false;

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length()<3){
                    TextView pesan = (TextView)findViewById(R.id.search_message);
                    pesan.setText("Type with minimum 3 character");
                }
                else {
                    if (firstquery){
                        GetData(query);
                    }
                    else {
                        GetData(query);
                        firstquery = true;
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")){
                    listData.clear();
                    ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.serach_message_layout);
                    constraintLayout.setVisibility(View.VISIBLE);
                    TextView pesan = (TextView)findViewById(R.id.search_message);
                    pesan.setText("Please type what you want to search");
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void showToast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }

    private void GetData(String namaFolder){
        String urlSearch ="http://192.168.1.228/chanthelAPI/index.php?u="+userName+"&p="+password+"&act=search&sname=";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(urlSearch+namaFolder,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        listData.clear();
                        if (response.length()>0){
                            for (int i=0; i < response.length(); i++){
                                try{
                                    JSONObject data = response.getJSONObject(i);
                                    ContentData item = new ContentData();
                                    item.setId(data.getString("id"));
                                    item.setPid(data.getString("pid"));
                                    item.setName(data.getString("name"));
                                    item.setTemplate_id(data.getString("template_id"));
                                    String cek = data.getString("template_id");
                                    if (cek.equals("5")){
                                        item.setExt("folder");
                                        item.setContent_image(R.drawable.ic_folder_01);
                                    }
                                    else {
                                        item.setExt(data.getString("ext"));
                                        String cekEkstensi = data.getString("ext");
                                        if (cekEkstensi.equals("pdf")){
                                            item.setContent_image(R.drawable.ic_file_pdf_01);
                                        }
                                        else if (cekEkstensi.equals("jpg")||cekEkstensi.equals("png")){
                                            item.setContent_image(R.drawable.ic_file_jpg_01);
                                        }
                                        else if (cekEkstensi.equals("mp3")){
                                            item.setContent_image(R.drawable.ic_file_mp3_01);
                                        }
                                        else if (cekEkstensi.equals("mp4")||cekEkstensi.equals("3gp")|| cekEkstensi.equals("mkv")){
                                            item.setContent_image(R.drawable.ic_file_mp4_01);
                                        }
                                        else if (cekEkstensi.equals("txt")){
                                            item.setContent_image(R.drawable.ic_file_txt_01);
                                        }
                                        else if (cekEkstensi.equals("doc")||cekEkstensi.equals("docx")){
                                            item.setContent_image(R.drawable.ic_file_doc_01);
                                        }
                                        else {
                                            item.setContent_image(R.drawable.ic_undefined_file_01);
                                        }
                                    }
                                    listData.add(item);
                                    contentAdapter.notifyDataSetChanged();
                                    ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.serach_message_layout);
                                    constraintLayout.setVisibility(View.GONE);
                                }
                                catch (JSONException e){
                                    ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.serach_message_layout);
                                    constraintLayout.setVisibility(View.VISIBLE);
                                    TextView pesan = (TextView)findViewById(R.id.search_message);
                                    pesan.setText("Ooops item not found");
                                }
                            }
                        }
                        else {
                            ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.serach_message_layout);
                            constraintLayout.setVisibility(View.VISIBLE);
                            TextView pesan = (TextView)findViewById(R.id.search_message);
                            pesan.setText("Ooops item not found");

                        }
                    }
                },

                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.serach_message_layout);
                        constraintLayout.setVisibility(View.VISIBLE);
                        TextView pesan = (TextView)findViewById(R.id.search_message);
                        pesan.setText("Ooops item not found");
                    }
                }
        );
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }
}