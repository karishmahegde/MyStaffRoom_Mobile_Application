package com.mystaffroom.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mystaffroom.R;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText text_recovery_email;
    Button button_send;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        text_recovery_email = findViewById(R.id.text_recovery_email);
        button_send = findViewById(R.id.button_send);

        firebaseAuth = FirebaseAuth.getInstance();

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = text_recovery_email.getText().toString().trim();
                if (email.equals(""))
                {
                    Toast.makeText(ResetPasswordActivity.this,"Please provide your email id.",Toast.LENGTH_LONG).show();
                }else{
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(ResetPasswordActivity.this,"Recovery email sent.",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                            }
                            else{
                                String error = task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this,error,Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });

    }
}
