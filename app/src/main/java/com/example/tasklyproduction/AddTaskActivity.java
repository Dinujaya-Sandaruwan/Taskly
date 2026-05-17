package com.example.tasklyproduction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        ImageView ivClose = findViewById(R.id.ivClose);
        ivClose.setOnClickListener(v -> finish());
    }
}