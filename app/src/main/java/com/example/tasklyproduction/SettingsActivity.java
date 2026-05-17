package com.example.tasklyproduction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Header Navigation
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        // Bottom Navigation
        LinearLayout llHomeNav = findViewById(R.id.llHomeNav);
        llHomeNav.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        LinearLayout llSearchNav = findViewById(R.id.llSearchNav);
        llSearchNav.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // Settings Actions
        LinearLayout llEditInfo = findViewById(R.id.llEditInfo);
        llEditInfo.setOnClickListener(v -> showEditProfileSheet());

        LinearLayout llChangePassword = findViewById(R.id.llChangePassword);
        llChangePassword.setOnClickListener(v -> showChangePasswordSheet());
    }

    private void showEditProfileSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_edit_profile, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void showChangePasswordSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_change_password, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}