package com.example.documentor.upload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.documentor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FetchUploadActivity extends AppCompatActivity {
    private ListView myPdfListView;
    DatabaseReference databaseReference;
    List<UploadPdf>  upload_pdfList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("View Uploads");
        setContentView(R.layout.activity_fetch_upload);
        myPdfListView=findViewById(R.id.view_upload_list);
        upload_pdfList=new ArrayList<>();
        viewAllpdf();
        myPdfListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UploadPdf uploadPdf=upload_pdfList.get(position);
                Intent intent=new Intent();
                intent.setType(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uploadPdf.getUrl()));
                startActivity(intent);
            }
        });
    }

    private void viewAllpdf() {
        databaseReference= FirebaseDatabase.getInstance().getReference("uploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postsnapshot:snapshot.getChildren()){
                    UploadPdf uploadPdf =postsnapshot.getValue(UploadPdf.class);
                    upload_pdfList.add(uploadPdf);
                }
                String [] uploads =new String[upload_pdfList.size()];
                for(int i=0;i<uploads.length;i++){
                    uploads[i]=upload_pdfList.get(i).getName();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(FetchUploadActivity.this, android.R.layout.simple_list_item_1,uploads);
                myPdfListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}