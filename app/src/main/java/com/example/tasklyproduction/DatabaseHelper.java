package com.example.tasklyproduction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "taskly.db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_NAME = "full_name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_CREATED = "created_at";

    // Tasks table
    private static final String TABLE_TASKS = "tasks";
    private static final String COL_TASK_ID = "id";
    private static final String COL_TASK_USER_ID = "user_id";
    private static final String COL_TASK_TITLE = "title";
    private static final String COL_TASK_DESCRIPTION = "description";
    private static final String COL_TASK_DUE_DATE = "due_date";
    private static final String COL_TASK_ALERT_TIME = "alert_time";
    private static final String COL_TASK_PRIORITY = "priority";
    private static final String COL_TASK_COMPLETED = "is_completed";
    private static final String COL_TASK_CREATED = "created_at";
    private static final String COL_TASK_UPDATED = "updated_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_NAME + " TEXT NOT NULL, "
                + COL_USER_EMAIL + " TEXT NOT NULL UNIQUE, "
                + COL_USER_PASSWORD + " TEXT NOT NULL, "
                + COL_USER_CREATED + " TEXT DEFAULT CURRENT_TIMESTAMP)";

        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " ("
                + COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TASK_USER_ID + " INTEGER NOT NULL, "
                + COL_TASK_TITLE + " TEXT NOT NULL, "
                + COL_TASK_DESCRIPTION + " TEXT, "
                + COL_TASK_DUE_DATE + " TEXT, "
                + COL_TASK_ALERT_TIME + " TEXT, "
                + COL_TASK_PRIORITY + " TEXT DEFAULT 'MEDIUM', "
                + COL_TASK_COMPLETED + " INTEGER DEFAULT 0, "
                + COL_TASK_CREATED + " TEXT DEFAULT CURRENT_TIMESTAMP, "
                + COL_TASK_UPDATED + " TEXT DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (" + COL_TASK_USER_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE)";

        db.execSQL(createUsersTable);
        db.execSQL(createTasksTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // ==================== PASSWORD HASHING ====================

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // ==================== USER OPERATIONS ====================

    /**
     * Register a new user. Returns the user ID on success, -1 if email already exists.
     */
    public long registerUser(String fullName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Check if email already exists
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID},
                COL_USER_EMAIL + "=?", new String[]{email.toLowerCase().trim()},
                null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return -1; // Email already exists
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, fullName.trim());
        values.put(COL_USER_EMAIL, email.toLowerCase().trim());
        values.put(COL_USER_PASSWORD, hashPassword(password));

        long result = db.insert(TABLE_USERS, null, values);
        return result;
    }

    /**
     * Authenticate user. Returns user ID on success, -1 on failure.
     */
    public int loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{email.toLowerCase().trim(), hashedPassword},
                null, null, null);

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }
        cursor.close();
        return -1;
    }

    /**
     * Get user full name by ID.
     */
    public String getUserName(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_NAME},
                COL_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            cursor.close();
            return name;
        }
        cursor.close();
        return "";
    }

    /**
     * Get user email by ID.
     */
    public String getUserEmail(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_EMAIL},
                COL_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            String email = cursor.getString(0);
            cursor.close();
            return email;
        }
        cursor.close();
        return "";
    }

    /**
     * Update user profile (name and email). Returns true on success.
     */
    public boolean updateUserProfile(int userId, String fullName, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if new email is taken by another user
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID},
                COL_USER_EMAIL + "=? AND " + COL_USER_ID + "!=?",
                new String[]{email.toLowerCase().trim(), String.valueOf(userId)},
                null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return false; // Email taken by another user
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, fullName.trim());
        values.put(COL_USER_EMAIL, email.toLowerCase().trim());

        int rows = db.update(TABLE_USERS, values, COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    /**
     * Change user password. Returns true on success, false if current password is wrong.
     */
    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Verify current password
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USER_ID + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{String.valueOf(userId), hashPassword(currentPassword)},
                null, null, null);

        if (cursor.getCount() == 0) {
            cursor.close();
            return false; // Current password is wrong
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, hashPassword(newPassword));

        int rows = db.update(TABLE_USERS, values, COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    // ==================== TASK OPERATIONS ====================

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getTomorrowDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    private String getEndOfWeekDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 7);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    /**
     * Add a new task. Returns the task ID on success, -1 on failure.
     */
    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_USER_ID, task.getUserId());
        values.put(COL_TASK_TITLE, task.getTitle());
        values.put(COL_TASK_DESCRIPTION, task.getDescription());
        values.put(COL_TASK_DUE_DATE, task.getDueDate());
        values.put(COL_TASK_ALERT_TIME, task.getAlertTime());
        values.put(COL_TASK_PRIORITY, task.getPriority());
        values.put(COL_TASK_COMPLETED, task.isCompleted() ? 1 : 0);
        String timestamp = getCurrentTimestamp();
        values.put(COL_TASK_CREATED, timestamp);
        values.put(COL_TASK_UPDATED, timestamp);

        return db.insert(TABLE_TASKS, null, values);
    }

    /**
     * Update an existing task. Returns true on success.
     */
    public boolean updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_TITLE, task.getTitle());
        values.put(COL_TASK_DESCRIPTION, task.getDescription());
        values.put(COL_TASK_DUE_DATE, task.getDueDate());
        values.put(COL_TASK_ALERT_TIME, task.getAlertTime());
        values.put(COL_TASK_PRIORITY, task.getPriority());
        values.put(COL_TASK_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COL_TASK_UPDATED, getCurrentTimestamp());

        int rows = db.update(TABLE_TASKS, values, COL_TASK_ID + "=?",
                new String[]{String.valueOf(task.getId())});
        return rows > 0;
    }

    /**
     * Delete a task by ID. Returns true on success.
     */
    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TASKS, COL_TASK_ID + "=?",
                new String[]{String.valueOf(taskId)});
        return rows > 0;
    }

    /**
     * Get a single task by ID.
     */
    public Task getTaskById(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null,
                COL_TASK_ID + "=?", new String[]{String.valueOf(taskId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            Task task = cursorToTask(cursor);
            cursor.close();
            return task;
        }
        cursor.close();
        return null;
    }

    /**
     * Get all tasks for a user, ordered by due date.
     */
    public List<Task> getTasksByUser(int userId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null,
                COL_TASK_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, COL_TASK_DUE_DATE + " ASC, " + COL_TASK_CREATED + " DESC");

        while (cursor.moveToNext()) {
            tasks.add(cursorToTask(cursor));
        }
        cursor.close();
        return tasks;
    }

    /**
     * Get tasks by completion status.
     */
    public List<Task> getTasksByStatus(int userId, boolean isCompleted) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null,
                COL_TASK_USER_ID + "=? AND " + COL_TASK_COMPLETED + "=?",
                new String[]{String.valueOf(userId), isCompleted ? "1" : "0"},
                null, null, COL_TASK_DUE_DATE + " ASC, " + COL_TASK_CREATED + " DESC");

        while (cursor.moveToNext()) {
            tasks.add(cursorToTask(cursor));
        }
        cursor.close();
        return tasks;
    }

    /**
     * Get pending tasks due today.
     */
    public List<Task> getTasksDueToday(int userId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String today = getTodayDate();
        Cursor cursor = db.query(TABLE_TASKS, null,
                COL_TASK_USER_ID + "=? AND " + COL_TASK_DUE_DATE + "=? AND " + COL_TASK_COMPLETED + "=0",
                new String[]{String.valueOf(userId), today},
                null, null, COL_TASK_PRIORITY + " ASC");

        while (cursor.moveToNext()) {
            tasks.add(cursorToTask(cursor));
        }
        cursor.close();
        return tasks;
    }

    /**
     * Get pending tasks due after today (upcoming).
     */
    public List<Task> getTasksUpcoming(int userId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String today = getTodayDate();
        Cursor cursor = db.query(TABLE_TASKS, null,
                COL_TASK_USER_ID + "=? AND " + COL_TASK_DUE_DATE + ">? AND " + COL_TASK_COMPLETED + "=0",
                new String[]{String.valueOf(userId), today},
                null, null, COL_TASK_DUE_DATE + " ASC");

        while (cursor.moveToNext()) {
            tasks.add(cursorToTask(cursor));
        }
        cursor.close();
        return tasks;
    }

    /**
     * Get tasks due tomorrow.
     */
    public List<Task> getTasksDueTomorrow(int userId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String tomorrow = getTomorrowDate();
        Cursor cursor = db.query(TABLE_TASKS, null,
                COL_TASK_USER_ID + "=? AND " + COL_TASK_DUE_DATE + "=?",
                new String[]{String.valueOf(userId), tomorrow},
                null, null, COL_TASK_PRIORITY + " ASC");

        while (cursor.moveToNext()) {
            tasks.add(cursorToTask(cursor));
        }
        cursor.close();
        return tasks;
    }

    /**
     * Get tasks due within this week.
     */
    public List<Task> getTasksDueThisWeek(int userId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String today = getTodayDate();
        String endOfWeek = getEndOfWeekDate();
        Cursor cursor = db.query(TABLE_TASKS, null,
                COL_TASK_USER_ID + "=? AND " + COL_TASK_DUE_DATE + ">=? AND " + COL_TASK_DUE_DATE + "<=?",
                new String[]{String.valueOf(userId), today, endOfWeek},
                null, null, COL_TASK_DUE_DATE + " ASC");

        while (cursor.moveToNext()) {
            tasks.add(cursorToTask(cursor));
        }
        cursor.close();
        return tasks;
    }

    /**
     * Get tasks by priority.
     */
    public List<Task> getTasksByPriority(int userId, String priority) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null,
                COL_TASK_USER_ID + "=? AND " + COL_TASK_PRIORITY + "=?",
                new String[]{String.valueOf(userId), priority},
                null, null, COL_TASK_DUE_DATE + " ASC");

        while (cursor.moveToNext()) {
            tasks.add(cursorToTask(cursor));
        }
        cursor.close();
        return tasks;
    }

    /**
     * Search tasks by title or description.
     */
    public List<Task> searchTasks(int userId, String query) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "%" + query + "%";
        Cursor cursor = db.query(TABLE_TASKS, null,
                COL_TASK_USER_ID + "=? AND (" + COL_TASK_TITLE + " LIKE ? OR " + COL_TASK_DESCRIPTION + " LIKE ?)",
                new String[]{String.valueOf(userId), searchQuery, searchQuery},
                null, null, COL_TASK_DUE_DATE + " ASC");

        while (cursor.moveToNext()) {
            tasks.add(cursorToTask(cursor));
        }
        cursor.close();
        return tasks;
    }

    /**
     * Search tasks with combined filters.
     */
    public List<Task> searchTasksFiltered(int userId, String query, String statusFilter,
                                           String priorityFilter, String dueDateFilter) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder selection = new StringBuilder(COL_TASK_USER_ID + "=?");
        List<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(String.valueOf(userId));

        // Text search
        if (query != null && !query.trim().isEmpty()) {
            String searchQuery = "%" + query.trim() + "%";
            selection.append(" AND (").append(COL_TASK_TITLE).append(" LIKE ? OR ")
                    .append(COL_TASK_DESCRIPTION).append(" LIKE ?)");
            selectionArgs.add(searchQuery);
            selectionArgs.add(searchQuery);
        }

        // Status filter
        if (statusFilter != null) {
            if (statusFilter.equals("Pending")) {
                selection.append(" AND ").append(COL_TASK_COMPLETED).append("=0");
            } else if (statusFilter.equals("Completed")) {
                selection.append(" AND ").append(COL_TASK_COMPLETED).append("=1");
            }
        }

        // Priority filter
        if (priorityFilter != null) {
            selection.append(" AND ").append(COL_TASK_PRIORITY).append("=?");
            selectionArgs.add(priorityFilter.toUpperCase());
        }

        // Due date filter
        if (dueDateFilter != null) {
            switch (dueDateFilter) {
                case "Today":
                    selection.append(" AND ").append(COL_TASK_DUE_DATE).append("=?");
                    selectionArgs.add(getTodayDate());
                    break;
                case "Tomorrow":
                    selection.append(" AND ").append(COL_TASK_DUE_DATE).append("=?");
                    selectionArgs.add(getTomorrowDate());
                    break;
                case "This Week":
                    selection.append(" AND ").append(COL_TASK_DUE_DATE).append(">=? AND ")
                            .append(COL_TASK_DUE_DATE).append("<=?");
                    selectionArgs.add(getTodayDate());
                    selectionArgs.add(getEndOfWeekDate());
                    break;
            }
        }

        Cursor cursor = db.query(TABLE_TASKS, null,
                selection.toString(),
                selectionArgs.toArray(new String[0]),
                null, null, COL_TASK_DUE_DATE + " ASC, " + COL_TASK_CREATED + " DESC");

        while (cursor.moveToNext()) {
            tasks.add(cursorToTask(cursor));
        }
        cursor.close();
        return tasks;
    }

    /**
     * Get count of completed tasks for a user.
     */
    public int getCompletedTaskCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_TASKS +
                        " WHERE " + COL_TASK_USER_ID + "=? AND " + COL_TASK_COMPLETED + "=1",
                new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * Get total task count for a user.
     */
    public int getTotalTaskCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_TASKS +
                        " WHERE " + COL_TASK_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * Toggle task completion status.
     */
    public boolean toggleTaskCompletion(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Task task = getTaskById(taskId);
        if (task == null) return false;

        ContentValues values = new ContentValues();
        values.put(COL_TASK_COMPLETED, task.isCompleted() ? 0 : 1);
        values.put(COL_TASK_UPDATED, getCurrentTimestamp());

        int rows = db.update(TABLE_TASKS, values, COL_TASK_ID + "=?",
                new String[]{String.valueOf(taskId)});
        return rows > 0;
    }

    // ==================== HELPERS ====================

    private Task cursorToTask(Cursor cursor) {
        return new Task(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_TASK_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_TASK_USER_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_DUE_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_ALERT_TIME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_PRIORITY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_TASK_COMPLETED)) == 1,
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_CREATED)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_UPDATED))
        );
    }
}
