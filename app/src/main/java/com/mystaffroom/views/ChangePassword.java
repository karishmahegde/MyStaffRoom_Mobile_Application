package com.mystaffroom.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class ChangePassword extends AppCompatActivity {

    TextView reauthenticate_heading, new_password_heading;
    EditText reauthenticate_password, new_password, retype_password;
    Button button_authenticate, button_update_password;

    FirebaseUser user;

    ProgressBar progressBar;

    String entered_password, new_password_text, retype_password_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        reauthenticate_heading = findViewById(R.id.reauthenticate_heading);
        new_password_heading = findViewById(R.id.new_password_heading);
        reauthenticate_password = findViewById(R.id.reauthenticate_password);
        new_password = findViewById(R.id.new_password);
        retype_password = findViewById(R.id.retype_password);
        button_authenticate = findViewById(R.id.button_authenticate);
        button_update_password = findViewById(R.id.button_update_password);
        progressBar = findViewById(R.id.progressbar);

        new_password_heading = findViewById(R.id.new_password_heading);
        new_password.setVisibility(View.GONE);
        retype_password.setVisibility(View.GONE);
        button_update_password.setVisibility(View.GONE);

        button_authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reauthenticate();
            }
        });
    }

    private void reauthenticate() {
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
                                progressBar.setVisibility(View.GONE);
                                reauthenticate_heading.setVisibility(View.GONE);
                                reauthenticate_password.setVisibility(View.GONE);
                                button_authenticate.setVisibility(View.GONE);

                                new_password_heading.setVisibility(View.VISIBLE);
                                new_password.setVisibility(View.VISIBLE);
                                retype_password.setVisibility(View.VISIBLE);
                                button_update_password.setVisibility(View.VISIBLE);

                                button_update_password.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updatePassword();
                                    }
                                });
                            } else {
                                Toast.makeText(ChangePassword.this, "Incorrect Password. Try again.", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

        }
    }

    private void updatePassword() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        new_password_text = new_password.getText().toString();
        retype_password_text = retype_password.getText().toString();
        if (new_password_text.isEmpty()) {
            new_password.setError("Required");
            new_password.requestFocus();
            return;
        }
        if (retype_password_text.isEmpty()) {
            retype_password.setError("Required");
            retype_password.requestFocus();
            return;
        }
        if (new_password_text.equals(retype_password_text)) {
            if (new_password_text.length() >= 8) {
                //Update Password
                progressBar.setVisibility(View.VISIBLE);
                user.updatePassword(new_password_text).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ChangePassword.this, "Password updated.", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ChangePassword.this, "Update unsuccessful. Try again later.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            } else {
                Toast.makeText(ChangePassword.this, "Password must be atleast 8 characters.", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
        } else {
            Toast.makeText(ChangePassword.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
    }
}
