package com.example.documentor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.internal.GoogleApiManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ChangePassword_Activity extends AppCompatActivity {
    private FirebaseAuth authprofile;
    private FirebaseUser firebaseUser;
    private EditText editTextpasscurrent,editTextpassnew;
    private TextView textViewAuthenticated;
    private Button Button_change_pass,Button_reauthenticate;
    private ProgressBar Change_pass_ProgressBar;
    private ImageView showhidecurrpwd,showhidenewpass;
    private String usercurrentPass,usernewPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setTitle("Change Password");
        editTextpasscurrent=findViewById(R.id.editText_change_pwd_current);
        editTextpassnew=findViewById(R.id.editText_change_pwd_new);
        textViewAuthenticated=findViewById(R.id.textView_change_pwd_authenticated);
        Button_change_pass=findViewById(R.id.button_change_pwd);
        showhidecurrpwd=findViewById(R.id.imageView_show_hide_curr_pwd);
        showhidenewpass=findViewById(R.id.imageView_show_hide_new_pwd);
        showhidecurrpwd.setImageResource(R.drawable.ic_hide_pwd);
        showhidecurrpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextpasscurrent.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If Password is visible then hide it
                    editTextpasscurrent.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Image icon
                    showhidecurrpwd.setImageResource(R.drawable.ic_show_pwd);
                }
                else{
                    editTextpasscurrent.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showhidecurrpwd.setImageResource(R.drawable.ic_hide_pwd);
                }
            }
        });
        showhidenewpass.setImageResource(R.drawable.ic_hide_pwd);
        showhidenewpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextpasscurrent.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If Password is visible then hide it
                    editTextpassnew.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Image icon
                    showhidenewpass.setImageResource(R.drawable.ic_show_pwd);
                }
                else{
                    editTextpassnew.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showhidenewpass.setImageResource(R.drawable.ic_hide_pwd);
                }
            }
        });
        Button_reauthenticate=findViewById(R.id.button_change_pwd_authenticate);
        Change_pass_ProgressBar=findViewById(R.id.Change_pass_progressBar);
        authprofile=FirebaseAuth.getInstance();
        firebaseUser=authprofile.getCurrentUser();
        //Disable edit text for new password and button
        editTextpassnew.setEnabled(false);
        Button_change_pass.setEnabled(false);
        showhidenewpass.setEnabled(false);
        if(firebaseUser==null){
            Toast.makeText(ChangePassword_Activity.this, "Something went wrong Users details not available", Toast.LENGTH_SHORT).show();
            Intent intent =new Intent(ChangePassword_Activity.this,UserProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            reAuthenticate(firebaseUser);
        }
    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button_reauthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usercurrentPass=editTextpasscurrent.getText().toString().trim();
                if(TextUtils.isEmpty(usercurrentPass)){
                    editTextpasscurrent.setError("Enter Your Current Password");
                    editTextpasscurrent.requestFocus();
                } else {
                    Change_pass_ProgressBar.setVisibility(View.VISIBLE);
                    //ReAuthenticate user now
                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(),usercurrentPass);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Change_pass_ProgressBar.setVisibility(View.GONE);
                                //enable edittext and button for new pass
                                editTextpassnew.setEnabled(true);
                                Button_change_pass.setEnabled(true);
                                showhidenewpass.setEnabled(true);
                                //disable editText and Button for authenticate
                                editTextpasscurrent.setEnabled(false);
                                Button_reauthenticate.setEnabled(false);
                                showhidecurrpwd.setEnabled(false);
                                //display textview
                                textViewAuthenticated.setText("You are now authenticated "
                                        +"You can now Change Password");
                                Toast.makeText(ChangePassword_Activity.this, "You can Change Password Now", Toast.LENGTH_SHORT).show();
                                Button_change_pass.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ChangePassword(firebaseUser);
                                    }
                                });
                            }
                            else{
                                try{
                                    throw task.getException();
                                }
                                catch (Exception e){
                                    Toast.makeText(ChangePassword_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                            Change_pass_ProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void ChangePassword(FirebaseUser firebaseUser) {
        usernewPass=editTextpassnew.getText().toString().trim();
        if(TextUtils.isEmpty(usernewPass)){
            editTextpassnew.setError("Please Enter Your new Password");
            editTextpassnew.requestFocus();
        } else if(usernewPass.length()<8) {
            editTextpassnew.setError("Your New Password Should not be less than 8 characters");
            editTextpassnew.requestFocus();
        } else if (usernewPass.matches(usercurrentPass)) {
            editTextpassnew.setError("Your new password should not be same as old password");
            editTextpassnew.requestFocus();
        }
        else {
            Change_pass_ProgressBar.setVisibility(View.VISIBLE);
            firebaseUser.updatePassword(usernewPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChangePassword_Activity.this, "Your Password has Been Changed", Toast.LENGTH_LONG).show();
                        Intent intent =new Intent(ChangePassword_Activity.this,UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity(intent);
                        finish();
                    }else{
                        try{
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(ChangePassword_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    Change_pass_ProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}