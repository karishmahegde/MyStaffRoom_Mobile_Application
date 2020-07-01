package com.mystaffroom.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mystaffroom.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference reference, database_college, database_departments;

    Spinner colleges_spinner, department_spinner;

    ProgressBar progressBar;

    EditText username, email, password;
    TextView login;
    Button signup;

    String selected_college, selected_department;

    List<String> collegeList = new ArrayList<>();
    List<String> departmentList = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.edit_text_username);
        email = findViewById(R.id.edit_text_email);
        password = findViewById(R.id.edit_text_password);
        signup = findViewById(R.id.button_signup);
        login = findViewById(R.id.text_view_login);
        colleges_spinner = findViewById(R.id.college_spinner);
        department_spinner = findViewById(R.id.department_spinner);

        progressBar = findViewById(R.id.progressbar);

        collegeList.add("Select College");
        departmentList.add("Select your Department");

        auth = FirebaseAuth.getInstance();

        database_college = FirebaseDatabase.getInstance().getReference("Colleges");
        database_departments = FirebaseDatabase.getInstance().getReference("Department");

        database_college.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String collegeName = dataSnapshot1.getValue(String.class);
                    collegeList.add(collegeName);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, collegeList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                colleges_spinner.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegisterActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        database_departments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String deptName = dataSnapshot1.getValue(String.class);
                    departmentList.add(deptName);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, departmentList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                department_spinner.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegisterActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        colleges_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_college = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        department_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_department = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username_text = username.getText().toString().trim();
                String email_text = email.getText().toString().trim();
                String password_text = password.getText().toString().trim();

                if (TextUtils.isEmpty(username_text)){
                    username.setError("Username required");
                    username.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(email_text)){
                    email.setError("Email required");
                    email.requestFocus();
                    return;
                }
                if (password_text.length()<8 || password_text.length()>12){
                    password.setError("Password should be 8-12 characters");
                    password.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email_text).matches()) {
                    email.setError("Invalid Email");
                    email.requestFocus();
                    return;
                }
                if (username.length()<2){
                    username.setError("Invalid Name");
                    username.requestFocus();
                    return;
                }
                register(username_text,email_text,password_text);

            }
        });

    }

    private void register(final String username, String email, String password)
    {
        if (selected_college.equals("Select College") ){
            Toast.makeText(RegisterActivity.this,"Please select college", Toast.LENGTH_LONG).show();
            return;
        }
        if (selected_department.equals("Select your Department")){
            Toast.makeText(RegisterActivity.this, "Please select department", Toast.LENGTH_LONG).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();

                            hashMap.put("id",userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "Offline");
                            hashMap.put("search", username.toLowerCase());
                            hashMap.put("college", selected_college);
                            hashMap.put("department", selected_department);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                        Toast.makeText(RegisterActivity.this,"Account created! Please login",Toast.LENGTH_LONG).show();
                                        FirebaseAuth.getInstance().signOut();
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        progressBar.setVisibility(View.GONE);
                                        finish();
                                    }
                                }
                            });
                        } else
                        {
                            Toast.makeText(RegisterActivity.this,"Registeration not successful. Try again",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
