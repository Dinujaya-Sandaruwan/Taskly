package com.example.tasklyproduction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        LinearLayout llHomeNav = findViewById(R.id.llHomeNav);
        llHomeNav.setOnClickListener(v -> finish());

        LinearLayout llSettingsNav = findViewById(R.id.llSettingsNav);
        llSettingsNav.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}