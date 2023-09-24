package com.arr.simple.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.arr.simple.databinding.ActivityBugBinding;

public class BugActivity extends AppCompatActivity {

    private ActivityBugBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBugBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
