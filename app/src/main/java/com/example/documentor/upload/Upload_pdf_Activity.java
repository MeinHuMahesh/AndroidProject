package com.example.documentor.upload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.documentor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class Upload_pdf_Activity extends AppCompatActivity {
    private Button upload_pdf;
    private TextView textViewstatus,textView_viewUploads;
    private EditText editext_filename;
    //this is the pick pdf code used in file chooser
    private final static int PICK_PDF_CODE=2342;


    String filename;
    //the firebase objects for storage and database
    FirebaseStorage storage;
    FirebaseDatabase database;
    FirebaseAuth authprofile;
    private String userpath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);
        storage= FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();
        authprofile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authprofile.getCurrentUser();
        String path=firebaseUser.getUid();
        userpath=path;
        //link the activity and class file
        textViewstatus=findViewById(R.id.textView_status);
        textView_viewUploads=findViewById(R.id.textView_View_uploads);
        editext_filename=findViewById(R.id.editText_filename);
        textView_viewUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Upload_pdf_Activity.this,View_Pdf_Activity.class);
                intent.putExtra("databasepath",firebaseUser.getUid());
                startActivity(intent);
            }
        });
        upload_pdf=findViewById(R.id.button_upload_pdf);
        upload_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate the edit text
                filename=editext_filename.getText().toString().trim();
                if(TextUtils.isEmpty(filename)){
                    editext_filename.setError("Enter File Name");
                    editext_filename.requestFocus();
                }
                else{
                        getPDF();
                }
            }
        });
    }
    //this method will get the pdf from the storage
    private void getPDF() {

        //Check If permissions are allowed
        //if the permission is not available user will
        if(ContextCompat.checkSelfPermission(Upload_pdf_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(Upload_pdf_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
        else{
            ActivityCompat.requestPermissions(Upload_pdf_Activity.this,new String []{Manifest.permission.READ_EXTERNAL_STORAGE},9);
        }
        //create a intent to select pdf file from file chooser
        Intent intent =new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select File"),PICK_PDF_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user chooses a file
        if (requestCode==PICK_PDF_CODE && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
        if(data.getData()!=null){
                UploadFile(data.getData());
        }
        else{
            Toast.makeText(this, "No File Chosen", Toast.LENGTH_SHORT).show();
        }
    }
}
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            Toast.makeText(Upload_pdf_Activity.this, "Please Provide Permission", Toast.LENGTH_SHORT).show();
        }
    }
    private void UploadFile(Uri data) {
        String filename=editext_filename.getText().toString().trim()+".pdf";
        StorageReference sref=storage.getReference("Uploads/"+userpath+"/").child(filename);
        sref.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        textViewstatus.setText("File Uploaded SuccessFully");
                        String databasepath="Uploads/"+userpath+"/";
                        DatabaseReference mDatabaseReference=database.getReference(databasepath);
                        Task<Uri> uri =taskSnapshot.getStorage().getDownloadUrl();
                        while(!uri.isComplete());
                        String filename=editext_filename.getText().toString().trim();
                        Uri url=uri.getResult();
                        UploadPdf uploadPdf=new UploadPdf(filename,url.toString());
                        mDatabaseReference.child(filename).setValue(uploadPdf).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Upload_pdf_Activity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    try{
                                        throw task.getException();
                                    }
                                    catch(Exception ex){
                                        Toast.makeText(Upload_pdf_Activity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Upload_pdf_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress =(100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        textViewstatus.setText((int)progress+"% Uploading");
                    }
                });

    }
}