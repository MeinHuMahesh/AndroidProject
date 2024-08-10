package com.example.documentor.upload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.documentor.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewSingle_Pdf extends AppCompatActivity {
    public String url;
    private String name;
    private PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_pdf);
        getSupportActionBar().setTitle("View PDF");
        url=getIntent().getStringExtra("pdfurl");
        name=getIntent().getStringExtra("pdfname");
        StrictMode.VmPolicy.Builder builder=new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        pdfView=findViewById(R.id.pdfView);
        new pdfdownload().execute(url);
    }
    private class pdfdownload extends AsyncTask<String,Void, InputStream>{

        @Override
        protected InputStream doInBackground(String... strings) {
             InputStream inputStream =null;
            try {
                URL Url =new URL(strings[0]);
                HttpURLConnection httpURLConnection=(HttpURLConnection)Url.openConnection();
                if(httpURLConnection.getResponseCode()==200){
                     inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return inputStream;
        }


        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
        }
    }
    //Creating Action Bar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pdf_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //when any menu item is selected

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        switch (id){
            case R.id.pdf_download:
                //Download the file
                Intent intent =new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "SomeThing Went Wrong", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

}