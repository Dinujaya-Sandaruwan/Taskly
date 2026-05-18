package com.example.tasklyproduction;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {

    private EditText etTaskTitle, etDescription;
    private TextView tvDueDate, tvCreatedAt, tvUpdatedAt;
    private TextView tvPriorityHigh, tvPriorityMedium, tvPriorityLow;
    private LinearLayout llPriorityHigh;
    private SwitchMaterial swComplete;
    private MaterialButton btnSaveChanges;

    private DatabaseHelper dbHelper;
    private int taskId = -1;
    private Task currentTask;
    private String selectedPriority = "HIGH";
    private String selectedDueDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        dbHelper = new DatabaseHelper(this);

        // Get task ID from intent
        taskId = getIntent().getIntExtra("task_id", -1);
        if (taskId == -1) {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Header
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        ImageView ivDelete = findViewById(R.id.ivDelete);
        ivDelete.setOnClickListener(v -> confirmDelete());

        // Fields
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etDescription = findViewById(R.id.etDescription);
        tvDueDate = findViewById(R.id.tvDueDate);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvUpdatedAt = findViewById(R.id.tvUpdatedAt);
        swComplete = findViewById(R.id.swComplete);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Due date card
        MaterialCardView cvDueDate = findViewById(R.id.cvDueDate);
        cvDueDate.setOnClickListener(v -> showDatePicker());
        tvDueDate.setOnClickListener(v -> showDatePicker());

        // Priority tabs
        llPriorityHigh = findViewById(R.id.llPriorityHigh);
        tvPriorityHigh = findViewById(R.id.tvPriorityHigh);
        tvPriorityMedium = findViewById(R.id.tvPriorityMedium);
        tvPriorityLow = findViewById(R.id.tvPriorityLow);

        llPriorityHigh.setOnClickListener(v -> selectPriority("HIGH"));
        tvPriorityMedium.setOnClickListener(v -> selectPriority("MEDIUM"));
        tvPriorityLow.setOnClickListener(v -> selectPriority("LOW"));

        // Save button
        btnSaveChanges.setOnClickListener(v -> saveChanges());

        // Load task data
        loadTask();
    }

    private void loadTask() {
        currentTask = dbHelper.getTaskById(taskId);
        if (currentTask == null) {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate fields
        etTaskTitle.setText(currentTask.getTitle());

        if (currentTask.getDescription() != null && !currentTask.getDescription().isEmpty()) {
            etDescription.setText(currentTask.getDescription());
        }

        // Due date
        selectedDueDate = currentTask.getDueDate() != null ? currentTask.getDueDate() : "";
        if (!selectedDueDate.isEmpty()) {
            try {
                SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                tvDueDate.setText(displayFormat.format(dbFormat.parse(selectedDueDate)));
            } catch (Exception e) {
                tvDueDate.setText(selectedDueDate);
            }
        }

        // Priority
        selectedPriority = currentTask.getPriority() != null ? currentTask.getPriority() : "MEDIUM";
        selectPriority(selectedPriority);

        // Completion status
        swComplete.setChecked(currentTask.isCompleted());

        // Metadata timestamps
        if (currentTask.getCreatedAt() != null) {
            tvCreatedAt.setText(currentTask.getCreatedAt());
        }
        if (currentTask.getUpdatedAt() != null) {
            tvUpdatedAt.setText(currentTask.getUpdatedAt());
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();

        // Try to pre-select current due date
        if (!selectedDueDate.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                cal.setTime(sdf.parse(selectedDueDate));
            } catch (Exception ignored) {
            }
        }

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedDueDate = sdf.format(selected.getTime());

                    SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    tvDueDate.setText(displayFormat.format(selected.getTime()));
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void selectPriority(String priority) {
        selectedPriority = priority;

        // Reset all - handle the LinearLayout HIGH tab differently
        llPriorityHigh.setBackgroundResource(0);
        tvPriorityHigh.setTextColor(getResources().getColor(R.color.version_text));
        tvPriorityHigh.setTypeface(null, android.graphics.Typeface.NORMAL);

        tvPriorityMedium.setBackgroundResource(0);
        tvPriorityMedium.setTextColor(getResources().getColor(R.color.version_text));
        tvPriorityMedium.setTypeface(null, android.graphics.Typeface.NORMAL);

        tvPriorityLow.setBackgroundResource(0);
        tvPriorityLow.setTextColor(getResources().getColor(R.color.version_text));
        tvPriorityLow.setTypeface(null, android.graphics.Typeface.NORMAL);

        switch (priority) {
            case "HIGH":
                llPriorityHigh.setBackgroundResource(R.drawable.bg_tab_selected);
                tvPriorityHigh.setTextColor(getResources().getColor(R.color.white));
                tvPriorityHigh.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case "MEDIUM":
                tvPriorityMedium.setBackgroundResource(R.drawable.bg_tab_selected);
                tvPriorityMedium.setTextColor(getResources().getColor(R.color.white));
                tvPriorityMedium.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
            case "LOW":
                tvPriorityLow.setBackgroundResource(R.drawable.bg_tab_selected);
                tvPriorityLow.setTextColor(getResources().getColor(R.color.white));
                tvPriorityLow.setTypeface(null, android.graphics.Typeface.BOLD);
                break;
        }
    }

    private void saveChanges() {
        String title = etTaskTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            etTaskTitle.setError("Task title is required");
            etTaskTitle.requestFocus();
            return;
        }

        currentTask.setTitle(title);
        currentTask.setDescription(description);
        currentTask.setDueDate(selectedDueDate);
        currentTask.setPriority(selectedPriority);
        currentTask.setCompleted(swComplete.isChecked());

        boolean success = dbHelper.updateTask(currentTask);
        if (success) {
            // Reschedule notification with updated info
            String alertTime = currentTask.getAlertTime();
            NotificationHelper.cancelReminder(this, taskId);
            if (!currentTask.isCompleted()) {
                NotificationHelper.scheduleReminder(this, taskId, title,
                        selectedDueDate, alertTime);
            }
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    NotificationHelper.cancelReminder(this, taskId);
                    boolean success = dbHelper.deleteTask(taskId);
                    if (success) {
                        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}