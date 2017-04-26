package com.fileops.fileops;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class HomePage extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 100;
    File parentDir;
    Button select;
    List<File> data = new ArrayList<>();
    LinearLayout treeLayout;
    ProgressBar progressBar;
    private List<File> innerdata = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        select = (Button) findViewById(R.id.select);
        treeLayout = (LinearLayout) findViewById(R.id.treeLayout);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        parentDir = new File(Environment.getExternalStorageDirectory().getPath());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant

                return;
            }
        }

    }


    public void openFileTree(View view) {
        select.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new FileOperation().execute();
    }


    private class FileOperation extends AsyncTask<Void, Void, List<File>> {

        @Override
        protected List<File> doInBackground(Void... params) {
            getFiles(parentDir);
            return data;
        }

        @Override
        protected void onPostExecute(List<File> fileList) {
            super.onPostExecute(fileList);
            DisplayTree();
        }
    }

    private void DisplayTree() {
        progressBar.setVisibility(View.GONE);
        treeLayout.setVisibility(View.VISIBLE);
        treeLayout.removeAllViews();
        for (int i = 0; i < data.size(); i++) {
            View child = getLayoutInflater().inflate(R.layout.tree_item, null);
            TextView filename = (TextView) child.findViewById(R.id.fileitem);
            filename.setText(data.get(i).getName());
            final int finalI = i;
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.get(finalI).isDirectory()) {
                        innerdata.clear();
                        ShowInnerFiles(data.get(finalI));
                    } else {
                        Intent filelistview = new Intent(HomePage.this, ResultActivity.class);
                        filelistview.putExtra("data", data.get(finalI));
                        startActivity(filelistview);
                    }
                }
            });

            treeLayout.addView(child);
        }
    }


    public void getFiles(File f) {
        if (f.exists() && f.canRead()) {
            File[] list = f.listFiles();
            for (File file : list) {
                if (file.isDirectory() && Character.isUpperCase(file.getName().charAt(0))) {
                    data.add(file);
//                    getFiles(file);
                } else {
                    if (file.getName().endsWith(".txt")) {
                        data.add(file);
                    }
                }
            }
        }
    }


    private void ShowInnerFiles(File file) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.inner_items, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(true);

        TextView heading = (TextView) promptView.findViewById(R.id.innerHeading);
        TextView emptymessage = (TextView) promptView.findViewById(R.id.emptymessage);
        final ProgressBar progressBar = (ProgressBar) promptView.findViewById(R.id.innerprogress);
        final LinearLayout linearLayout = (LinearLayout) promptView.findViewById(R.id.innerLayout);
        progressBar.setVisibility(View.VISIBLE);
        heading.setText(file.getName());
        final List<File> innerdata = getInnerFiles(file);
        if (innerdata.size() == 0) {
            progressBar.setVisibility(View.GONE);
            emptymessage.setVisibility(View.VISIBLE);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                linearLayout.removeAllViews();
                for (int i = 0; i < innerdata.size(); i++) {
                    View child = getLayoutInflater().inflate(R.layout.tree_item, null);
                    TextView filename = (TextView) child.findViewById(R.id.fileitem);
                    filename.setText(innerdata.get(i).getName());
                    final int finalI = i;
                    child.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent filelistview = new Intent(HomePage.this, ResultActivity.class);
                            filelistview.putExtra("data", innerdata.get(finalI));
                            startActivity(filelistview);
                        }
                    });

                    linearLayout.addView(child);
                }
            }
        }, 500);
// create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    private List<File> getInnerFiles(File f) {

        if (f.exists() && f.canRead()) {
            File[] list = f.listFiles();
            for (File file : list) {
                if (file.isDirectory()) {
                    getFiles(file);
                } else {
                    if (file.getName().endsWith(".txt")) {
                        System.out.println("nnnn : " + file.getName());
                        innerdata.add(file);
                    }
                }
            }
        }
        return innerdata;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "App won't work if you didn't allow", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_STORAGE);
                    }

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
