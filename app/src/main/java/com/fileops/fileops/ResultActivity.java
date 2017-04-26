package com.fileops.fileops;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adapters.FileAdapter;

public class ResultActivity extends AppCompatActivity {

    File incomingFile;
    List<List<Map.Entry<String, Integer>>> mainData = new ArrayList<List<Map.Entry<String, Integer>>>();
    RecyclerView recyclerview;
    LinearLayoutManager layoutmanager;
    FileAdapter adapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        layoutmanager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        incomingFile = (File) getIntent().getExtras().get("data");
        progressBar.setVisibility(View.VISIBLE);
        readFile(incomingFile);
        
    }

    private void readFile(File incomingFile) {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(incomingFile));
            String line;

            while ((line = br.readLine()) != null) {
                String [] word = line.split("\\s+");
                for (String w : word) {
                    text.append(w +" ");
                }
            }
            br.close();
            new AnalyseFile().execute(text.toString());

        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        System.out.println("FILE :"+ text);
    }

    private void CountWords(String text) {
        HashMap<String, Integer> data = new HashMap<>();
        for (int i=0;i<text.split(" ").length;i++){
            String item = removeSpecialChars(text.split(" ")[i]);
            if (data.containsKey(item)){
                int index = data.get(item);
                data.put(item, index+1);
            }
            else {
                data.put(item, 1);
            }
        }

        Set<Map.Entry<String, Integer>> set = data.entrySet();
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(set);
        Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );
        int temp = 1;
        List<Map.Entry<String, Integer>> tempList = new ArrayList<>();
        Collections.reverse(list);
        for(Map.Entry<String, Integer> entry:list){
            if (entry.getValue() < temp*10){
                tempList.add(entry);
            }
            else {
                mainData.add(temp-1,tempList);
                tempList = new ArrayList<>();
                temp += 1;
                tempList.add(entry);
            }
        }
        if (!tempList.isEmpty()){
            mainData.add(temp-1,tempList);
        }

    }

    private String removeSpecialChars(String c){
        Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
        Matcher match= pt.matcher(c);
        while(match.find())
        {
            String s= match.group();
            c=c.replaceAll("\\"+s, "");
        }
        return c;
    }

    class AnalyseFile extends AsyncTask<String, Void, List<List<Map.Entry<String, Integer>>>>{

        @Override
        protected List<List<Map.Entry<String, Integer>>> doInBackground(String... params) {
            CountWords(params[0]);
            return mainData;
        }

        @Override
        protected void onPostExecute(List<List<Map.Entry<String, Integer>>> lists) {
            super.onPostExecute(lists);
            System.out.println("MAINN : "+mainData.size());
            updateData(mainData);
        }
    }

    private void updateData(List<List<Map.Entry<String, Integer>>> mainData) {
        adapter = new FileAdapter(mainData,this);
        recyclerview.setLayoutManager(layoutmanager);
        recyclerview.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
