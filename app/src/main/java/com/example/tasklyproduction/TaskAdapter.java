package com.example.tasklyproduction;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskStatusToggle(Task task, int position);
    }

    public TaskAdapter(List<Task> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.tvTaskTitle.setText(task.getTitle());

        // Show due date or empty
        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            holder.tvTaskDueDate.setText(task.getDueDate());
            holder.tvTaskDueDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvTaskDueDate.setVisibility(View.GONE);
        }

        // Completion status visual
        if (task.isCompleted()) {
            holder.ivTaskStatus.setImageResource(R.drawable.ic_check_circle);
            holder.tvTaskTitle.setPaintFlags(
                    holder.tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.ivTaskStatus.setImageResource(R.drawable.ic_circle_outline);
            holder.tvTaskTitle.setPaintFlags(
                    holder.tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Click on card -> edit task
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task);
            }
        });

        // Click on status icon -> toggle completion
        holder.ivTaskStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskStatusToggle(task, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.taskList = newTasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTaskStatus;
        TextView tvTaskTitle;
        TextView tvTaskDueDate;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTaskStatus = itemView.findViewById(R.id.ivTaskStatus);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskDueDate = itemView.findViewById(R.id.tvTaskDueDate);
        }
    }
}
