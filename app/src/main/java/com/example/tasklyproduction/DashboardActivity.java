package com.example.tasklyproduction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.material.card.MaterialCardView;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Task Cards Navigation
        MaterialCardView cvTask1 = findViewById(R.id.cvTask1);
        MaterialCardView cvTask2 = findViewById(R.id.cvTask2);
        MaterialCardView cvTask3 = findViewById(R.id.cvTask3);

        View.OnClickListener taskClickListener = v -> {
            Intent intent = new Intent(DashboardActivity.this, EditTaskActivity.class);
            startActivity(intent);
        };

        cvTask1.setOnClickListener(taskClickListener);
        cvTask2.setOnClickListener(taskClickListener);
        cvTask3.setOnClickListener(taskClickListener);

        // Bottom Navigation
        LinearLayout llSearchNav = findViewById(R.id.llSearchNav);
        llSearchNav.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        LinearLayout llSettingsNav = findViewById(R.id.llSettingsNav);
        llSettingsNav.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}