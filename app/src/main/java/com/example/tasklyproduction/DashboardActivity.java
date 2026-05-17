package com.example.tasklyproduction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Add Task FAB
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        // Task Cards Navigation
...
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