package com.example.tasklyproduction;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private TextView tvUserName, tvUserEmail;
    private ImageView ivProfileAvatar;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // User info display
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        ivProfileAvatar = findViewById(R.id.ivProfileAvatar);
        loadUserInfo();

        // Profile picture picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            // Take persistable permission so the URI survives app restarts
                            try {
                                getContentResolver().takePersistableUriPermission(imageUri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } catch (SecurityException e) {
                                // Some providers don't support persistable permissions
                            }
                            ivProfileAvatar.setImageURI(imageUri);
                            ivProfileAvatar.setPadding(0, 0, 0, 0);
                            sessionManager.updateProfilePic(imageUri.toString());
                        }
                    }
                }
        );

        // Edit avatar button
        MaterialCardView cvEditAvatar = findViewById(R.id.cvEditAvatar);
        cvEditAvatar.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
            pickIntent.setType("image/*");
            pickIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            imagePickerLauncher.launch(pickIntent);
        });

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

        // Footer Actions
        findViewById(R.id.llHelpSupport).setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, HelpSupportActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.llAboutTaskly).setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        // Logout
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserInfo() {
        tvUserName.setText(sessionManager.getFullName());
        tvUserEmail.setText(sessionManager.getEmail());

        // Load profile picture if set
        String profilePicUri = sessionManager.getProfilePic();
        if (profilePicUri != null && !profilePicUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(profilePicUri);
                ivProfileAvatar.setImageURI(uri);
                ivProfileAvatar.setPadding(0, 0, 0, 0);
            } catch (Exception e) {
                ivProfileAvatar.setImageResource(R.drawable.ic_dummy_avatar);
            }
        }
    }

    private void showEditProfileSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_edit_profile, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        EditText etEditFullName = bottomSheetView.findViewById(R.id.etEditFullName);
        EditText etEditEmail = bottomSheetView.findViewById(R.id.etEditEmail);
        MaterialButton btnUpdateProfile = bottomSheetView.findViewById(R.id.btnUpdateProfile);

        // Pre-fill with current data
        etEditFullName.setText(sessionManager.getFullName());
        etEditEmail.setText(sessionManager.getEmail());

        btnUpdateProfile.setOnClickListener(v -> {
            String newName = etEditFullName.getText().toString().trim();
            String newEmail = etEditEmail.getText().toString().trim();

            if (newName.isEmpty()) {
                etEditFullName.setError("Name is required");
                etEditFullName.requestFocus();
                return;
            }

            if (newEmail.isEmpty()) {
                etEditEmail.setError("Email is required");
                etEditEmail.requestFocus();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                etEditEmail.setError("Please enter a valid email");
                etEditEmail.requestFocus();
                return;
            }

            boolean success = dbHelper.updateUserProfile(sessionManager.getUserId(), newName, newEmail);
            if (success) {
                sessionManager.updateName(newName);
                sessionManager.updateEmail(newEmail);
                loadUserInfo();
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(this, "Email already taken by another account", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }

    private void showChangePasswordSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_change_password, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        EditText etCurrentPassword = bottomSheetView.findViewById(R.id.etCurrentPassword);
        EditText etNewPassword = bottomSheetView.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = bottomSheetView.findViewById(R.id.etConfirmPassword);
        MaterialButton btnConfirmPassword = bottomSheetView.findViewById(R.id.btnConfirmPassword);

        // Password visibility toggles
        ImageView ivToggle1 = bottomSheetView.findViewById(R.id.ivToggle1);
        ImageView ivToggle2 = bottomSheetView.findViewById(R.id.ivToggle2);
        ImageView ivToggle3 = bottomSheetView.findViewById(R.id.ivToggle3);

        final boolean[] toggleState = {false, false, false};

        ivToggle1.setOnClickListener(v -> {
            toggleState[0] = !toggleState[0];
            if (toggleState[0]) {
                etCurrentPassword.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                ivToggle1.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etCurrentPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                ivToggle1.setImageResource(R.drawable.ic_visibility);
            }
            etCurrentPassword.setSelection(etCurrentPassword.getText().length());
        });

        ivToggle2.setOnClickListener(v -> {
            toggleState[1] = !toggleState[1];
            if (toggleState[1]) {
                etNewPassword.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                ivToggle2.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etNewPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                ivToggle2.setImageResource(R.drawable.ic_visibility);
            }
            etNewPassword.setSelection(etNewPassword.getText().length());
        });

        ivToggle3.setOnClickListener(v -> {
            toggleState[2] = !toggleState[2];
            if (toggleState[2]) {
                etConfirmPassword.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                ivToggle3.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etConfirmPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                ivToggle3.setImageResource(R.drawable.ic_visibility);
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        btnConfirmPassword.setOnClickListener(v -> {
            String currentPass = etCurrentPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (currentPass.isEmpty()) {
                etCurrentPassword.setError("Current password is required");
                etCurrentPassword.requestFocus();
                return;
            }

            if (newPass.isEmpty()) {
                etNewPassword.setError("New password is required");
                etNewPassword.requestFocus();
                return;
            }

            if (newPass.length() < 6) {
                etNewPassword.setError("Password must be at least 6 characters");
                etNewPassword.requestFocus();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                etConfirmPassword.setError("Passwords do not match");
                etConfirmPassword.requestFocus();
                return;
            }

            boolean success = dbHelper.changePassword(sessionManager.getUserId(), currentPass, newPass);
            if (success) {
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }
}