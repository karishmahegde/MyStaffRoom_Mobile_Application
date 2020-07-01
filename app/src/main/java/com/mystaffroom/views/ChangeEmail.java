package com.mystaffroom.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mystaffroom.R;

public class ChangeEmail extends AppCompatActivity {

    TextView reauthenticate_heading, new_email_heading;
    EditText reauthenticate_password, new_email;
    Button button_authenticate, button_reset_email;

    ProgressBar progressBar;

    String entered_password, entered_email;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        reauthenticate_heading = findViewById(R.id.reauthenticate_heading);
        new_email_heading = findViewById(R.id.new_email_heading);
        reauthenticate_password = findViewById(R.id.reauthenticate_password);
        new_email = findViewById(R.id.new_email);
        button_authenticate = findViewById(R.id.button_authenticate);
        button_reset_email = findViewById(R.id.button_reset_email);
        progressBar = findViewById(R.id.progressbar);

        new_email_heading.setVisibility(View.GONE);
        new_email.setVisibility(View.GONE);
        button_reset_email.setVisibility(View.GONE);

        button_authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reautheticate();
            }
        });

    }

    private void reautheticate() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        entered_password = reauthenticate_password.getText().toString();
        if (entered_password.isEmpty()) {
            reauthenticate_password.setError("Password Required");
            reauthenticate_password.requestFocus();
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE);
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), entered_password);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                reauthenticate_heading.setVisibility(View.GONE);
                                reauthenticate_password.setVisibility(View.GONE);
                                button_authenticate.setVisibility(View.GONE);

                                new_email_heading.setVisibility(View.VISIBLE);
                                new_email.setVisibility(View.VISIBLE);
                                button_reset_email.setVisibility(View.VISIBLE);

                                user = FirebaseAuth.getInstance().getCurrentUser();
                                button_reset_email.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        entered_email = new_email.getText().toString().trim();
                                        updateEmail();
                                    }
                                });
                            } else {
                                Toast.makeText(ChangeEmail.this, "Incorrect Password. Try again.", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

        }
    }

    private void updateEmail() {

        if (entered_email.isEmpty()) {
            new_email.setError("Email Required");
            new_email.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(entered_email).matches()) {
            new_email.setError("Invalid Email");
            new_email.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        } else {
            user.updateEmail(entered_email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangeEmail.this, "Email Updated Successfully", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                finish();
                            } else {
                                Toast.makeText(ChangeEmail.this, "Somthing went wrong. Try again", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                finish();
                            }
                        }
                    });
        }
    }
}
