package com.example.documentor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private TextView register_text;
    public TextView Login_connector;
    private Spinner gender_spinner,year_spinner,stream_spinner;
    private EditText input_name,input_roll,input_email,input_pass,input_phone;
    private Button submit_btn;
    private static final String TAG="RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        register_text=findViewById(R.id.RegisterText);
        input_name=findViewById(R.id.register_name);
        input_roll=findViewById(R.id.register_roll);
        input_email=findViewById(R.id.register_email);
        input_pass=findViewById(R.id.register_password);
        input_phone=findViewById(R.id.register_phone);
        submit_btn=findViewById(R.id.register_submit);
        Login_connector=findViewById(R.id.login_connector);
        ImageView image_Viewshowhidepass=findViewById(R.id.img_register_showhidepass);
        image_Viewshowhidepass.setImageResource(R.drawable.ic_hide_pwd);
        image_Viewshowhidepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_pass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    input_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    image_Viewshowhidepass.setImageResource(R.drawable.ic_hide_pwd);
                }
                else{
                    input_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    image_Viewshowhidepass.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });
        Login_connector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(RegisterActivity.this,Login_Activity.class);
                startActivity(intent);
            }
        });
        genderspinner();
        yearspinner();
        streamspinner();
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

    }
    private void Register(){
        String name=input_name.getText().toString().trim();
        String roll=input_roll.getText().toString().trim();
        String email=input_email.getText().toString().trim();
        String pass=input_pass.getText().toString().trim();
        String phone=input_phone.getText().toString().trim();
        String gender=gender_spinner.getSelectedItem().toString();
        String year=year_spinner.getSelectedItem().toString();
        String stream=stream_spinner.getSelectedItem().toString();
        Validation(name,roll,email,pass,phone,gender,year,stream);
        if(Validation(name,roll,email,pass,phone,gender,year,stream)==true){
            registeruser(name,roll,email,pass,phone,gender,year,stream);
        }
        else{
            Toast.makeText(this, "Please Fill all the Details !!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean Validation(String name, String roll, String email, String pass, String phone, String gender, String year, String stream) {
        if(name.isEmpty()){
            input_name.setError("Enter Name");
            input_name.requestFocus();
            return false;
        }
        if (roll.isEmpty()) {
            input_roll.setError("Enter Roll No");
            input_roll.requestFocus();
            return false;
        }
        if (email.isEmpty()) {
            input_email.setError("Enter email id");
            input_email.requestFocus();
            return false;
        }
        if (pass.isEmpty()) {
            input_pass.setError("Enter Password");
            input_pass.requestFocus();
            return false;
        }
        if (phone.isEmpty()) {
            input_phone.setError("Enter Phone Number");
            input_phone.requestFocus();
            return false;
        }
        if (gender_spinner.getSelectedItem().equals("Select Gender")) {
            input_email.setError("Please Select Gender");
            input_email.requestFocus();
            return false;
        }
        if (year_spinner.getSelectedItem().equals("Select Year")) {
            input_email.setError("Please Select Year");
            input_email.requestFocus();
            return false;
        }
        if (stream_spinner.getSelectedItem().equals("Select Stream")) {
            input_email.setError("please select stream");
            input_email.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.setError("Enter Valid Email ID");
            input_email.requestFocus();
            return false;
        }
        if (pass.length()<8) {
            input_pass.setError("Password Should contain more than 8 character");
            input_pass.requestFocus();
            return false;
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            input_phone.setError("Enter Valid Phone Number Please");
            input_phone.requestFocus();
            return false;
        }
        return true;
    }


    private void registeruser(String name, String roll, String email, String pass, String phone, String gender, String year, String stream) {
        FirebaseAuth mAuth =FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser= mAuth.getCurrentUser();
                    //Update Display Name of User
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    firebaseUser.updateProfile(profileChangeRequest);
                    //Enter user data in Firebase realtime Database
                    ReadWriteUserDetails writeUserDetails =new ReadWriteUserDetails(roll,email,pass,phone,gender,year,stream);
                    //extract user reference from database for "registered users"
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Registered Users");
                    reference.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //send verification email
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this, "Registration SuccessFull Please Verify Your Email", Toast.LENGTH_SHORT).show();
                                Intent intent =new Intent(RegisterActivity.this,Login_Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "User registration failed.Please try again", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });



                }
                else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        input_pass.setError("Your Password is too weak Please use Strong Password");
                        input_pass.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        input_email.setError("Your email is invalid or in use Kindly re enter");
                        input_email.requestFocus();
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        input_pass.setError("User is already Registered");
                        input_pass.requestFocus();
                    }
                    catch (Exception e){
                        Log.v(TAG,e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void genderspinner(){
        gender_spinner=findViewById(R.id.register_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_spinner.setAdapter(adapter);
        gender_spinner.setSelection(0);

    }
    public void yearspinner(){
        year_spinner=findViewById(R.id.register_year);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.year_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year_spinner.setAdapter(adapter);
        year_spinner.setSelection(0);
    }
    public void streamspinner(){
        stream_spinner=findViewById(R.id.register_stream);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.stream_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stream_spinner.setAdapter(adapter);
        stream_spinner.setSelection(0);
    }

}