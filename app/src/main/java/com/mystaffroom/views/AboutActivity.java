package com.mystaffroom.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.mystaffroom.R;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class AboutActivity extends AppCompatActivity {

    TextView paragraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        paragraph = findViewById(R.id.paragraph1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            paragraph.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
    }
}
