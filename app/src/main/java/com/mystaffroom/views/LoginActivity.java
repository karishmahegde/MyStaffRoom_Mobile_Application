package com.mystaffroom.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mystaffroom.R;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    TextView forgot_password;
    ProgressBar progressBar;

    EditText email, password;
    Button button_login;
    TextView register;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.text_email);
        password = findViewById(R.id.text_password);
        button_login = findViewById(R.id.button_login);
        register = findViewById(R.id.text_view_register);
        forgot_password = findViewById(R.id.forgot_password);
        progressBar = findViewById(R.id.progressbar);

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_text = email.getText().toString().trim();
                String password_text = password.getText().toString().trim();

                if (TextUtils.isEmpty(email_text)) {
                    email.setError("Username required");
                    email.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password_text)) {
                    password.setError("Email required");
                    password.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email_text).matches()) {
                    email.setError("Invalid Email");
                    email.requestFocus();
                    return;
                }

                login(email_text, password_text);
            }
        });

    }

    private void login(String email_text, String password_text) {
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email_text, password_text)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Email or Password is Incorrect. Try Again.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
