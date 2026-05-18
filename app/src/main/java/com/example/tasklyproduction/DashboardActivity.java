package com.example.tasklyproduction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView rvTasks;
    private LinearLayout llEmptyState;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private TextView tvTabToday, tvTabUpcoming, tvTabCompleted;
    private TextView tvVelocityPercent, tvProjectName, tvPriorityLabel;
    private String currentTab = "Today";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Request notification permission for Android 13+
        requestNotificationPermission();

        // Views
        rvTasks = findViewById(R.id.rvTasks);
        llEmptyState = findViewById(R.id.llEmptyState);
        tvVelocityPercent = findViewById(R.id.tvVelocityPercent);
        tvProjectName = findViewById(R.id.tvProjectName);
        tvPriorityLabel = findViewById(R.id.tvPriorityLabel);

        // Tabs
        tvTabToday = findViewById(R.id.tvTabToday);
        tvTabUpcoming = findViewById(R.id.tvTabUpcoming);
        tvTabCompleted = findViewById(R.id.tvTabCompleted);

        // Setup RecyclerView
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);

        // Set user name in greeting
        String userName = sessionManager.getFullName();
        if (userName != null && !userName.isEmpty()) {
            String firstName = userName.split(" ")[0];
            tvProjectName.setText(firstName + "'s\nTasks");
        }

        // Tab click listeners
        tvTabToday.setOnClickListener(v -> switchTab("Today"));
        tvTabUpcoming.setOnClickListener(v -> switchTab("Upcoming"));
        tvTabCompleted.setOnClickListener(v -> switchTab("Completed"));

        // Add Task FAB
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
        updateVelocity();
        loadProfilePicture();
    }

    private void loadProfilePicture() {
        ImageView ivHeaderAvatar = findViewById(R.id.ivHeaderAvatar);
        String profilePicUri = sessionManager.getProfilePic();
        if (profilePicUri != null && !profilePicUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(profilePicUri);
                ivHeaderAvatar.setImageURI(uri);
                ivHeaderAvatar.setPadding(0, 0, 0, 0);
                ivHeaderAvatar.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            } catch (Exception e) {
                ivHeaderAvatar.setImageResource(R.drawable.ic_dummy_avatar);
            }
        }
    }

    private void switchTab(String tab) {
        currentTab = tab;

        // Reset all tabs to unselected
        tvTabToday.setBackgroundResource(0);
        tvTabToday.setTextColor(getResources().getColor(R.color.version_text));
        tvTabToday.setTypeface(null, android.graphics.Typeface.NORMAL);

        tvTabUpcoming.setBackgroundResource(0);
        tvTabUpcoming.setTextColor(getResources().getColor(R.color.version_text));
        tvTabUpcoming.setTypeface(null, android.graphics.Typeface.NORMAL);

        tvTabCompleted.setBackgroundResource(0);
        tvTabCompleted.setTextColor(getResources().getColor(R.color.version_text));
        tvTabCompleted.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Set selected tab
        TextView selectedTab;
        switch (tab) {
            case "Upcoming":
                selectedTab = tvTabUpcoming;
                tvPriorityLabel.setText("UPCOMING");
                break;
            case "Completed":
                selectedTab = tvTabCompleted;
                tvPriorityLabel.setText("COMPLETED");
                break;
            default:
                selectedTab = tvTabToday;
                tvPriorityLabel.setText("HIGH PRIORITY");
                break;
        }
        selectedTab.setBackgroundResource(R.drawable.bg_tab_selected);
        selectedTab.setTextColor(getResources().getColor(R.color.white));
        selectedTab.setTypeface(null, android.graphics.Typeface.BOLD);

        loadTasks();
    }

    private void loadTasks() {
        int userId = sessionManager.getUserId();
        List<Task> tasks;

        switch (currentTab) {
            case "Upcoming":
                tasks = dbHelper.getTasksUpcoming(userId);
                break;
            case "Completed":
                tasks = dbHelper.getTasksByStatus(userId, true);
                break;
            default: // Today
                tasks = dbHelper.getTasksDueToday(userId);
                // If no tasks due today, show all pending tasks
                if (tasks.isEmpty()) {
                    tasks = dbHelper.getTasksByStatus(userId, false);
                }
                break;
        }

        taskAdapter.updateTasks(tasks);

        if (tasks.isEmpty()) {
            rvTasks.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvTasks.setVisibility(View.VISIBLE);
            llEmptyState.setVisibility(View.GONE);
        }
    }

    private void updateVelocity() {
        int userId = sessionManager.getUserId();
        int total = dbHelper.getTotalTaskCount(userId);
        int completed = dbHelper.getCompletedTaskCount(userId);

        if (total > 0) {
            int velocity = (int) ((completed / (float) total) * 100);
            tvVelocityPercent.setText(velocity + "%");
        } else {
            tvVelocityPercent.setText("0%");
        }
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(DashboardActivity.this, EditTaskActivity.class);
        intent.putExtra("task_id", task.getId());
        startActivity(intent);
    }

    @Override
    public void onTaskStatusToggle(Task task, int position) {
        dbHelper.toggleTaskCompletion(task.getId());
        // Cancel reminder if task is now completed
        if (!task.isCompleted()) {
            NotificationHelper.cancelReminder(this, task.getId());
        }
        loadTasks();
        updateVelocity();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }
}