package com.mystaffroom.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mystaffroom.R;
import com.mystaffroom.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChangeCollege extends AppCompatActivity {

    TextView reauthenticate_heading, new_college_heading;
    EditText password;
    Button button_authenticate, button_change_college;
    Spinner new_college_spinner;

    DatabaseReference database_college,reference;

    String entered_password,selected_college,current_user_college;

    ProgressBar progressBar;

    List<String> collegeList = new ArrayList<>();

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_college);

        reauthenticate_heading = findViewById(R.id.reauthenticate_heading);
        new_college_heading = findViewById(R.id.new_college_heading);
        password = findViewById(R.id.reauthenticate_password);
        button_authenticate = findViewById(R.id.button_authenticate);
        button_change_college = findViewById(R.id.button_change_college);
        new_college_spinner = findViewById(R.id.new_college_spinner);
        progressBar = findViewById(R.id.progressbar);

        new_college_heading.setVisibility(View.GONE);
        new_college_spinner.setVisibility(View.GONE);
        button_change_college.setVisibility(View.GONE);

        button_authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reautheticate();
            }
        });

        collegeList.add("Select College");
        database_college = FirebaseDatabase.getInstance().getReference("Colleges");

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                current_user_college = user.getCollege();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        database_college.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String collegeName = dataSnapshot1.getValue(String.class);
                    collegeList.add(collegeName);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ChangeCollege.this, android.R.layout.simple_spinner_item, collegeList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                new_college_spinner.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChangeCollege.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        new_college_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_college = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

    }

    private void reautheticate() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        entered_password = password.getText().toString();
        if (entered_password.isEmpty()) {
            password.setError("Password Required");
            password.requestFocus();
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE);
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), entered_password);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                reauthenticate_heading.setVisibility(View.GONE);
                                password.setVisibility(View.GONE);
                                button_authenticate.setVisibility(View.GONE);

                                new_college_heading.setVisibility(View.VISIBLE);
                                new_college_spinner.setVisibility(View.VISIBLE);
                                button_change_college.setVisibility(View.VISIBLE);

                                button_change_college.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updateCollege(selected_college);
                                    }
                                });
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ChangeCollege.this, "Incorrect Password. Try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
    }

    private void updateCollege(String selected_college){
        progressBar.setVisibility(View.VISIBLE);
        if (!current_user_college.equals(selected_college)){
            HashMap<String, Object> map = new HashMap<>();
            map.put("college", selected_college);
            reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ChangeCollege.this, "College Updated!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else{
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ChangeCollege.this, "Something went wrong. Try again later.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ChangeCollege.this, "You are already in this college.", Toast.LENGTH_LONG).show();
        }
    }

}
