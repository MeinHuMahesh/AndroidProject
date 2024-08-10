package com.example.documentor.upload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.documentor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

public class View_Pdf_Activity extends AppCompatActivity {
    private ListView upload_view_list;
    FirebaseDatabase database;
    List<UploadPdf> uploads_list;
    private TextView textViewStatus;
    ArrayAdapter<String> adapter;
    DatabaseReference mDatabaseReference;
    private FirebaseAuth authprofile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private static final int PERMISSION_STORAGE_CODE=1000;
    private static final int PERMISSION_INTERNET_CODE=102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        getSupportActionBar().setTitle("Files");
        progressBar=findViewById(R.id.progressbar_viewpdf);
        textViewStatus=findViewById(R.id.textViewStatus);
        progressBar.setVisibility(View.VISIBLE);
        authprofile=FirebaseAuth.getInstance();
        firebaseUser=authprofile.getCurrentUser();
        uploads_list=new ArrayList<>();
        database=FirebaseDatabase.getInstance();
        upload_view_list=findViewById(R.id.listView_uploads);
        viewAllpdf();
        if(uploads_list.isEmpty()){
            textViewStatus.setText("Upload Files to view");
        }
        progressBar.setVisibility(View.GONE);
        if(ContextCompat.checkSelfPermission(View_Pdf_Activity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(View_Pdf_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
            &&ContextCompat.checkSelfPermission(View_Pdf_Activity.this, Manifest.permission.INTERNET)==PackageManager.PERMISSION_GRANTED)
        {
            viewpdf();
        }
        else {
            Dexter.withContext(View_Pdf_Activity.this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();
            Dexter.withContext(View_Pdf_Activity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();
            Dexter.withContext(View_Pdf_Activity.this).withPermission(Manifest.permission.INTERNET).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();
        }

    }

    private void viewpdf() {
        upload_view_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                        UploadPdf uploadPdf = uploads_list.get(i);
                        //startDownloading(uploadPdf);
                        Intent intent = new Intent(View_Pdf_Activity.this,ViewSingle_Pdf.class);
                        //intent.setAction(Intent.ACTION_VIEW);
                        intent.putExtra("pdfurl",uploadPdf.getUrl());
                        intent.putExtra("pdfname",uploadPdf.getName());
                        //.setData(Uri.parse(uploadPdf.getUrl()));
                        startActivity(intent);
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    viewpdf();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            case PERMISSION_INTERNET_CODE:
                if(grantResults.length>0&&grantResults[1]==PackageManager.PERMISSION_GRANTED){
                    viewpdf();
                }else{
                    Toast.makeText(View_Pdf_Activity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }

    }

    private void viewAllpdf() {
        String databasepath=getIntent().getStringExtra("databasepath");
        mDatabaseReference=database.getReference("Uploads/"+databasepath+"/");//get firebase uid
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postsnapshot:snapshot.getChildren()){
                    UploadPdf uploadPdf =postsnapshot.getValue(UploadPdf.class);
                    uploads_list.add(uploadPdf);
                }
                String [] uploads =new String[uploads_list.size()];
                for(int i=0;i<uploads.length;i++){
                    uploads[i]=uploads_list.get(i).getName();
                }
                adapter = new ArrayAdapter<String>(View_Pdf_Activity.this, android.R.layout.simple_list_item_1,uploads);
                upload_view_list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //long press to delete file
        upload_view_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(View_Pdf_Activity.this)
                        .setTitle("Do You Want to remove "+uploads_list.get(position).getName()+" from List ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UploadPdf deletefile=uploads_list.get(position);

                                StorageReference storageReference= FirebaseStorage.getInstance().getReference("Uploads/"+firebaseUser.getUid()+"/"+deletefile.getName()+".pdf");
                                storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Uploads/"+databasepath+"/"+deletefile.getName());
                                            databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(View_Pdf_Activity.this, "File Deleted SuccessFully", Toast.LENGTH_SHORT).show();

                                                    }
                                                    else{
                                                        try {
                                                            task.getException();
                                                        }catch (Exception e){
                                                            Toast.makeText(View_Pdf_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();}
                                                    }}
                                            });
                                        }
                                        else{
                                            try{
                                                task.getException();
                                            }catch (Exception e){
                                                Toast.makeText(View_Pdf_Activity.this, "", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                });
                                uploads_list.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                return true;
            }
        });
    }
}