package com.mystaffroom.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mystaffroom.R;

public class ImageActivity extends AppCompatActivity {

    ImageView image;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        intent = getIntent();

        image = findViewById(R.id.image_fullsize);
        String img_url = intent.getStringExtra("image_url");
        Glide.with(ImageActivity.this).load(img_url).into(image);
    }
}
