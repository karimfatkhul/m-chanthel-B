package com.solusi247.fatkhul.chanthelbeta.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.solusi247.fatkhul.chanthelbeta.R;
import com.solusi247.fatkhul.chanthelbeta.adapter.ContentAdapter;
import com.solusi247.fatkhul.chanthelbeta.data.ContentData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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

    private static final int REQUEST_ID_READ_PERMISSION = 100;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;

    private DownloadManager downloadManager;
    ArrayList<Long> list = new ArrayList<>();
    private long refid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

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
        pid = preferences.getString("pid", "");
        lastPid = pid;
        rootName = preferences.getString("rootname", "");
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
                            String foldernames = listData.get(position).getName();
                            pid = cek.toString();
                            lastPid = cek.toString();
                            rootFolder.setText(foldernames);
                            listData.clear();
                            GetData(pid);
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
                        LinearLayout copy = (LinearLayout) bottomSheetDialogMoreOption.findViewById(R.id.copy);
                        LinearLayout paste = (LinearLayout) bottomSheetDialogMoreOption.findViewById(R.id.paste);
                        final LinearLayout rename = (LinearLayout) bottomSheetDialogMoreOption.findViewById(R.id.rename);
                        LinearLayout delete = (LinearLayout) bottomSheetDialogMoreOption.findViewById(R.id.delete);
                        //LinearLayout preview = (LinearLayout) bottomSheetDialogMoreOption.findViewById(R.id.preview);

                        // tambah download code start
                        LinearLayout download = bottomDialogView.findViewById(R.id.download);
                        LinearLayout preview = bottomDialogView.findViewById(R.id.preview);

                        preview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String template_id;
                                final String item_id;
                                template_id = listData.get(position).getTemplate_id();
                                item_id = listData.get(position).getId();
                                if (template_id.matches("6")) {
                                    fileName = listData.get(position).getName();
                                    getLinkPreview(item_id, fileName);
                                    bottomSheetDialogMoreOption.hide();
//                                    final AlertDialog.Builder builderDownload = new AlertDialog.Builder(HomeActivity.this);
//                                    builderDownload.setMessage("Are you sure you want to preview this file ?")
//                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                    fungsiDownload(item_id, fileName);
//                                                }
//                                            }).setNegativeButton("Cancel", null);
//                                    AlertDialog alert = builderDownload.create();
//                                    alert.show();
//                                    bottomSheetDialogMoreOption.hide();
                                }
                            }
                        });

                        download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
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
                        // tambah download code end


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
                                                        action = "rename_file&fid=";
                                                        messej = "File has been renamed";
                                                        listData.clear();
                                                        RenameContent(folderName, "");
                                                        contentAdapter.notifyItemChanged(position);
                                                        contentAdapter.notifyDataSetChanged();
                                                        showToast(messej);
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
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);

                }

                if (checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    showToast("write permission ok");
                }

                new MaterialFilePicker()
                        .withActivity(HomeActivity.this)
                        .withRequestCode(1000)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();

                UploadFile(fileName, path);
                bottomSheetDialog.hide();
            }
        });

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (!askPermission(REQUEST_ID_WRITE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        }
    }

    private void getLinkPreview(final String item_id, final String fileName) {
        String apiPreview = urlDirectory + "?u=" + userName + "&p=" + password + "&act=preview_file&fid=" + item_id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiPreview, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("error_code") == 3) {
                        showToast("sorry, something wrong");
                    } else {
                        String urlPReview = response.getString("url");
                        fungsiPreview(urlPReview, fileName);
                    }
                } catch (org.json.JSONException err) {
                    showToast(err.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast(error.toString());
            }
        }
        );
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void fungsiPreview(String url_Preview, String fileName) {
        list.clear();

        Uri Download_Uri = Uri.parse(url_Preview.replaceFirst("http://yava-228.solusi247.com", "http://192.168.1.228"));

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
        if (lastPid.equals("1")) {
            if (doubleBackToExitPressedOnce) {
                // broadcast logout signal to all activity
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.package.ACTION_LOGOUT");
                sendBroadcast(broadcastIntent);
                // broadcast logout signal to all activity

                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(i);
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
            GetLastData(lastPid);
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
    public void GetData(String pids) {
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
        String urlCreateDirectory = "" + urlDirectory + "?u=" + userName + "&p=" + password + "&act=create_directory&pid=" + pid + "&dname=" + name;
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

    BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {


            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);


            Log.e("IN", "" + referenceId);

            list.remove(referenceId);


            if (list.isEmpty()) {


                Log.e("INSIDE", "" + referenceId);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(HomeActivity.this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Chanthel")
                                .setContentText("All Download completed");


                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(455, mBuilder.build());


            }

        }
    };
}