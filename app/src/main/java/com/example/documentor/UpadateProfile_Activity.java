package com.example.documentor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpadateProfile_Activity extends AppCompatActivity {
    private TextView update_profile_header;
    private EditText update_profile_name,update_profile_roll,update_profile_phone;
    private Spinner update_profile_gender,update_profile_year,update_profile_stream;
    private Button update_profile_button;
    private String textname,textroll,textphone,textgender,textyear,textstream;
    private ProgressBar update_profile_progressBar;
    private FirebaseAuth authprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upadate_profile);
        update_profile_header=findViewById(R.id.textView_updateprofile_head);
        update_profile_name=findViewById(R.id.edittext_updateprofile_name);
        update_profile_roll=findViewById(R.id.edittext_updateProfile_roll);
        update_profile_phone=findViewById(R.id.editText_updateProfile_Phone);
        update_profile_gender=findViewById(R.id.spinner_updateprofile_gender);
        update_profile_stream=findViewById(R.id.spinner_updateprofile_stream);
        update_profile_year=findViewById(R.id.spinner_updateprofile_year);
        update_profile_button=findViewById(R.id.update_profile_submit);
        update_profile_progressBar=findViewById(R.id.updateProfile_progressbar);
        getSupportActionBar().setTitle("Update Profile Details");
        genderspinner();
        yearspinner();
        streamspinner();
        authprofile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authprofile.getCurrentUser();
        //show the profile data
        showProfile(firebaseUser);
        update_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();//validate the edittext and spinner
                if(validation()==false){
                    Toast.makeText(UpadateProfile_Activity.this, "Please fill Details", Toast.LENGTH_SHORT).show();
                }else{
                    updateProfile(firebaseUser);
                }


            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {

        // get the data entered by user
        textname=update_profile_name.getText().toString().trim();
        textroll=update_profile_roll.getText().toString().trim();
        textphone=update_profile_phone.getText().toString().trim();
        textgender=update_profile_gender.getSelectedItem().toString();
        textstream=update_profile_stream.getSelectedItem().toString();
        textyear=update_profile_year.getSelectedItem().toString();

        //Enter user data into the Firebase real time database
        ReadWriteUserDetails writeUserDetails=new ReadWriteUserDetails();
        writeUserDetails.setEmail(firebaseUser.getEmail());
        writeUserDetails.setRoll(textroll);
        writeUserDetails.setPhone(textphone);
        writeUserDetails.setGender(textgender);
        writeUserDetails.setStream(textstream);
        writeUserDetails.setYear(textyear);

        //Extract user reference from database for registered users
        DatabaseReference referenceProfile =FirebaseDatabase.getInstance().getReference("Registered Users");
        String userID=firebaseUser.getUid();

        update_profile_progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //setting new display name
                    UserProfileChangeRequest updateprofile =new UserProfileChangeRequest.Builder()
                            .setDisplayName(textname).build();
                    firebaseUser.updateProfile(updateprofile);
                    Toast.makeText(UpadateProfile_Activity.this, "Profile Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                    //Stop user from returning to update profile activity
                    Intent intent =new Intent(UpadateProfile_Activity.this,UserProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity(intent);
                    finish();
                }
                else{
                    try{
                        throw task.getException();
                    }
                    catch (Exception e){
                        Toast.makeText(UpadateProfile_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                update_profile_progressBar.setVisibility(View.GONE);
            }
        });
    }

    //fetch data from firebase realtime database and display
    private void showProfile(FirebaseUser firebaseUser) {
        String UserID=firebaseUser.getUid();
        //Extreacting User Reference from Database for "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        update_profile_progressBar.setVisibility(View.VISIBLE);
        referenceProfile.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails =snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails!=null){
                    // save the user data into string
                    textname=firebaseUser.getDisplayName();

                    textroll= readUserDetails.getRoll();
                    textgender=readUserDetails.getGender();
                    textstream=readUserDetails.getStream();
                    textphone=readUserDetails.getPhone();
                    textyear=readUserDetails.getYear();
                    //display it
                    update_profile_name.setText(textname);

                    update_profile_phone.setText(textphone);
                    update_profile_roll.setText(textroll);
                    //for gender
                    if(textgender.equals("Male")){
                        update_profile_gender.setSelection(1);
                    }
                    else if(textgender.equals("Female")){
                        update_profile_gender.setSelection(2);
                    }
                    else if (textgender.equals("others")) {
                        update_profile_gender.setSelection(3);
                    }
                    else{
                        update_profile_name.setError("Please Select Gender");
                        update_profile_name.requestFocus();
                    }
                    //for year
                    if(textyear.equals("FY")){
                        update_profile_year.setSelection(1);
                    }
                    else if(textyear.equals("SY")){
                        update_profile_year.setSelection(2);
                    }
                    else if(textyear.equals("TY")){
                        update_profile_year.setSelection(3);
                    }
                    else{
                        update_profile_name.setError("Please Select Year");
                        update_profile_name.requestFocus();
                    }
                    //for stream
                    if(textstream.equals("BSc IT")){
                        update_profile_stream.setSelection(1);
                    }
                    else if(textstream.equals("BAF")){
                        update_profile_stream.setSelection(2);
                    }
                    else if(textyear.equals("BMS")){
                        update_profile_stream.setSelection(3);
                    }
                    else if(textyear.equals("BBI")){
                        update_profile_stream.setSelection(4);
                    }
                    else{
                        update_profile_name.setError("Please Select Stream");
                        update_profile_name.requestFocus();
                    }

                }
                else{
                    Toast.makeText(UpadateProfile_Activity.this, "Something Went Wrong !!!", Toast.LENGTH_SHORT).show();
                }
                update_profile_progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpadateProfile_Activity.this, "Something Went Wrong !!!", Toast.LENGTH_SHORT).show();
                update_profile_progressBar.setVisibility(View.GONE);

            }
        });
    }
    private void genderspinner(){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(UpadateProfile_Activity.this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        update_profile_gender.setAdapter(adapter);

    }
    private void yearspinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(UpadateProfile_Activity.this, R.array.year_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        update_profile_year.setAdapter(adapter);

    }
    private void streamspinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(UpadateProfile_Activity.this, R.array.stream_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        update_profile_stream.setAdapter(adapter);

    }
    private boolean validation(){
        if (TextUtils.isEmpty(textname)) {
            update_profile_name.setError("Enter Name");
            update_profile_name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(textroll)) {
            update_profile_roll.setError("Enter Roll No");
            update_profile_roll.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(textphone)) {
            update_profile_phone.setError("Enter Phone Number");
            update_profile_phone.requestFocus();
            return false;
        }
        if (update_profile_gender.getSelectedItem().equals("Select Gender")) {
            update_profile_name.setError("Please Select Gender");
            update_profile_name.requestFocus();
            return false;
        }
        if (update_profile_year.getSelectedItem().equals("Select Year")) {
            update_profile_name.setError("Please Select Year");
            update_profile_name.requestFocus();
            return false;
        }
        if (update_profile_stream.getSelectedItem().equals("Select Stream")) {
            update_profile_name.setError("please select stream");
            update_profile_name.requestFocus();
            return false;
        }
        if (!Patterns.PHONE.matcher(textphone).matches()) {
            update_profile_phone.setError("Enter Valid Phone Number Please");
            update_profile_phone.requestFocus();
            return false;
        }
        return true;
    }
}