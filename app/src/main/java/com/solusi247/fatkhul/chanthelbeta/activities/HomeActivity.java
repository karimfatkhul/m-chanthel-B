package com.solusi247.fatkhul.chanthelbeta.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.solusi247.fatkhul.chanthelbeta.R;
import com.solusi247.fatkhul.chanthelbeta.adapter.ContentAdapter;
import com.solusi247.fatkhul.chanthelbeta.data.ContentData;
import com.solusi247.fatkhul.chanthelbeta.helper.CopyApi;
import com.solusi247.fatkhul.chanthelbeta.helper.CopyResponse;
import com.solusi247.fatkhul.chanthelbeta.helper.CutApi;
import com.solusi247.fatkhul.chanthelbeta.helper.CutResponse;
import com.solusi247.fatkhul.chanthelbeta.helper.UploadApi;
import com.solusi247.fatkhul.chanthelbeta.helper.UploadResponse;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.AudioPickActivity;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.activity.VideoPickActivity;
import com.vincent.filepicker.filter.entity.AudioFile;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;
import com.vincent.filepicker.filter.entity.VideoFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.vincent.filepicker.activity.AudioPickActivity.IS_NEED_RECORDER;
import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;


/**
 * Created by 247 on 28/03/2018.
 */


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String urlDirectory;
    private ArrayList<ContentData> listData;
    private RecyclerView recyclerView;
    private ContentAdapter contentAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private boolean status = true;
    private String userName, password, rootName, idfolder, namaContent, action, idRename, path, fileName, messej;
    private String pid, lastPid;
    private TextView rootFolder;
    private String kontenFile;
    private String previewName;
    private String fid;
    private ProgressDialog dialogLoading;

    private static final int REQUEST_ID_READ_PERMISSION = 100;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;

    private DownloadManager downloadManager;
    ArrayList<Long> list = new ArrayList<>();
    private long refid;

    boolean doDisplay;
    String ekstensi;

    private BottomNavigationView bottomNavigation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        bottomNavigation = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        bottomNavigation.setVisibility(View.GONE);

        //inisialisasi komponen ui
        drawer = findViewById(R.id.drawer_layout);
        fab = findViewById(R.id.fab);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get data dari login form
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName = preferences.getString("username", "");
        password = preferences.getString("password", "");


        // On activity start check whether there is user previously logged in or not.
        if ((userName == "") & (password == "")) {

            // Finishing current Profile activity.
            finish();

            // If user already not log in then Redirect to LoginActivity .
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);

            // Showing toast message.
            Toast.makeText(HomeActivity.this, "Please Log in to continue", Toast.LENGTH_LONG).show();
        }


        urlDirectory = preferences.getString("urlAPI", "");
        pid = preferences.getString("pid", "1");
        lastPid = preferences.getString("lastPid", "1");
        rootName = preferences.getString("rootName", "Task");
        rootFolder = (TextView) findViewById(R.id.root_name);
        rootFolder.setText(rootName);

        //inisialisasi recyclerview untuk menampung data
        recyclerView = (RecyclerView) findViewById(R.id.recycler_content);
        recyclerView.setHasFixedSize(true);

        GetData(pid);
        //set resycler view mode ke mode grid
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        //inisialisasi adapter untuk binding data dari adapter ke recycler
        listData = new ArrayList<ContentData>();
        contentAdapter = new ContentAdapter(this, listData, layoutManager);
        recyclerView.setAdapter(contentAdapter);
        contentAdapter.notifyDataSetChanged();

        //inisialisasi view mode option
        //ISSUE KETIKA MODE VIEW AKTIF, FOLDER TIDAK BISA DIBUKA
        final ImageView imageView = (ImageView) findViewById(R.id.view_mode);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == true) {
                    //mengganti mode tampilan ke list
                    layoutManager = new LinearLayoutManager(getBaseContext());
                    recyclerView.setLayoutManager(layoutManager);

                    ScrollView scrollView = (ScrollView) findViewById(R.id.scrollViewHome);
                    scrollView.setPadding(0, 0, 0, 65);

                    imageView.setImageResource(R.drawable.ic_format_grid_white);
                    status = false;
                } else {
                    //mengganti mode tampilan ke grid
                    layoutManager = new GridLayoutManager(getBaseContext(), 2);
                    recyclerView.setLayoutManager(layoutManager);

                    imageView.setImageResource(R.drawable.ic_format_list_bulleted_white);
                    status = true;
                }
                //contentAdapter = new ContentAdapter(HomeActivity.this, listData, layoutManager);
                recyclerView.setAdapter(contentAdapter);
                contentAdapter.notifyDataSetChanged();

            }
        });

        contentAdapter.setOnItemClickListener(new ContentAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(final View view, final int position) {

                // prevent multiple click - start
                view.setClickable(false);

                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setClickable(true);
                    }
                }, 500);
                // prevent multiple click - end

                switch (view.getId()) {
                    case R.id.content_image:
                        //showToast(listData.get(position).getTemplate_id().toString());
                        if (listData.get(position).getTemplate_id().toString().matches("5")) {
                            String cek = listData.get(position).getId();
//                            String foldernames = listData.get(position).getName();
                            rootName = listData.get(position).getName();
                            lastPid = pid;
                            pid = cek.toString();
                            rootFolder.setText(rootName);
                            listData.clear();
                            GetData(pid);

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("pid", pid);
                            editor.putString("lastPid", lastPid);
                            editor.putString("rootName", rootName);
                            editor.commit();
//                        showToast(pid);
                        }
                        break;

                    case R.id.more_option:
                        final BottomSheetDialog bottomSheetDialogMoreOption = new BottomSheetDialog(HomeActivity.this);
                        View bottomDialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_more_option, null);
                        bottomSheetDialogMoreOption.setContentView(bottomDialogView);

                        TextView contentName = (TextView) bottomDialogView.findViewById(R.id.more_option_content_name);
//                        LinearLayout linearLayout = (LinearLayout) bottomDialogView.findViewById(R.id.preview);
//                        linearLayout.setVisibility(View.GONE);
                        //View border = (View) bottomDialogView.findViewById(R.id.border_preview);
                        //border.setVisibility(View.GONE);
                        ImageView chat = (ImageView) bottomDialogView.findViewById(R.id.chat);
                        chat.setVisibility(View.GONE);
                        ImageView thumbnail = (ImageView) bottomDialogView.findViewById(R.id.more_option_thumbnail);
                        String templateID = listData.get(position).getTemplate_id();
                        if (templateID.equals("5")) {
                            thumbnail.setImageResource(R.drawable.ic_folder);
                        } else {
                            String ext = listData.get(position).getExt();
                            if (ext.equals("pdf")) {
                                thumbnail.setImageResource(R.drawable.ic_file_pdf_01);
                            } else if (ext.equals("doc") || ext.equals("docx")) {
                                thumbnail.setImageResource(R.drawable.ic_file_doc_01);
                            } else if (ext.equals("xls") || ext.equals("xlsx")) {
                                thumbnail.setImageResource(R.drawable.ic_file_xlx_01);
                            } else if (ext.equals("mp4") || ext.equals("3gp") || ext.equals("mov") || ext.equals("avi") || ext.equals("mkv")) {
                                thumbnail.setImageResource(R.drawable.ic_file_mp4_01);
                            } else if (ext.equals("mp3") || ext.equals("wav") || ext.equals("aac") || ext.equals("mpg") || ext.equals("amr")) {
                                thumbnail.setImageResource(R.drawable.ic_file_mp3_01);
                            } else if (ext.equals("jpg") || ext.equals("png") || ext.equals("bmp") || ext.equals("tiff") || ext.equals("gif")) {
                                thumbnail.setImageResource(R.drawable.ic_file_jpg_01);
                            } else if (ext.equals("txt")) {
                                thumbnail.setImageResource(R.drawable.ic_file_txt_01);
                            } else {
                                thumbnail.setImageResource(R.drawable.ic_undefined_file_01);
                            }
                        }
                        namaContent = listData.get(position).getName();
                        contentName.setText(namaContent);
                        bottomSheetDialogMoreOption.show();
                        LinearLayout preview = bottomDialogView.findViewById(R.id.preview);
                        LinearLayout download = bottomDialogView.findViewById(R.id.download);
                        LinearLayout cut = bottomSheetDialogMoreOption.findViewById(R.id.cut);
                        LinearLayout copy = bottomSheetDialogMoreOption.findViewById(R.id.copy);
                        LinearLayout rename = bottomSheetDialogMoreOption.findViewById(R.id.rename);
                        LinearLayout delete = bottomSheetDialogMoreOption.findViewById(R.id.delete);
                        LinearLayout linePreview = bottomSheetDialogMoreOption.findViewById(R.id.linearPreview);
                        LinearLayout lineDownload = bottomSheetDialogMoreOption.findViewById(R.id.linearDownload);
                        LinearLayout lineCut = bottomSheetDialogMoreOption.findViewById(R.id.linearCut);
                        LinearLayout lineCopy = bottomSheetDialogMoreOption.findViewById(R.id.linearCopy);
                        if (listData.get(position).getTemplate_id().matches("5")) {
                            linePreview.setVisibility(View.GONE);
                            preview.setVisibility(View.GONE);
                            lineDownload.setVisibility(View.GONE);
                            download.setVisibility(View.GONE);
                            lineCut.setVisibility(View.GONE);
                            cut.setVisibility(View.GONE);
                            lineCopy.setVisibility(View.GONE);
                            copy.setVisibility(View.GONE);
                        }

                        //cut
                        cut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String template_id = listData.get(position).getTemplate_id();
                                if (template_id.matches("6")) {
                                    final String filePid = pid;
                                    fid = listData.get(position).getId();

                                    bottomNavigation.setVisibility(View.VISIBLE);
                                    bottomSheetDialogMoreOption.hide();

                                    dialogLoading = new ProgressDialog(HomeActivity.this);
                                    dialogLoading.setIndeterminate(true);
                                    dialogLoading.setMessage("Loading");

                                    bottomNavigation.setOnNavigationItemSelectedListener(
                                            new BottomNavigationView.OnNavigationItemSelectedListener() {
                                                @Override
                                                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                                    switch (item.getItemId()) {
                                                        case R.id.action_paste:
                                                            if (filePid == pid) {
                                                                showToast("Cannot cut file in same folder");
                                                                break;
                                                            }

                                                            dialogLoading.show();
                                                            HashMap<String, String> params = new HashMap<>();
                                                            params.put("u", userName);
                                                            params.put("p", password);
                                                            params.put("act", "move");
                                                            params.put("fid", fid);
                                                            params.put("pid", pid);
                                                            fungsiCut(params);

                                                            bottomNavigation.setVisibility(View.GONE);
                                                            restartActivity(pid);
                                                            break;

                                                        case R.id.action_add_folder:
                                                            showToast("Not Available Yet");
                                                            break;

                                                        case R.id.action_cancel:
                                                            bottomNavigation.setVisibility(View.GONE);
                                                            break;
                                                    }
                                                    return false;
                                                }
                                            });
                                }
                            }
                        });
                        //cut

                        // copy
                        copy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String template_id = listData.get(position).getTemplate_id();
                                if (template_id.matches("6")) {
                                    final String filePid = pid;
                                    fid = listData.get(position).getId();

                                    bottomNavigation.setVisibility(View.VISIBLE);
                                    bottomSheetDialogMoreOption.hide();

                                    dialogLoading = new ProgressDialog(HomeActivity.this);
                                    dialogLoading.setIndeterminate(true);
                                    dialogLoading.setMessage("Loading");

                                    bottomNavigation.setOnNavigationItemSelectedListener(
                                            new BottomNavigationView.OnNavigationItemSelectedListener() {
                                                @Override
                                                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                                    switch (item.getItemId()) {
                                                        case R.id.action_paste:
                                                            if (filePid == pid) {
                                                                showToast("Cannot copy file in same folder");
                                                                break;
                                                            }

                                                            dialogLoading.show();
                                                            HashMap<String, String> params = new HashMap<>();
                                                            params.put("u", userName);
                                                            params.put("p", password);
                                                            params.put("act", "copy_paste");
                                                            params.put("fid", fid);
                                                            params.put("pid", pid);
                                                            fungsiCopy(params);

                                                            bottomNavigation.setVisibility(View.GONE);
                                                            restartActivity(pid);
                                                            break;

                                                        case R.id.action_add_folder:
                                                            showToast("Not Available Yet");
                                                            break;

                                                        case R.id.action_cancel:
                                                            bottomNavigation.setVisibility(View.GONE);
                                                            break;
                                                    }
                                                    return false;
                                                }
                                            });
                                }
                            }
                        });
                        //copy

                        preview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                doDisplay = true;
                                final String template_id;
                                final String item_id;
                                template_id = listData.get(position).getTemplate_id();
                                item_id = listData.get(position).getId();
                                if (template_id.matches("6")) {
                                    fileName = listData.get(position).getName();
                                    ekstensi = listData.get(position).getExt();

                                    if (ekstensi.matches("zip") || ekstensi.matches("gz")) {
                                        showToast("Sorry, we can't preview this file");
                                    } else {
                                        fungsiPreview(item_id, fileName, ekstensi);

                                        dialogLoading = new ProgressDialog(HomeActivity.this);
                                        dialogLoading.setIndeterminate(true);
                                        dialogLoading.setMessage("Please wait ...");
                                        dialogLoading.show();
                                    }
                                    bottomSheetDialogMoreOption.hide();
                                }
                            }
                        });

                        download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                doDisplay = false;
                                final String template_id;
                                final String item_id;
                                template_id = listData.get(position).getTemplate_id();
                                item_id = listData.get(position).getId();
                                if (template_id.matches("6")) {
                                    fileName = listData.get(position).getName();
                                    final AlertDialog.Builder builderDownload = new AlertDialog.Builder(HomeActivity.this);
                                    builderDownload.setMessage("Are you sure you want to download this file ?")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    fungsiDownload(item_id, fileName);
                                                }
                                            }).setNegativeButton("Cancel", null);
                                    AlertDialog alert = builderDownload.create();
                                    alert.show();
                                    bottomSheetDialogMoreOption.hide();
                                }
                            }
                        });

                        rename.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                                LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
                                final View view = inflater.inflate(R.layout.new_folder_popup, null);
                                final EditText contentName = (EditText) view.findViewById(R.id.new_content_name);
                                final String title, template_id;
                                idRename = listData.get(position).getId();
                                title = listData.get(position).getName();
                                template_id = listData.get(position).getTemplate_id();
                                contentName.setHint(title);
                                builder.setView(view)
                                        .setTitle("Rename " + title)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    //get folder name that user inputed
                                                    String folderName = contentName.getText().toString();

                                                    if (folderName.matches(".")) {
                                                        folderName.replace(" ", "%20");
                                                    }

                                                    if (template_id.equals("5")) {
                                                        action = "rename_directory&id=";
                                                        messej = "Folder has been renamed";
                                                        String solar = "&solr=http://192.168.1.250:8983";
                                                        listData.clear();
                                                        RenameContent(folderName, solar);
                                                        contentAdapter.notifyItemChanged(position);
                                                        contentAdapter.notifyDataSetChanged();
                                                        showToast(messej);
                                                    } else {
                                                        if (folderName.contains(".")) {
                                                            showToast("please do not use \".\" symbol");
                                                        } else {
                                                            ekstensi = listData.get(position).getExt();
                                                            if (ekstensi.length() != 0) {
                                                                folderName = folderName + "." + ekstensi;
                                                            }
                                                            action = "rename_file&fid=";
                                                            messej = "File has been renamed";
                                                            listData.clear();
                                                            RenameContent(folderName, "");
                                                            contentAdapter.notifyItemChanged(position);
                                                            contentAdapter.notifyDataSetChanged();
                                                            showToast(messej);
                                                        }
                                                    }
                                                    dialog.dismiss();

                                                    restartActivity(pid);


                                                } catch (Exception e) {
                                                    showToast(e + "");
                                                }

                                                listData.clear();
                                                GetData(pid);
//                                                showToast(pid);
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                bottomSheetDialogMoreOption.hide();
                            }
                        });

                        //delete belum bisa automatic refresh
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                final AlertDialog.Builder builderDelete = new AlertDialog.Builder(HomeActivity.this);
                                builderDelete.setMessage("Are you sure, want to Delete this Folder ?")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                idfolder = listData.get(position).getId();
                                                pid = listData.get(position).getPid();
                                                String pidFolder = listData.get(position).getPid();
                                                try {
                                                    DeleteData(idfolder);
                                                    contentAdapter.notifyItemRemoved(position);
                                                    contentAdapter.notifyDataSetChanged();
                                                    showToast("Delete Success");
                                                    dialog.dismiss();
                                                    listData.clear();
                                                    //GetData(pid);

                                                    restartActivity(pid);

                                                } catch (Exception e) {
                                                    showToast(e + "");
                                                }
                                            }
                                        })
                                        .setNegativeButton("Cancel", null);
                                AlertDialog alert = builderDelete.create();
                                alert.show();
                                bottomSheetDialogMoreOption.hide();
                            }
                        });
                        break;
                }
            }
        });

        //inisialisasi floating action bar with bottomsheet dialog
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(HomeActivity.this);
        View bottomDialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_add, null);
        bottomSheetDialog.setContentView(bottomDialogView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });
        LinearLayout add_task = (LinearLayout) bottomSheetDialog.findViewById(R.id.add_task);
        LinearLayout add_folder = (LinearLayout) bottomSheetDialog.findViewById(R.id.add_folder);
        LinearLayout add_link = (LinearLayout) bottomSheetDialog.findViewById(R.id.add_link);
        LinearLayout add_case = (LinearLayout) bottomSheetDialog.findViewById(R.id.add_case);
        LinearLayout upload_file = (LinearLayout) bottomSheetDialog.findViewById(R.id.upload_file);
        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Not Available Yet");
            }
        });
        add_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Not Available Yet");
            }
        });
        add_case.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Not Available Yet");
            }
        });

        // nambah folder
        add_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.new_folder_popup, null);
                final EditText contentName = (EditText) view.findViewById(R.id.new_content_name);
                builder.setView(view)
                        .setTitle("Create New Folder")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {

                                    //get folder name that user inputed
                                    String folderName = contentName.getText().toString();
                                    CreateContent(folderName);
                                    listData.clear();
                                    contentAdapter.notifyDataSetChanged();
                                    showToast("Folder has been created");
                                    dialog.dismiss();
                                    //GetData(pid);
//                                    showToast(pid+"");

                                    restartActivity(pid);

                                } catch (Exception e) {
                                    showToast(e + "");
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                bottomSheetDialog.hide();
            }
        });
        upload_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoice();
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
//
//                }
//
//                if (checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    showToast("write permission ok");
//                }
//
//                new MaterialFilePicker()
//                        .withActivity(HomeActivity.this)
//                        .withRequestCode(1000)
//                        .withHiddenFiles(true) // Show hidden files and folders
//                        .start();
//
////                UploadFile(fileName, path);
//                fungsiUpload(path);

//                final Context ctx = HomeActivity.this;
//                new ChooserDialog(ctx)
////                        .withFilterRegex(false, true, ".*\\.(jpe?g|png)")
//                        .withStartFile(path)
//                        .withResources(R.string.title_choose_file, R.string.title_choose, R.string.dialog_cancel)
//                        .withChosenListener(new ChooserDialog.Result() {
//                            @Override
//                            public void onChoosePath(String paths, File pathFile) {
////                                Toast.makeText(ctx, "FILE: " + paths, Toast.LENGTH_SHORT).show();
////                                path = paths;
//                                fungsiUpload(paths, pid);
////                                restartActivity(pid);
//                            }
//                        })
//                        .withNavigateUpTo(new ChooserDialog.CanNavigateUp() {
//                            @Override
//                            public boolean canUpTo(File dir) {
//                                return true;
//                            }
//                        })
//                        .withNavigateTo(new ChooserDialog.CanNavigateTo() {
//                            @Override
//                            public boolean canNavigate(File dir) {
//                                return true;
//                            }
//                        })
//                        .build()
//                        .show();

                bottomSheetDialog.hide();
            }
        });

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (!askPermission(REQUEST_ID_WRITE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        }
    }

    // check user after back onBackPressed
    @Override
    protected void onResume() {
        super.onResume();

        //get data dari login form
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName = preferences.getString("username", "");
        password = preferences.getString("password", "");

        if ((userName.matches("")) & (password.matches(""))) {
            finish();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(HomeActivity.this, "Please Log in to continue", Toast.LENGTH_LONG).show();
        }
    }

//    private void getLinkPreview(final String item_id, final String fileName) {
//        String apiPreview = urlDirectory + "?u=" + userName + "&p=" + password + "&act=preview_file&fid=" + item_id;
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiPreview, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    if (response.getInt("error_code") == 3) {
//                        showToast("sorry, something wrong");
//                    } else {
//                        String urlPReview = response.getString("url");
//                        fungsiPreview(urlPReview, fileName);
//                    }
//                } catch (org.json.JSONException err) {
//                    Log.e("PREVIEW", err.toString());
//                    showToast(err.toString());
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("PREVIEW", error.toString());
//                showToast(error.toString());
//            }
//        }
//        );
//        Volley.newRequestQueue(this).add(jsonObjectRequest);
//    }

    private void fungsiPreview(final String item_id, final String fileName, String ekstensi) {
        list.clear();

        previewName = fileName;

        Uri Download_Uri;

        // clear temporary folder
        File ext = Environment.getExternalStorageDirectory();
//        ext = getBaseContext().getCacheDir();
//        File cDir = getBaseContext().getCacheDir();
        File parentDir = new File(ext.getAbsolutePath() + getBaseContext().getCacheDir() + "/Chanthel");
        List<File> listFiles = getListFiles(parentDir);
        if (listFiles.size() != 0) {
            deleteAllFiles(listFiles);
        }

        if (ekstensi.matches("doc") || ekstensi.matches("docx") || ekstensi.matches("xls") ||
                ekstensi.matches("xlsx") || ekstensi.matches("ppt") || ekstensi.matches("pptx") ||
                ekstensi.matches("dps") || ekstensi.matches("js") || ekstensi.matches("txt") ||
                ekstensi.matches("odt") || ekstensi.matches("odp") || ekstensi.matches("ods")) {

            String dokumenPreview = urlDirectory + "?u=" + userName + "&p=" + password + "&act=download&fid=" + item_id;
            Download_Uri = Uri.parse(dokumenPreview);

        } else {
            String nonDokumenPreview = urlDirectory + "?u=" + userName + "&p=" + password + "&act=preview_file_mobile&fid=" + item_id;
            Download_Uri = Uri.parse(nonDokumenPreview);
        }

//        if (ekstensi.matches("doc") || ekstensi.matches("docx") || ekstensi.matches("xls") ||
//                ekstensi.matches("xlsx") || ekstensi.matches("ppt") || ekstensi.matches("pptx") ||
//                ekstensi.matches("txt") || ekstensi.matches("js")) {
//            previewName = fileName.replace("." + ekstensi, "") + ".pdf";
//        } else {
//            previewName = fileName;
//        }
//        String apiPreview = urlDirectory + "?u=" + userName + "&p=" + password + "&act=preview_file_mobile&fid=" + item_id;
//        Uri Download_Uri = Uri.parse(apiPreview);

        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle("Chanthel Downloading " + fileName);
        request.setDescription("Downloading " + fileName);
        request.setVisibleInDownloadsUi(true);

//        File tempFile;
//        String newName = "";
//        String[] shortName = fileName.split("\\.");
//        for (int i = 0; i < shortName.length - 1; i++) {
//            newName = newName + shortName[i];
//        }

//        showToast(newName);
//        final String TEMP_FILE_NAME = newName;

        // temp
//        final String TEMP_FILE_NAME = newName;
//        File tempFile;
//        final String TEMP_FILE_NAME = fileName;

        /** Getting Cache Directory */
        File cDir = getBaseContext().getCacheDir();

        /** Getting a reference to temporary file, if created earlier */
//        tempFile = new File(cDir.getPath() + "/" + TEMP_FILE_NAME);


        request.setDestinationInExternalPublicDir(cDir.getPath(), "/Chanthel/" + fileName);


        Log.e("DIR", "" + cDir.getPath());
        Log.e("DIR", cDir.getPath() + "/Chanthel/" + fileName);
        //showToast(HomeActivity.this.getCacheDir().getAbsolutePath());
        // temp

        refid = downloadManager.enqueue(request);

        Log.e("OUT", "" + refid);

        list.add(refid);

//        // launch other apps
//        Intent sharingIntent = new Intent(Intent.ACTION_VIEW);
//        Uri screenshotUri = Uri.parse(cDir.getPath() + "/Chanthel/" + TEMP_FILE_NAME);
//
//        sharingIntent.setType("image/*");
//        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
//        startActivity(Intent.createChooser(sharingIntent, "Share image using"));
    }

    private void displayPreview(String namaFile, String tipeFile) {

        Intent intent = null;
        File file;
        Uri uri;
        // setType berdasarkan tipe file
        try {
            switch (tipeFile) {
                case "odt":
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getBaseContext().getCacheDir() + "/Chanthel/" + namaFile);
                    uri = Uri.fromFile(file);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.oasis.opendocument.text");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case "odp":
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getBaseContext().getCacheDir() + "/Chanthel/" + namaFile);
                    uri = Uri.fromFile(file);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.oasis.opendocument.presentation");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case "ods":
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getBaseContext().getCacheDir() + "/Chanthel/" + namaFile);
                    uri = Uri.fromFile(file);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.oasis.opendocument.spreadsheet");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case "png":
                case "jpg":
                case "gif":
                    intent = new Intent(this, ImageActivity.class);
                    break;
                case "mp3":
                    intent = new Intent(this, AudioActivity.class);
                    break;
                case "doc":
                case "docx":
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getBaseContext().getCacheDir() + "/Chanthel/" + namaFile);
                    uri = Uri.fromFile(file);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/msword");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case "xls":
                case "xlsx":
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getBaseContext().getCacheDir() + "/Chanthel/" + namaFile);
                    uri = Uri.fromFile(file);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case "dps":
                case "ppt":
                case "pptx":
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + getBaseContext().getCacheDir() + "/Chanthel/" + namaFile);
                    uri = Uri.fromFile(file);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case "pdf":
//                sharingIntent.setType("application/pdf");
                    intent = new Intent(this, PdfActivity.class);
                    break;
                case "mp4":
                case "3gp":
                    intent = new Intent(this, VideoActivity.class);
                    break;
                case "js":
                case "txt":
                case "hjs":
                case "html":
                    intent = new Intent(this, TextActivity.class);
                    break;
//                default:
//                    showToast("Sorry, we can't preview this file");
//                    dialogLoading.dismiss();
            }

            // ini kayaknya bisa dihapus karena tiap masuk file sudah langsung di save di preferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("pid", pid);
            editor.commit();

            intent.putExtra("namaFile", namaFile);

            dialogLoading.dismiss();

            this.startActivity(intent);
        } catch (Exception e) {
            showToast("Sorry, we can't preview this file");
            dialogLoading.dismiss();
        }
        //finish();
    }

//    private void displayPreview(String namaFile, String tipeFile) {
//        final String TEMP_FILE_NAME = namaFile;
//
//        /** Getting Cache Directory */
//        File cDir = getBaseContext().getCacheDir();
//
//        Intent sharingIntent = new Intent(Intent.ACTION_VIEW);
//        Uri previewUri = Uri.parse(cDir.getPath() + "/Chanthel/" + TEMP_FILE_NAME);
//
//        // setType berdasarkan tipe file
//        switch (tipeFile) {
//            case "png":
//            case "jpg":
//                sharingIntent.setType("image/*");
//                break;
//            case "mp3":
//                sharingIntent.setType("audio/*");
//                break;
//            case "doc":
//            case "docx":
//            case "xls":
//            case "xlsx":
//            case "ppt":
//            case "pptx":
//            case "txt":
//            case "pdf":
//                sharingIntent.setType("application/pdf");
//                break;
//            case "mp4":
//                sharingIntent.setType("video/*");
//                break;
//        }
//
//        sharingIntent.putExtra(Intent.EXTRA_STREAM, previewUri);
//        startActivity(Intent.createChooser(sharingIntent, "display preview using"));
//    }


    // download file
//    private void downloadFile(String item_id, final String fileName) {
//        String urlRename = urlDirectory + "?u=" + userName + "&p=" + password + "&act=download&fid=" + item_id;
//        StringRequest stringReq = new StringRequest(Request.Method.GET, urlRename, new Response.Listener<String>() {
//            public void onResponse(String response) {
//                if (response.matches("error_code: 3")) {
//                    showToast("sorry, download failed");
//                } else {
//                    //showToast(response);
//                    askPermissionAndWriteFile(response, fileName);
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                showToast(error.toString());
//            }
//        }
//        );
//        Volley.newRequestQueue(this).add(stringReq);
//    }

    //inisialisasi method untuk menambahkan file baru (upload file)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            // Sementara pakai toast dulu nanti lanjutkan untuk upload file ke dalam folder
            Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
            path = filePath;
            fileName = path.substring(path.lastIndexOf("/") + 1);
            showToast(fileName + "");
        } else {
            String filePath = "";
            switch (requestCode) {
                case Constant.REQUEST_CODE_PICK_IMAGE:
                    if (resultCode == RESULT_OK) {
                        ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
//                        StringBuilder builder = new StringBuilder();
                        for (ImageFile file : list) {
                            filePath = file.getPath();
//                            builder.append(path + "\n");
//                            builder.append(path);
                        }
//                    Toast.makeText(MainActivity.this, builder.toString(), Toast.LENGTH_LONG);
//                        tView.setText(builder.toString());
//                        fungsiUpload(filePath, pid);
                    }
                    break;
                case Constant.REQUEST_CODE_PICK_VIDEO:
                    if (resultCode == RESULT_OK) {
                        ArrayList<VideoFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                        for (VideoFile file : list) {
                            filePath = file.getPath();
                        }
                    }
                    break;
                case Constant.REQUEST_CODE_PICK_AUDIO:
                    if (resultCode == RESULT_OK) {
                        ArrayList<AudioFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO);
                        for (AudioFile file : list) {
                            filePath = file.getPath();
                        }
                    }
                    break;
//                case Constant.REQUEST_CODE_PICK_FILE:
//                    if (resultCode == RESULT_OK) {
//                        ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
//                        StringBuilder builder = new StringBuilder();
//                        for (NormalFile file : list) {
//                            String path = file.getPath();
//                            builder.append(path + "\n");
////                        Toast.makeText(MainActivity.this, builder.toString(), Toast.LENGTH_LONG);
//                        }
////                        tView.setText(builder.toString());
//                    }
//                    break;
            }
            fungsiUpload(filePath, pid);
        }
    }

//    //inisialisasi method untuk meminta akses terhadap external storage
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 1001: {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission granteed", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Permission not granteed", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//        }
//    }

    //inisialisasi method toas untuk menampilkan informasi
    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }


    //inisialisasi method untuk key back press
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (pid.equals("1")) {
//            finish();
            if (doubleBackToExitPressedOnce) {
                // broadcast logout signal to all activity
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.package.ACTION_LOGOUT");
                sendBroadcast(broadcastIntent);
                // broadcast logout signal to all activity

//                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
//                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Please click once more to exit", Toast.LENGTH_SHORT).show();
            }
            this.doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 5000);
        } else {
//            showToast(lastPid);
            listData.clear();
//            GetLastData(lastPid);
//            if (lastPid.matches("1")) {
//                rootName = "Task";
//            } else {
            pid = lastPid;
            GetData(lastPid);
//            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSearch:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("pid", pid);
                editor.commit();
                Intent i = new Intent(HomeActivity.this, SearchActivity.class);
                this.startActivity(i);
                finish();
                break;
            case R.id.actionNotification:
//                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
//                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navWorkflow) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, new WorkflowFragment())
//                    .addToBackStack(null)
//                    .commit();
        } else if (id == R.id.navTask) {

        } else if (id == R.id.navAbout) {
            aboutUs();
        } else if (id == R.id.navLogOut) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setMessage("Are you sure, want to Exit this app ?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.clear();
                            editor.apply();

                            // broadcast logout signal to all activity
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("com.package.ACTION_LOGOUT");
                            sendBroadcast(broadcastIntent);
                            // broadcast logout signal to all activity

                            //finish();

                            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", null);
            AlertDialog alert = builder.create();
            alert.show();
        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //inisialisasi method about us
    public void aboutUs() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Chanthel App version 1.0.0");
        AlertDialog alert = builder.create();
        alert.show();
    }

    //inisialisasi get data method for fetching json data
    public void GetData(final String pids) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlDirectory + "?u=" + userName + "&p=" + password + "&act=tree_directory&pid=" + pids, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String errorCode = response.getString("error_code");
                            if (errorCode.equals("0")) {

                                try {
                                    JSONArray jsonArray = response.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject data = jsonArray.getJSONObject(i);
                                        ContentData item = new ContentData();
                                        item.setId(data.getString("id"));
                                        item.setPid(data.getString("pid"));
                                        item.setName(data.getString("name"));
                                        item.setTemplate_id(data.getString("template_id"));
                                        String cek = data.getString("template_id");

                                        if (cek.equals("5")) {
                                            item.setExt("folder");
                                            item.setContent_image(R.drawable.ic_folder_01);
                                        } else {
                                            item.setExt(data.getString("ext"));
                                            String cekEkstensi = data.getString("ext");
                                            if (cekEkstensi.equals("pdf")) {
                                                item.setContent_image(R.drawable.ic_file_pdf_01);
                                            } else if (cekEkstensi.equals("jpg") || cekEkstensi.equals("png") || cekEkstensi.equals("tiff") || cekEkstensi.equals("gif") || cekEkstensi.equals("bmp")) {
                                                item.setContent_image(R.drawable.ic_file_jpg_01);
                                            } else if (cekEkstensi.equals("mp3") || cekEkstensi.equals("wav") || cekEkstensi.equals("aac") || cekEkstensi.equals("mpg") || cekEkstensi.equals("amr")) {
                                                item.setContent_image(R.drawable.ic_file_mp3_01);
                                            } else if (cekEkstensi.equals("mp4") || cekEkstensi.equals("3gp") || cekEkstensi.equals("mkv") || cekEkstensi.equals("mov") || cekEkstensi.equals("avi")) {
                                                item.setContent_image(R.drawable.ic_file_mp4_01);
                                            } else if (cekEkstensi.equals("txt")) {
                                                item.setContent_image(R.drawable.ic_file_txt_01);
                                            } else if (cekEkstensi.equals("doc") || cekEkstensi.equals("docx")) {
                                                item.setContent_image(R.drawable.ic_file_doc_01);
                                            } else if (cekEkstensi.equals("xls") || cekEkstensi.equals("xlsx")) {
                                                item.setContent_image(R.drawable.ic_file_xlx_01);
                                            } else {
                                                item.setContent_image(R.drawable.ic_undefined_file_01);
                                            }
                                        }
                                        listData.add(item);
                                        contentAdapter.notifyDataSetChanged();
                                    }

                                    // get name dan pid baru

                                    if (pid.matches("1")) {
                                        rootName = "Task";

                                        // name for root
                                        rootFolder.setText(rootName);

                                        // commit ke preferences
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("pid", pid);
                                        editor.putString("rootName", rootName);
                                        editor.commit();

                                    } else if (!(lastPid.matches("1"))) {
                                        if (pids == lastPid) {
                                            getNamePid(lastPid);
                                        }
                                    }

                                } catch (JSONException e) {
                                    showToast(e + "");
                                }

                            } else {
                                listData.clear();
                                String pesan = response.getString("message");
                                showToast(pesan + "");
                                contentAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            showToast(e + "");
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                showToast(error + "");
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    public void GetLastData(String pids) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlDirectory + "?u=" + userName + "&p=" + password + "&act=back_tree_directory&fid=" + pids, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String errorCode = response.getString("error_code");
                            if (errorCode.equals("0")) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject data = jsonArray.getJSONObject(i);
                                        ContentData item = new ContentData();
                                        item.setId(data.getString("id"));
                                        item.setPid(data.getString("pid"));
                                        item.setName(data.getString("name"));
                                        item.setTemplate_id(data.getString("template_id"));
                                        lastPid = item.getPid();
                                        pid = lastPid;
                                        if (lastPid.equals("1")) {
                                            rootFolder.setText("Task");
                                        }
                                        String cek = data.getString("template_id");
                                        if (cek.equals("5")) {
                                            item.setExt("folder");
                                            item.setContent_image(R.drawable.ic_folder_01);
                                        } else {
                                            item.setExt(data.getString("ext"));
                                            String cekEkstensi = data.getString("ext");
                                            if (cekEkstensi.equals("pdf")) {
                                                item.setContent_image(R.drawable.ic_file_pdf_01);
                                            } else if (cekEkstensi.equals("jpg") || cekEkstensi.equals("png") || cekEkstensi.equals("tiff") || cekEkstensi.equals("gif") || cekEkstensi.equals("bmp")) {
                                                item.setContent_image(R.drawable.ic_file_jpg_01);
                                            } else if (cekEkstensi.equals("mp3") || cekEkstensi.equals("wav") || cekEkstensi.equals("aac") || cekEkstensi.equals("mpg") || cekEkstensi.equals("amr")) {
                                                item.setContent_image(R.drawable.ic_file_mp3_01);
                                            } else if (cekEkstensi.equals("mp4") || cekEkstensi.equals("3gp") || cekEkstensi.equals("mkv") || cekEkstensi.equals("mov") || cekEkstensi.equals("avi")) {
                                                item.setContent_image(R.drawable.ic_file_mp4_01);
                                            } else if (cekEkstensi.equals("txt")) {
                                                item.setContent_image(R.drawable.ic_file_txt_01);
                                            } else if (cekEkstensi.equals("doc") || cekEkstensi.equals("docx")) {
                                                item.setContent_image(R.drawable.ic_file_doc_01);
                                            } else if (cekEkstensi.equals("xls") || cekEkstensi.equals("xlsx")) {
                                                item.setContent_image(R.drawable.ic_file_xlx_01);
                                            } else {
                                                item.setContent_image(R.drawable.ic_undefined_file_01);
                                            }
                                        }
                                        listData.add(item);
                                        contentAdapter.notifyDataSetChanged();
                                    }

                                } catch (JSONException e) {
                                    showToast(e + "");
                                }

                            } else {
                                String pesan = response.getString("message");
                                showToast(pesan + "");
                            }
                        } catch (JSONException e) {
                            showToast(e + "");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast(error + "");
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    public void getNamePid(final String pids) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlDirectory + "?u=" + userName + "&p=" + password + "&act=back_tree_directory&fid=" + pids, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String errorCode = response.getString("error_code");
                            if (errorCode.equals("0")) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject data = jsonArray.getJSONObject(i);
                                        String ids = data.getString("id");
                                        if (ids.matches(pids)) {
                                            lastPid = data.getString("pid");
                                            rootName = data.getString("name");
                                            break;
                                        }
                                    }

                                    // name for root
                                    rootFolder.setText(rootName);

                                    // commit ke preferences
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("pid", pid);
                                    editor.putString("lastPid", lastPid);
                                    editor.putString("rootName", rootName);
                                    editor.commit();

                                } catch (JSONException e) {
                                    showToast(e + "");
                                }

                            } else {
                                String pesan = response.getString("message");
                                showToast(pesan + "");
                            }
                        } catch (JSONException e) {
                            showToast(e + "");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast(error + "");
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    public void DeleteData(final String folderId) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlDirectory + "?u=" + userName + "&p=" + password + "&act=delete_directory&id=" + folderId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String errorCode = response.getString("error_code");
                            if (errorCode.equals("0")) {
                                showToast("Delete Success");
                                contentAdapter.notifyDataSetChanged();

                            } else {
                                showToast("Ooppss, Folder not found");
                            }
                        } catch (JSONException e) {
                            showToast(e + "");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    public void CreateContent(final String name) {
        String direktoriName;
        if (name.contains(" ")) {
            direktoriName = name.replace(" ", "%20");
        } else {
            direktoriName = name;
        }

        String urlCreateDirectory = "" + urlDirectory + "?u=" + userName + "&p=" + password + "&act=create_directory&pid=" + pid + "&dname=" + direktoriName;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(urlCreateDirectory,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject data = response.getJSONObject(i);
                                    ContentData item = new ContentData();
                                    item.setId(data.getString("id"));
                                    item.setPid(data.getString("pid"));
                                    item.setName(data.getString("name"));
                                    item.setTemplate_id(data.getString("template_id"));
                                    String cek = data.getString("template_id");
                                    if (cek.equals("5")) {
                                        item.setExt("folder");
                                        item.setContent_image(R.drawable.ic_folder_01);
                                    } else {
                                        item.setExt(data.getString("ext"));
                                        String cekEkstensi = data.getString("ext");
                                        if (cekEkstensi.equals("pdf")) {
                                            item.setContent_image(R.drawable.ic_file_pdf_01);
                                        } else if (cekEkstensi.equals("jpg") || cekEkstensi.equals("png")) {
                                            item.setContent_image(R.drawable.ic_file_jpg_01);
                                        } else if (cekEkstensi.equals("mp3")) {
                                            item.setContent_image(R.drawable.ic_file_mp3_01);
                                        } else if (cekEkstensi.equals("mp4") || cekEkstensi.equals("3gp") || cekEkstensi.equals("mkv")) {
                                            item.setContent_image(R.drawable.ic_file_mp4_01);
                                        } else if (cekEkstensi.equals("txt")) {
                                            item.setContent_image(R.drawable.ic_file_txt_01);
                                        } else if (cekEkstensi.equals("doc") || cekEkstensi.equals("docx")) {
                                            item.setContent_image(R.drawable.ic_file_doc_01);
                                        } else {
                                            item.setContent_image(R.drawable.ic_undefined_file_01);
                                        }
                                    }
                                    listData.add(item);
                                    contentAdapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    showToast(e + "");
                                }
                            }
                        } else {

                        }
                    }
                },

                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
//                        showToast(error+"");
//                        contentAdapter.notifyDataSetChanged();
                    }
                }
        );
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    public void RenameContent(final String name, final String solr) {
        String urlRename = "" + urlDirectory + "?u=" + userName + "&p=" + password + "&act=" + action + idRename + "&newname=" + name + solr;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlRename, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String errorCode = response.getString("error_code");
                            if (errorCode.equals("0")) {
//                                showToast("Rename Success");
                                contentAdapter.notifyDataSetChanged();

                            } else {
                                showToast("Ooppss, Folder not found");
                            }
                        } catch (JSONException e) {
                            showToast(e + "");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    public void UploadFile(final String fileNames, final String filePathes) {
//        Log.d("Input", fileNames);
//        Log.d("Input", filePathes);
//        Log.d("Input", urlDirectory);
//        Log.d("Input", userName);
//        Log.d("Input", password);
//        Log.d("Input", pid);

        // tambahan

        File file = new File(filePathes);
        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;

            try {
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
            } catch (java.io.IOException e) {
                showToast(e.toString());
            }

            kontenFile = total.toString();
        } catch (java.io.FileNotFoundException e) {
            showToast(e.toString());
        }
        // tambahan

        //String urlCreateDirectory = "" + urlDirectory + "?u=" + userName + "&p=" + password + "&act=upload&fname=" + fileNames + "&fpath=" + filePathes + "&pid=" + pid + "";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlDirectory, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                //Log.d("Response", response);
                //showToast(response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int err_code = jsonResponse.getInt("err_code");
                    String message = jsonResponse.getString("message");
                    String fid = "";
                    if (err_code == 0) {
                        fid = jsonResponse.getString("fid");
                    }

                    showToast(err_code + " - " + message + " - " + fid);
                    //String site = jsonResponse.getString("site"),
                    //        network = jsonResponse.getString("network");
                    //System.out.println("Site: " + site + "\nNetwork: " + network);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("upload - " + error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("filedata", kontenFile);
                params.put("u", userName);
                params.put("p", password);
                params.put("act", "upload");
                params.put("fname", fileNames);
                params.put("pid", pid);
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                params.put("Accept", "application/json");
//                params.put("name",name);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void restartActivity(String pid) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pid", pid);
        editor.commit();
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void askPermissionAndWriteFile(String response, String fileName) {
        boolean canWrite = this.askPermission(REQUEST_ID_WRITE_PERMISSION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //
        if (canWrite) {
            this.writeFile(response, fileName);
        }
    }

    private void writeFile(String data, String fileName) {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        //File root = Environment.getExternalStorageDirectory();
        File outDir = new File(root.getAbsolutePath() + File.separator);

        showToast(root.getAbsolutePath() + File.separator);

        if (!outDir.isDirectory()) {
            outDir.mkdir();
        }
        try {
            if (!outDir.isDirectory()) {
                throw new IOException(
                        "Unable to create directory EZ_time_tracker. Maybe the SD card is mounted?");
            }
            File outputFile = new File(outDir, fileName);
            Writer writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(data);
            Toast.makeText(HomeActivity.this.getApplicationContext(),
                    "Report successfully saved to: " + outputFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
            writer.close();
        } catch (IOException e) {
            Log.w("eztt", e.getMessage(), e);
            Toast.makeText(HomeActivity.this, e.getMessage() + " Unable to write to external storage.",
                    Toast.LENGTH_LONG).show();
        }
    }

//    private void writeFile(String response, String fileName) {
//        InputStream targetStream = new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));
//
//        File dst = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + fileName);
//
//        try (InputStream in = targetStream) {
//            try (OutputStream out = new FileOutputStream(dst)) {
//                // Transfer bytes from in to out
//                byte[] buf = new byte[1024];
//                int len;
//                while ((len = in.read(buf)) > 0) {
//                    out.write(buf, 0, len);
//                }
//                showToast(fileName + " donwloaded");
//            }
//        } catch (Exception e) {
//            showToast(e.toString());
//        }
//    }

//    private void writeFile(String response, String fileName) {
//        File extStore = Environment.getExternalStorageDirectory();
//        // ==> /storage/emulated/0/<fileName>
//        String path = extStore.getAbsolutePath() + "/Download/" + fileName;
//        Log.i("ExternalStorageDemo", "Save to: " + path);
//
//        String data = response;
//
//        try {
//            File myFile = new File(path);
//            myFile.createNewFile();
//            FileOutputStream fOut = new FileOutputStream(myFile);
//            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
//            myOutWriter.append(data);
//            myOutWriter.close();
//            fOut.close();
//
//            Toast.makeText(getApplicationContext(), this.fileName + " saved", Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private boolean askPermission(int requestId, String permissionName) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(this, permissionName);


            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            }
        }
        return true;
    }

    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        // Note: If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_ID_READ_PERMISSION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Read permission granted", Toast.LENGTH_SHORT).show();
                        //readFile();
                    }
                }
                case REQUEST_ID_WRITE_PERMISSION: {
                    Toast.makeText(this, "Write permission granted", Toast.LENGTH_SHORT).show();
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Write permission granted", Toast.LENGTH_SHORT).show();
                        //writeFile(response);
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permission Cancelled!", Toast.LENGTH_SHORT).show();
        }
    }

    // fungsi download baru
    private void fungsiDownload(String item_id, String fileName) {
        list.clear();

        String urlDownload = urlDirectory + "?u=" + userName + "&p=" + password + "&act=download&fid=" + item_id;
        Uri Download_Uri = Uri.parse(urlDownload);

        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle("Chanthel Downloading " + fileName);
        request.setDescription("Downloading " + fileName);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Chanthel/" + "/" + fileName);

        refid = downloadManager.enqueue(request);

        Log.e("OUT", "" + refid);

        list.add(refid);
    }


    private void fungsiPreview02(String item_id, String fileName) {
        list.clear();

        String urlDownload = urlDirectory + "?u=" + userName + "&p=" + password + "&act=preview_file_mobile&fid=" + item_id;
        Uri Download_Uri = Uri.parse(urlDownload);

        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle("Chanthel Downloading " + fileName);
        request.setDescription("Downloading " + fileName);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Chanthel/" + "/" + fileName);

        refid = downloadManager.enqueue(request);

        Log.e("OUT", "" + refid);

        list.add(refid);
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {
            ////
//            SharedPreferences downloadids = ctxt.getSharedPreferences("DownloadIDS", 0);
//            long savedDownloadIds = downloadids.getLong("savedDownloadIds", 0);

            Bundle extras = intent.getExtras();
            DownloadManager.Query q = new DownloadManager.Query();
            Long downloaded_id = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
            if (refid == downloaded_id) { // so it is my file that has been completed
                q.setFilterById(downloaded_id);

                DownloadManager manager = (DownloadManager) ctxt.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor c = manager.query(q);
                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        // do any thing here
                        if (doDisplay) {
                            displayPreview(previewName, ekstensi);
                        }
                    }
                }
                c.close();
            }
            ////

//            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//
//
//            Log.e("IN", "" + referenceId);
//
//            list.remove(referenceId);
//
//
//            if (list.isEmpty()) {
//
//
//                Log.e("INSIDE", "" + referenceId);
//                NotificationCompat.Builder mBuilder =
//                        new NotificationCompat.Builder(HomeActivity.this)
//                                .setSmallIcon(R.mipmap.ic_launcher)
//                                .setContentTitle("Chanthel")
//                                .setContentText("All Download completed");
//
//
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(455, mBuilder.build());
//
//
//            }

        }
    };

    private void fungsiUpload(String fileUri, String pidNumber) {
        // kasih progress dialog
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage("Uploading to server ...");
        progressDialog.show();

        // this will build full path of API url where we want to send data.
        //Converter factory is required in Retrofit2 there are many converters, i'm using GSON Converter.
        String urlApi = urlDirectory.replace("/chanthelAPI/index.php", "");
        Retrofit builder = new Retrofit.Builder().baseUrl(urlApi).addConverterFactory(GsonConverterFactory.create()).build();
        UploadApi api = builder.create(UploadApi.class);

        //create file which we want to send to server.
        File imageFIle = new File(fileUri);

        //request body is used to attach file.
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFIle);

        //and request body and file name using multipart.
        MultipartBody.Part image = MultipartBody.Part.createFormData("filedata", imageFIle.getName(), requestBody); //"image" is parameter for photo in API.

        // parameters
        RequestBody userName = RequestBody.create(MediaType.parse("text/plain"), this.userName);
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), this.password);
        RequestBody action = RequestBody.create(MediaType.parse("text/plain"), "upload");
        RequestBody fileName = RequestBody.create(MediaType.parse("text/plain"), imageFIle.getName());
        final RequestBody pidNumb = RequestBody.create(MediaType.parse("text/plain"), pidNumber);

        Call<UploadResponse> call = api.submitData(image, userName, password, action, fileName, pidNumb); //we will get our response in call variable.

        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, retrofit2.Response<UploadResponse> response) {
                progressDialog.dismiss();

                UploadResponse body = response.body(); //get body from response.

                AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                alert.setMessage(body.getMessage()); //display response in Alert dialog.
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartActivity(pid);
                    }
                });

                alert.show();
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void showChoice() {
        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflater = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View layout = inflater.inflate(R.layout.choice_popup, null);
        //load results
//        Button imageBtn = layout.findViewById(R.id.buttonImage);
        //Get the devices screen density to calculate correct pixel sizes
        float density = HomeActivity.this.getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        final PopupWindow pw = new PopupWindow(layout, (int) density * 240, (int) density * 285, true);

        ((Button) layout.findViewById(R.id.buttonImage)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ke image picker
                Intent intent1 = new Intent(HomeActivity.this, ImagePickActivity.class);
                intent1.putExtra(IS_NEED_CAMERA, true);
                intent1.putExtra(Constant.MAX_NUMBER, 1);
                startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);

                pw.dismiss();
            }
        });

        ((Button) layout.findViewById(R.id.buttonVideo)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = new Intent(HomeActivity.this, VideoPickActivity.class);
                intent2.putExtra(IS_NEED_CAMERA, true);
                intent2.putExtra(Constant.MAX_NUMBER, 1);
                startActivityForResult(intent2, Constant.REQUEST_CODE_PICK_VIDEO);

                pw.dismiss();
            }
        });

        ((Button) layout.findViewById(R.id.buttonAudio)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent3 = new Intent(HomeActivity.this, AudioPickActivity.class);
                intent3.putExtra(IS_NEED_RECORDER, true);
                intent3.putExtra(Constant.MAX_NUMBER, 1);
                startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO);

                pw.dismiss();
            }
        });

        ((Button) layout.findViewById(R.id.buttonOther)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent intent4 = new Intent(HomeActivity.this, NormalFilePickActivity.class);
//                intent4.putExtra(Constant.MAX_NUMBER, 9);
//                intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
//                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                final Context ctx = HomeActivity.this;
                new ChooserDialog(ctx)
//                        .withFilterRegex(false, true, ".*\\.(jpe?g|png)")
                        .withStartFile(path)
                        .withResources(R.string.title_choose_file, R.string.title_choose, R.string.dialog_cancel)
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(String paths, File pathFile) {
//                                Toast.makeText(ctx, "FILE: " + paths, Toast.LENGTH_SHORT).show();
//                                path = paths;
                                fungsiUpload(paths, pid);
//                                restartActivity(pid);
                            }
                        })
                        .withNavigateUpTo(new ChooserDialog.CanNavigateUp() {
                            @Override
                            public boolean canUpTo(File dir) {
                                return true;
                            }
                        })
                        .withNavigateTo(new ChooserDialog.CanNavigateTo() {
                            @Override
                            public boolean canNavigate(File dir) {
                                return true;
                            }
                        })
                        .build()
                        .show();

                pw.dismiss();
            }
        });


//        imageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // ke image picker
//                Intent intent1 = new Intent(MainActivity.this, ImagePickActivity.class);
//                intent1.putExtra(IS_NEED_CAMERA, true);
//                intent1.putExtra(Constant.MAX_NUMBER, 9);
//                startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
//
//                // kalo sudah selesai
//                pw.dismiss();
//            }
//        });

        //Set up touch closing outside of pop-up
        pw.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pw.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });
        pw.setOutsideTouchable(true);
        // display the pop-up in the center
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

    private void fungsiCopy(HashMap<String, String> params) {
        String urlApi = urlDirectory.replace("/chanthelAPI/index.php", "");

        Retrofit twohRetro;
        twohRetro = new Retrofit.Builder()
                .baseUrl(urlApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CopyApi apiService = twohRetro.create(CopyApi.class);
        Call<CopyResponse> result = apiService.getCopy(params);
        result.enqueue(new Callback<CopyResponse>() {
            @Override
            public void onResponse(Call<CopyResponse> call, retrofit2.Response<CopyResponse> response) {
                dialogLoading.dismiss();
                try {
                    if (response.body() != null) {
                        if (response.body().getErrorCode() == 0) {
                            showToast("File copied successfully");
                        } else {
                            showToast("Sorry, failed to copy file");
                        }
                    }
                } catch (Exception e) {
                    showToast("Sorry, failed to copy file");
//                    showToast("Error : " + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CopyResponse> call, Throwable t) {
                dialogLoading.dismiss();
                t.printStackTrace();
            }
        });
    }

    private void fungsiCut(HashMap<String, String> params) {
        String urlApi = urlDirectory.replace("/chanthelAPI/index.php", "");

        Retrofit twohRetro;
        twohRetro = new Retrofit.Builder()
                .baseUrl(urlApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CutApi apiService = twohRetro.create(CutApi.class);
        Call<CutResponse> result = apiService.getCut(params);
        result.enqueue(new Callback<CutResponse>() {
            @Override
            public void onResponse(Call<CutResponse> call, retrofit2.Response<CutResponse> response) {
                dialogLoading.dismiss();
                try {
                    if (response.body() != null) {
                        if (response.body().getErrorCode() == 0) {
                            showToast("File cut successfully");
                        } else {
                            showToast("Sorry, failed to cut file");
                        }
                    }
                } catch (Exception e) {
                    showToast("Sorry, failed to cut file");
//                    showToast("Error : " + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CutResponse> call, Throwable t) {
                dialogLoading.dismiss();
                t.printStackTrace();
            }
        });
    }

    private List<File> getListFiles(File parentDir) {
        List<File> inFiles = new ArrayList<>();
        Queue<File> files = new LinkedList<>();
        files.addAll(Arrays.asList(parentDir.listFiles()));
        while (!files.isEmpty()) {
            File file = files.remove();
            if (file.isDirectory()) {
                files.addAll(Arrays.asList(file.listFiles()));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

    private void deleteAllFiles(List<File> files) {
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
    }
}