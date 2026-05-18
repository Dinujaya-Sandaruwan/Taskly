package com.example.tasklyproduction;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Calendar;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        // Set dynamic year
        TextView tvCopyright = findViewById(R.id.tvCopyright);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        tvCopyright.setText("© " + year + " TASKLY ARCHITECTURE");
    }
}