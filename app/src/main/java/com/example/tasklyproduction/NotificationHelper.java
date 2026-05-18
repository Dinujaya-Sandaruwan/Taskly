package com.example.tasklyproduction;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationHelper {

    /**
     * Schedule a notification reminder for a task.
     *
     * @param context  Application context
     * @param taskId   The task's database ID (used as alarm request code)
     * @param title    The task title shown in the notification
     * @param dueDate  Due date in "yyyy-MM-dd" format
     * @param alertTime Alert time in "HH:mm" format
     */
    public static void scheduleReminder(Context context, int taskId, String title,
                                         String dueDate, String alertTime) {
        if (dueDate == null || dueDate.isEmpty() || alertTime == null || alertTime.isEmpty()) {
            return; // Cannot schedule without date and time
        }

        try {
            // Parse date and time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date reminderDate = sdf.parse(dueDate + " " + alertTime);

            if (reminderDate == null) return;

            // Don't schedule if the time is in the past
            if (reminderDate.getTime() <= System.currentTimeMillis()) {
                return;
            }

            Intent intent = new Intent(context, TaskReminderReceiver.class);
            intent.putExtra("task_id", taskId);
            intent.putExtra("task_title", title);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, taskId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Android 12+: check if exact alarms are allowed
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP, reminderDate.getTime(), pendingIntent);
                    } else {
                        // Fall back to inexact alarm
                        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderDate.getTime(), pendingIntent);
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, reminderDate.getTime(), pendingIntent);
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP, reminderDate.getTime(), pendingIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancel a previously scheduled reminder.
     */
    public static void cancelReminder(Context context, int taskId) {
        Intent intent = new Intent(context, TaskReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, taskId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
