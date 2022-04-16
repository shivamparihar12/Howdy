package com.example.howdy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.howdy.databinding.ActivityBuildProfileBinding;

public class BuildProfileActivity extends AppCompatActivity {
    private ActivityBuildProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityBuildProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}