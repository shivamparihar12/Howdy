package com.example.howdy;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.howdy.databinding.ActivityAddStoryBinding;

public class AddStoryActivity extends AppCompatActivity {
    private ActivityAddStoryBinding binding;
    private final static String TAG = "AddStoryActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String uri = getIntent().getStringExtra("imagePath");
        binding.imageView.setImageURI(Uri.parse(uri));

        binding.uploadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }



}