package com.example.documentor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.documentor.upload.UploadActivity;
import com.example.documentor.upload.Upload_pdf_Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class Home extends AppCompatActivity {
    private Button home_upload,home_view_user_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        home_upload=findViewById(R.id.Home_upload_Button);
        home_view_user_profile=findViewById(R.id.home_view_profile);
        FirebaseAuth authprofile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authprofile.getCurrentUser();
        runtimepermissions();
        checkIFEmailisVerified(firebaseUser);
        if(checkIFEmailisVerified(firebaseUser)==false){
            showAlertDailog();
        }
            home_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(Home.this, Upload_pdf_Activity.class);
                    startActivity(intent);

                }
            });

            home_view_user_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Home.this,UserProfileActivity.class));
                }
            });



    }

    private boolean checkIFEmailisVerified(FirebaseUser firebaseUser) {
        if(firebaseUser.isEmailVerified()){
            //Do Nothing
            return true;
        }
        else{

            return false;
        }

    }

    private void showAlertDailog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Home.this);
        builder.setTitle("Email not Verified !!!!");
        builder.setMessage("Please verify your email now ,you can not login without email verification");
        //open email app if user click continue
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent =new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//to email app in new window not within app
                startActivity(intent);
            }
        });
        //Create Alert Dailog
        AlertDialog alertDialog =builder.create();
        //show the alert box
        alertDialog.show();
    }
    private void runtimepermissions(){
        Dexter.withContext(Home.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
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
        Dexter.withContext(Home.this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
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
        Dexter.withContext(Home.this).withPermission(Manifest.permission.INTERNET).withListener(new PermissionListener() {
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