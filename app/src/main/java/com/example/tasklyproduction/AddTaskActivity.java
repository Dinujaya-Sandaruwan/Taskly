package com.example.tasklyproduction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTaskTitle, etNotes;
    private TextView tvDueDate, tvAlertTime;
    private TextView tvPriorityHigh, tvPriorityMedium, tvPriorityLow;
    private TextView tvSave;

    private String selectedDueDate = "";
    private String selectedAlertTime = "";
    private String selectedPriority = "HIGH";

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Header
        ImageView ivClose = findViewById(R.id.ivClose);
        ivClose.setOnClickListener(v -> finish());

        tvSave = findViewById(R.id.tvSave);

        // Input fields
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etNotes = findViewById(R.id.etNotes);

        // Schedule
        tvDueDate = findViewById(R.id.tvDueDate);
        tvAlertTime = findViewById(R.id.tvAlertTime);
        RelativeLayout rlDueDate = findViewById(R.id.rlDueDate);
        RelativeLayout rlAlertTime = findViewById(R.id.rlAlertTime);

        rlDueDate.setOnClickListener(v -> showDatePicker());
        tvDueDate.setOnClickListener(v -> showDatePicker());
        rlAlertTime.setOnClickListener(v -> showTimePicker());
        tvAlertTime.setOnClickListener(v -> showTimePicker());

        // Priority tabs
        tvPriorityHigh = findViewById(R.id.tvPriorityHigh);
        tvPriorityMedium = findViewById(R.id.tvPriorityMedium);
        tvPriorityLow = findViewById(R.id.tvPriorityLow);

        tvPriorityHigh.setOnClickListener(v -> selectPriority("HIGH"));
        tvPriorityMedium.setOnClickListener(v -> selectPriority("MEDIUM"));
        tvPriorityLow.setOnClickListener(v -> selectPriority("LOW"));

        // Save button
        tvSave.setOnClickListener(v -> saveTask());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
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

    private void showTimePicker() {
        Calendar cal = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedAlertTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);

                    // Display in 12-hour format
                    String amPm = hourOfDay >= 12 ? "PM" : "AM";
                    int displayHour = hourOfDay % 12;
                    if (displayHour == 0) displayHour = 12;
                    tvAlertTime.setText(String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm));
                },
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        dialog.show();
    }

    private void selectPriority(String priority) {
        selectedPriority = priority;

        // Reset all tabs
        tvPriorityHigh.setBackgroundResource(0);
        tvPriorityHigh.setTextColor(getResources().getColor(R.color.version_text));
        tvPriorityHigh.setTypeface(null, android.graphics.Typeface.NORMAL);

        tvPriorityMedium.setBackgroundResource(0);
        tvPriorityMedium.setTextColor(getResources().getColor(R.color.version_text));
        tvPriorityMedium.setTypeface(null, android.graphics.Typeface.NORMAL);

        tvPriorityLow.setBackgroundResource(0);
        tvPriorityLow.setTextColor(getResources().getColor(R.color.version_text));
        tvPriorityLow.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Set selected tab
        TextView selectedTab;
        switch (priority) {
            case "MEDIUM":
                selectedTab = tvPriorityMedium;
                break;
            case "LOW":
                selectedTab = tvPriorityLow;
                break;
            default:
                selectedTab = tvPriorityHigh;
                break;
        }
        selectedTab.setBackgroundResource(R.drawable.bg_tab_selected);
        selectedTab.setTextColor(getResources().getColor(R.color.white));
        selectedTab.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    private void saveTask() {
        String title = etTaskTitle.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (title.isEmpty()) {
            etTaskTitle.setError("Task title is required");
            etTaskTitle.requestFocus();
            return;
        }

        Task task = new Task(
                sessionManager.getUserId(),
                title,
                notes,
                selectedDueDate,
                selectedAlertTime,
                selectedPriority
        );

        long result = dbHelper.addTask(task);
        if (result != -1) {
            // Schedule notification reminder
            NotificationHelper.scheduleReminder(this, (int) result, title,
                    selectedDueDate, selectedAlertTime);
            Toast.makeText(this, "Task created", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to create task", Toast.LENGTH_SHORT).show();
        }
    }
}