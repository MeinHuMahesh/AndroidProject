package com.example.documentor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Delete_User_Activity extends AppCompatActivity {
    private FirebaseAuth authprofile;
    private FirebaseUser firebaseUser;
    private EditText editTextUserpwd;
    private TextView textViewAuthenticated;
    private ProgressBar delete_user_progressbar;
    private Button button_reAuthenticate,button_DeleteUser;
    private ImageView showhideimg;
    private String user_pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        getSupportActionBar().setTitle("Delete User");
        delete_user_progressbar=findViewById(R.id.Delete_user_progressBar);
        authprofile=FirebaseAuth.getInstance();
        showhideimg=findViewById(R.id.imageView_show_hide_pwd);
        showhideimg.setImageResource(R.drawable.ic_hide_pwd);
        showhideimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextUserpwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If Password is visible then hide it
                    editTextUserpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Image icon
                    showhideimg.setImageResource(R.drawable.ic_show_pwd);
                }
                else{
                    editTextUserpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showhideimg.setImageResource(R.drawable.ic_hide_pwd);
                }
            }

        });
        editTextUserpwd=findViewById(R.id.editText_delete_user_pwd);
        firebaseUser=authprofile.getCurrentUser();
        textViewAuthenticated=findViewById(R.id.textView_delete_user_authenticated);
        button_DeleteUser=findViewById(R.id.button_delete_user);
        button_reAuthenticate=findViewById(R.id.button_delete_user_authenticate);
        //Disable delete user button until user is authenticated
        button_DeleteUser.setEnabled(false);
        if(firebaseUser==null){
            Toast.makeText(Delete_User_Activity.this, "SomeThing Went Wrong !!!"+
                    "User Details are not available at the Moment", Toast.LENGTH_SHORT).show();
            Intent intent =new Intent(Delete_User_Activity.this,UserProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            reAuthenticate(firebaseUser);
        }

    }

    private void reAuthenticate(FirebaseUser firebaseUser) {

        button_reAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_pwd=editTextUserpwd.getText().toString().trim();
                if(TextUtils.isEmpty(user_pwd)){
                    editTextUserpwd.setError("Enter Your Password");
                    editTextUserpwd.requestFocus();
                } else {
                    delete_user_progressbar.setVisibility(View.VISIBLE);
                    //ReAuthenticate user now
                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(),user_pwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                delete_user_progressbar.setVisibility(View.GONE);
                                //enable button to delete user afther authentication
                                button_DeleteUser.setEnabled(true);
                                //disable editText and Button for authenticate
                                editTextUserpwd.setEnabled(false);
                                button_reAuthenticate.setEnabled(false);
                                //display textview
                                textViewAuthenticated.setText("You are now authenticated "+
                                        "You can now Delete User");
                                Toast.makeText(Delete_User_Activity.this, "You can Delete Your Profile Now Be Careful this action is irreversible !!!", Toast.LENGTH_SHORT).show();
                                button_DeleteUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAlertDailog();
                                    }
                                });
                            }
                            else{
                                try{
                                    throw task.getException();
                                }
                                catch (Exception e){
                                    Toast.makeText(Delete_User_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                            delete_user_progressbar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }
    private void showAlertDailog() {
        new AlertDialog.Builder(Delete_User_Activity.this)
                .setTitle("Do you want to Delete User ?")
                .setMessage("This Action is irreversible and all your data and files will be deleted.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser(firebaseUser);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent =new Intent(Delete_User_Activity.this,UserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).create().show();
    }

    private void deleteUser(FirebaseUser firebaseUser) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    deleteUserData();
                    authprofile.signOut();
                    Toast.makeText(Delete_User_Activity.this, "User is deleted", Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(Delete_User_Activity.this,Login_Activity.class);
                    //clear stack to avoid user returning to useractivity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    try{
                        throw task.getException();
                    }
                    catch (Exception e){
                        Toast.makeText(Delete_User_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void deleteUserData() {
        FirebaseStorage firebaseStorage =FirebaseStorage.getInstance();
        StorageReference storageReference=firebaseStorage.getReference("Uploads");
        storageReference.child(firebaseUser.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Delete_User_Activity.this, "Users Files Deleted SuccessFully", Toast.LENGTH_SHORT).show();

                }
                else{
                    try{
                        throw task.getException();
                    }
                    catch(Exception e){
                        Toast.makeText(Delete_User_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Delete_User_Activity.this, "User Data Deleted !!", Toast.LENGTH_SHORT).show();
                }
                else{
                    try{
                        throw task.getException();
                    }catch(Exception e){
                        Toast.makeText(Delete_User_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}