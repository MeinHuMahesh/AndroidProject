package com.example.documentor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Login_Activity extends AppCompatActivity {
    public EditText login_email,login_pass;
    public TextView register_connector;
    public TextView forgot_pass_btn;
    public Button Login_Btn;
    private FirebaseAuth authprofile;
    private static final String TAG="Login_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_email=findViewById(R.id.Login_email);
        login_pass=findViewById(R.id.Login_password);
        Login_Btn=findViewById(R.id.Login_btn);
        register_connector=findViewById(R.id.registernowbtn);
        forgot_pass_btn=findViewById(R.id.forgot_password_btn);
        ImageView imageViewShowhidepass=findViewById(R.id.img_view_showhidepass);
        imageViewShowhidepass.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowhidepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(login_pass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If Password is visible then hide it
                    login_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Image icon
                    imageViewShowhidepass.setImageResource(R.drawable.ic_show_pwd);
                }
                else{
                    login_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowhidepass.setImageResource(R.drawable.ic_hide_pwd);
                }
            }
        });

        authprofile=FirebaseAuth.getInstance();

        register_connector.setOnClickListener(v -> {
            Intent intent =new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(intent);
        });
        Login_Btn.setOnClickListener(v -> Login());

        forgot_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),ForgotPass.class);
                startActivity(intent);
            }
        });
    }

    private void Login() {
       final String email =login_email.getText().toString().trim();
       final String password=login_pass.getText().toString().trim();
       //validation(email,password);
       if(!email.isEmpty()&&!password.isEmpty()&&Patterns.EMAIL_ADDRESS.matcher(email).matches()){
           //Toast.makeText(this, "Please enter Login Credentials", Toast.LENGTH_SHORT).show();
           loginuser(email,password);
       }
       else{

               if(email.isEmpty() ){
                   login_email.setError("Please enter email address");
                   login_email.requestFocus();
               }
               if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                   login_email.setError("Enter Valid Email Address");
                   login_email.requestFocus();
               }
               if (password.isEmpty()) {
                   login_pass.setError("Enter Password");
                   login_pass.requestFocus();
               }
       }
    }


    private void loginuser(String email, String password) {
        authprofile.signInWithEmailAndPassword(email,password).addOnCompleteListener(Login_Activity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //get instance of current user
                    FirebaseUser firebaseUser=authprofile.getCurrentUser();
                    //check if email is verified or not
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(Login_Activity.this, "Login SuccessFull", Toast.LENGTH_SHORT).show();
                        Intent intent =new Intent(Login_Activity.this,Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        firebaseUser.sendEmailVerification();
                        authprofile.signOut();//sign out
                        showAlertDailog();
                    }

                }
                else{
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        login_email.setError("Invalid Credentials Kindly, check and re-enter");
                        login_email.requestFocus();
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        login_email.setError("User Does not exists or no longer valid Please Register again");
                        login_email.requestFocus();
                    }
                    catch(Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(Login_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    private void showAlertDailog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Login_Activity.this);
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
    //check if user is already logged in if logged in take user to home page
    @Override
    protected void onStart() {
        super.onStart();
        if(authprofile.getCurrentUser()!=null){
            Toast.makeText(this, "Already Logged in", Toast.LENGTH_SHORT).show();
            Intent intent =new Intent(Login_Activity.this,Home.class);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this, "You can login now", Toast.LENGTH_SHORT).show();
        }
    }
}