package com.example.documentor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPass extends AppCompatActivity {
    private Button button_pass_reset;
    private EditText edittext_pass_reset_email;
    private ProgressBar progressBar;
    FirebaseAuth authprofile;
    private final static String TAG="ForgotPass";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        getSupportActionBar().setTitle("Forgot Password");
        button_pass_reset=findViewById(R.id.button_password_reset);
        edittext_pass_reset_email=findViewById(R.id.editText_password_reset_email);
        progressBar=findViewById(R.id.progressBar);
        button_pass_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=edittext_pass_reset_email.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    edittext_pass_reset_email.setError("Please Enter Your Registered Email For Reset");
                    edittext_pass_reset_email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edittext_pass_reset_email.setError("Enter Valid Email");
                    edittext_pass_reset_email.requestFocus();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });

    }

    private void resetPassword(String email) {
        authprofile=FirebaseAuth.getInstance();
        authprofile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPass.this, "Please Check Your Email For Password Reset Link", Toast.LENGTH_LONG).show();
                    Intent intent =new Intent(ForgotPass.this,Login_Activity.class);
                    //clear stack to avoid user returning to Forget Pass
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    try{
                        throw task.getException();

                    }
                    catch(FirebaseAuthInvalidUserException e){
                        edittext_pass_reset_email.setError("User Does Not Exist or no Longer Valid Please register Again !");
                        edittext_pass_reset_email.requestFocus();
                    }
                    catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(ForgotPass.this,e.getMessage(), Toast.LENGTH_LONG).show();

                    }

                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}