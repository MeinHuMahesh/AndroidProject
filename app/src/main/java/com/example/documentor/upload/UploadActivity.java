package com.example.documentor.upload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.documentor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadActivity extends AppCompatActivity {

    private Button select,upload,fetch;
    private TextView notification;
    private ProgressDialog uploadprogressDialog;



    Uri pdfuri;//uri are actually urls that are meant for local storage

    FirebaseStorage storage;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload);
        select =findViewById(R.id.selectbutton);
        upload=findViewById(R.id.uploadbutton);
        notification=findViewById(R.id.textview1);
        fetch=findViewById(R.id.viewuploads);
        storage=FirebaseStorage.getInstance();//returns the object of firebase storage
        database=FirebaseDatabase.getInstance();//return the object of firebase database
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)){
                    selectpdf();
                }
                else{
                    ActivityCompat.requestPermissions(UploadActivity.this,new String []{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }

            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pdfuri!=null){//user has selected the file
                    uplaodpdf(pdfuri);
                }
                else{
                    Toast.makeText(UploadActivity.this, "Select a File", Toast.LENGTH_SHORT).show();
                }

            }
        });
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadActivity.this,FetchUploadActivity.class));
            }
        });
    }



    private void uplaodpdf(Uri pdfuri) {
        uploadprogressDialog=new ProgressDialog(UploadActivity.this);
        uploadprogressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadprogressDialog.setTitle("Uploading File....");
        uploadprogressDialog.setProgress(0);
        uploadprogressDialog.show();
        final String filename1=System.currentTimeMillis()+"";
        final String filename=System.currentTimeMillis()+".pdf";//set the file name
        StorageReference storageReference=storage.getReference();// returns root path
        storageReference.child("Uploads").child(filename).putFile(pdfuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String url=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();//return the url of uploaded file
                        //store the url in real time database
                        DatabaseReference databaseReference=database.getReference("Uploads");//return the root path
                        databaseReference.child(filename1).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(UploadActivity.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(UploadActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadActivity.this, "File not uploaded SuccessFully", Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        //to show progress dailog to user
                        int currentprogress= (int) (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        uploadprogressDialog.setProgress(currentprogress);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectpdf();
        } else {
            Toast.makeText(this, "Please Provide Permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectpdf() {
        //select a file using File manager
         Intent intent =new Intent();
         intent.setType("application/pdf");
         intent.setAction(Intent.ACTION_GET_CONTENT);
         startActivityForResult(intent,86);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check whether user has selected File
        if(requestCode==86&&resultCode==RESULT_OK&&data!=null){
            pdfuri =data.getData();//return the uri of selected file
            notification.setText("File Selected :"+data.getData().getLastPathSegment());
        }
        else{
            Toast.makeText(this, "Please Select The File", Toast.LENGTH_SHORT).show();
        }
    }
}