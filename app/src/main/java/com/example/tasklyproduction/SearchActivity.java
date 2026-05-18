package com.example.tasklyproduction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private EditText etSearch;
    private RecyclerView rvSearchResults;
    private MaterialCardView cvNoResults;
    private TaskAdapter searchAdapter;
    private List<Task> searchResults;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    // Filter tabs
    private TextView tvStatusPending, tvStatusCompleted;
    private TextView tvPriorityHigh, tvPriorityMedium, tvPriorityLow;
    private TextView tvDueToday, tvDueTomorrow, tvDueThisWeek;

    // Active filters
    private String activeStatus = "Pending";
    private String activePriority = null;
    private String activeDueDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Header
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        // Search field
        etSearch = findViewById(R.id.etSearch);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        cvNoResults = findViewById(R.id.cvNoResults);

        // Clear search
        ImageView ivClearSearch = findViewById(R.id.ivClearSearch);
        ivClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
        });

        // Setup RecyclerView
        searchResults = new ArrayList<>();
        searchAdapter = new TaskAdapter(searchResults, this);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(searchAdapter);

        // Status tabs
        tvStatusPending = findViewById(R.id.tvStatusPending);
        tvStatusCompleted = findViewById(R.id.tvStatusCompleted);

        tvStatusPending.setOnClickListener(v -> {
            activeStatus = activeStatus != null && activeStatus.equals("Pending") ? null : "Pending";
            updateStatusTabs();
            performSearch();
        });
        tvStatusCompleted.setOnClickListener(v -> {
            activeStatus = activeStatus != null && activeStatus.equals("Completed") ? null : "Completed";
            updateStatusTabs();
            performSearch();
        });

        // Priority tabs
        tvPriorityHigh = findViewById(R.id.tvPriorityHigh);
        tvPriorityMedium = findViewById(R.id.tvPriorityMedium);
        tvPriorityLow = findViewById(R.id.tvPriorityLow);

        tvPriorityHigh.setOnClickListener(v -> {
            activePriority = activePriority != null && activePriority.equals("HIGH") ? null : "HIGH";
            updatePriorityTabs();
            performSearch();
        });
        tvPriorityMedium.setOnClickListener(v -> {
            activePriority = activePriority != null && activePriority.equals("MEDIUM") ? null : "MEDIUM";
            updatePriorityTabs();
            performSearch();
        });
        tvPriorityLow.setOnClickListener(v -> {
            activePriority = activePriority != null && activePriority.equals("LOW") ? null : "LOW";
            updatePriorityTabs();
            performSearch();
        });

        // Due date tabs
        tvDueToday = findViewById(R.id.tvDueToday);
        tvDueTomorrow = findViewById(R.id.tvDueTomorrow);
        tvDueThisWeek = findViewById(R.id.tvDueThisWeek);

        tvDueToday.setOnClickListener(v -> {
            activeDueDate = activeDueDate != null && activeDueDate.equals("Today") ? null : "Today";
            updateDueDateTabs();
            performSearch();
        });
        tvDueTomorrow.setOnClickListener(v -> {
            activeDueDate = activeDueDate != null && activeDueDate.equals("Tomorrow") ? null : "Tomorrow";
            updateDueDateTabs();
            performSearch();
        });
        tvDueThisWeek.setOnClickListener(v -> {
            activeDueDate = activeDueDate != null && activeDueDate.equals("This Week") ? null : "This Week";
            updateDueDateTabs();
            performSearch();
        });

        // Clear all filters
        TextView tvClearFilters = findViewById(R.id.tvClearFilters);
        tvClearFilters.setOnClickListener(v -> {
            etSearch.setText("");
            activeStatus = null;
            activePriority = null;
            activeDueDate = null;
            updateStatusTabs();
            updatePriorityTabs();
            updateDueDateTabs();
            performSearch();
        });

        // Live search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Bottom Navigation
        LinearLayout llHomeNav = findViewById(R.id.llHomeNav);
        llHomeNav.setOnClickListener(v -> finish());

        LinearLayout llSettingsNav = findViewById(R.id.llSettingsNav);
        llSettingsNav.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Initial search
        performSearch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        performSearch();
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        int userId = sessionManager.getUserId();

        List<Task> results = dbHelper.searchTasksFiltered(userId, query,
                activeStatus, activePriority, activeDueDate);

        searchAdapter.updateTasks(results);

        if (results.isEmpty()) {
            rvSearchResults.setVisibility(View.GONE);
            cvNoResults.setVisibility(View.VISIBLE);
        } else {
            rvSearchResults.setVisibility(View.VISIBLE);
            cvNoResults.setVisibility(View.GONE);
        }
    }

    private void updateStatusTabs() {
        resetTab(tvStatusPending);
        resetTab(tvStatusCompleted);

        if ("Pending".equals(activeStatus)) {
            selectTab(tvStatusPending);
        } else if ("Completed".equals(activeStatus)) {
            selectTab(tvStatusCompleted);
        }
    }

    private void updatePriorityTabs() {
        resetTab(tvPriorityHigh);
        resetTab(tvPriorityMedium);
        resetTab(tvPriorityLow);

        if ("HIGH".equals(activePriority)) {
            selectTab(tvPriorityHigh);
        } else if ("MEDIUM".equals(activePriority)) {
            selectTab(tvPriorityMedium);
        } else if ("LOW".equals(activePriority)) {
            selectTab(tvPriorityLow);
        }
    }

    private void updateDueDateTabs() {
        resetTab(tvDueToday);
        resetTab(tvDueTomorrow);
        resetTab(tvDueThisWeek);

        if ("Today".equals(activeDueDate)) {
            selectTab(tvDueToday);
        } else if ("Tomorrow".equals(activeDueDate)) {
            selectTab(tvDueTomorrow);
        } else if ("This Week".equals(activeDueDate)) {
            selectTab(tvDueThisWeek);
        }
    }

    private void selectTab(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_tab_selected);
        tab.setTextColor(getResources().getColor(R.color.white));
        tab.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    private void resetTab(TextView tab) {
        tab.setBackgroundResource(0);
        tab.setTextColor(getResources().getColor(R.color.version_text));
        tab.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(SearchActivity.this, EditTaskActivity.class);
        intent.putExtra("task_id", task.getId());
        startActivity(intent);
    }

    @Override
    public void onTaskStatusToggle(Task task, int position) {
        dbHelper.toggleTaskCompletion(task.getId());
        performSearch();
    }
}