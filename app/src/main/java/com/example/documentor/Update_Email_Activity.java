package com.example.documentor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Update_Email_Activity extends AppCompatActivity {
    private FirebaseAuth authprofile;
    private FirebaseUser firebaseUser;
    private ProgressBar Update_email_progressbar;
    private TextView textViewAuthenticated,textViewOldEmail;
    private String userOldEmail,userNewEmail,userPassword;
    private Button Button_update_email,Button_authenticate;
    private EditText editeTextNewEmail,editTextPassword;
    private ImageView showhidepassImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        getSupportActionBar().setTitle("Update Email");
        //Firebase auth object
        authprofile=FirebaseAuth.getInstance();
        //progressbar
        Update_email_progressbar=findViewById(R.id.updateemail_progressBar);
        //Firebase user object
        firebaseUser=authprofile.getCurrentUser();
        //
        textViewOldEmail=findViewById(R.id.textView_update_email_old);
        textViewAuthenticated=findViewById(R.id.textView_update_email_authenticated);
        Button_update_email=findViewById(R.id.button_update_email);
        Button_authenticate=findViewById(R.id.button_authenticate_user);
        editeTextNewEmail=findViewById(R.id.editText_update_email_new);
        editTextPassword=findViewById(R.id.editText_update_email_verify_password);
        Button_update_email.setEnabled(false);//Make button Disabled in the beginning until user is authenticated
        editeTextNewEmail.setEnabled(false);// disable this until user is authenticated
        userOldEmail=firebaseUser.getEmail();
        showhidepassImg=findViewById(R.id.imageView_show_hide_pwd);
        showhidepassImg.setImageResource(R.drawable.ic_hide_pwd);
        showhidepassImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If Password is visible then hide it
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Image icon
                    showhidepassImg.setImageResource(R.drawable.ic_show_pwd);
                }
                else{
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showhidepassImg.setImageResource(R.drawable.ic_hide_pwd);
                }
            }

        });
        textViewOldEmail.setText(userOldEmail);
        if(firebaseUser==null){
            Toast.makeText(this, "Something Went Wrong ! User Details not availabe", Toast.LENGTH_SHORT).show();
        }
        else{
            reAuthenticate(firebaseUser);
        }


    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button_authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get password for authentication
                userPassword=editTextPassword.getText().toString().trim();
                if(TextUtils.isEmpty(userPassword)){
                    editTextPassword.setError("Enter Password for Authtication");
                    editTextPassword.requestFocus();
                }else{
                    Update_email_progressbar.setVisibility(View.VISIBLE);
                    AuthCredential credential= EmailAuthProvider.getCredential(userOldEmail,userPassword);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               Update_email_progressbar.setVisibility(View.GONE);
                               Toast.makeText(Update_Email_Activity.this, "Your PassWord is Verified", Toast.LENGTH_SHORT).show();
                               // set text View to show user is authenticate
                               textViewAuthenticated.setText("You are authenticated . You can now update your email now !!");
                               //Disable editText for password and enable edittext for new email and update email button
                               editTextPassword.setEnabled(false);
                               showhidepassImg.setEnabled(false);
                               Button_authenticate.setEnabled(false);
                               editeTextNewEmail.setEnabled(true);
                               Button_update_email.setEnabled(true);
                               Button_update_email.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       userNewEmail=editeTextNewEmail.getText().toString().trim();
                                       if(validation()==true){
                                           Update_email_progressbar.setVisibility(View.VISIBLE);
                                           updateemail(firebaseUser);
                                       }
                                       else{
                                           Toast.makeText(Update_Email_Activity.this, "Please Enter Valid Email Address", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });
                           }
                           else {
                               try{
                                   throw task.getException();
                               }catch (Exception e){
                                   Toast.makeText(Update_Email_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                               }
                           }
                           Update_email_progressbar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }
    private boolean validation() {
        if (TextUtils.isEmpty(userNewEmail)) {
            editeTextNewEmail.setError("Enter new Email");
            editeTextNewEmail.requestFocus();
            return false;
        } if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
            editeTextNewEmail.setError("Enter Valid new Email");
            editeTextNewEmail.requestFocus();
            return false;
        } if (userNewEmail.matches(userOldEmail)) {
            editeTextNewEmail.setError("New Email cannot be same as old email.Please enter new email");
            editeTextNewEmail.requestFocus();
            return false;
        }
        return true;
    }
    private void updateemail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(Update_Email_Activity.this, "Your Email has been Updated.Please Verify new email", Toast.LENGTH_LONG).show();
                    Intent intent =new Intent(Update_Email_Activity.this,UserProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity(intent);
                    finish();
                }
                else{
                    try{
                        throw task.getException();
                    }
                    catch (Exception e){
                        Toast.makeText(Update_Email_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                Update_email_progressbar.setVisibility(View.GONE);
            }
        });
    }
}