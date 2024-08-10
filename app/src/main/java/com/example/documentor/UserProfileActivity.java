package com.example.documentor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private TextView textviewWelcome,textviewName,textviewemail,textviewroll,textviewgender,textviewyear,textviewstream,textviewphone;
    private ProgressBar progressBar;
    private String name,email,roll,phone,gender,year,stream;
    private ImageView imageview_profile_pic;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle("User Profile");
        textviewWelcome=findViewById(R.id.textview_welcome_text);
        textviewemail=findViewById(R.id.textview_email);
        textviewName=findViewById(R.id.textview_fullname);
        textviewroll=findViewById(R.id.textview_roll);
        textviewgender=findViewById(R.id.textview_gender);
        textviewyear=findViewById(R.id.textview_year);
        textviewphone=findViewById(R.id.textview_phone);
        textviewstream=findViewById(R.id.textview_stream);

        authProfile= FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        if(firebaseUser!=null){
            showuserprofile(firebaseUser);
        }
        else{
            Toast.makeText(UserProfileActivity.this, "SomeThing went wrong User Details are not available at the moment", Toast.LENGTH_SHORT).show();
        }

    }

    private void showuserprofile(FirebaseUser firebaseUser) {
        String UserID =firebaseUser.getUid();
        //Extracting User reference from database for registered user
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readuserdetails =snapshot.getValue(ReadWriteUserDetails.class);
                if(readuserdetails!=null){
                    //get data from user database
                    name=firebaseUser.getDisplayName();
                    email=firebaseUser.getEmail();
                    phone= readuserdetails.phone;
                    roll= readuserdetails.roll;
                    gender=readuserdetails.gender;
                    year=readuserdetails.year;
                    stream= readuserdetails.stream;
                    //show data in text view
                    textviewWelcome.setText("Welocome "+name+"!");
                    textviewemail.setText(email);
                    textviewgender.setText(gender);
                    textviewphone.setText(phone);
                    textviewroll.setText(roll);
                    textviewstream.setText(stream);
                    textviewName.setText(name);
                    textviewyear.setText(year);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "SomeThing went wrong ", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Creating Action Bar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //when any menu item is selected

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        switch (id){
            case R.id.menu_refresh:
                //Refresh Activity
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                break;
            case R.id.menu_log_out:
                authProfile.signOut();
                Toast.makeText(UserProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(UserProfileActivity.this,Login_Activity.class);
                //clear stack to avoid user returning to useractivity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_update_profile:
                Intent intent1=new Intent(UserProfileActivity.this,UpadateProfile_Activity.class);
                startActivity(intent1);
                break;
            case R.id.menu_update_email:
                Intent intent2=new Intent(UserProfileActivity.this,Update_Email_Activity.class);
                startActivity(intent2);
                break;
            case R.id.menu_change_password:
                Intent intent3=new Intent(UserProfileActivity.this,ChangePassword_Activity.class);
                startActivity(intent3);
                break;
            case R.id.menu_delete_user:
                Intent intent4=new Intent(UserProfileActivity.this,Delete_User_Activity.class);
                startActivity(intent4);
                break;
            default:
                Toast.makeText(this, "SomeThing Went Wrong", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}