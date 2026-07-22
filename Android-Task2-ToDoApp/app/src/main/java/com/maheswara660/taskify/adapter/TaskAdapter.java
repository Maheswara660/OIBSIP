package com.maheswara660.taskify.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.maheswara660.taskify.R;
import com.maheswara660.taskify.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskChangeListener {
        void onTaskStatusChanged(Task task, boolean isCompleted);
        void onTaskDeleteRequested(Task task);
    }

    private List<Task> taskList;
    private final OnTaskChangeListener listener;

    public TaskAdapter(List<Task> taskList, OnTaskChangeListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    public void updateTasks(List<Task> newTasks) {
        this.taskList = newTasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCheckBox cbCompleted;
        private final TextView tvTaskText;
        private final ImageButton btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbCompleted = itemView.findViewById(R.id.cbTaskCompleted);
            tvTaskText = itemView.findViewById(R.id.tvTaskText);
            btnDelete = itemView.findViewById(R.id.btnDeleteTask);
        }

        public void bind(final Task task, final OnTaskChangeListener listener) {
            tvTaskText.setText(task.getTaskText());

            // Remove listener before setting checked state to avoid trigger loop
            cbCompleted.setOnCheckedChangeListener(null);
            cbCompleted.setChecked(task.isCompleted());

            updateTextStyling(task.isCompleted());

            cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setCompleted(isChecked);
                updateTextStyling(isChecked);
                if (listener != null) {
                    listener.onTaskStatusChanged(task, isChecked);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskDeleteRequested(task);
                }
            });
        }

        private void updateTextStyling(boolean isCompleted) {
            if (isCompleted) {
                tvTaskText.setPaintFlags(tvTaskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTaskText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_completed));
            } else {
                tvTaskText.setPaintFlags(tvTaskText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTaskText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_primary));
            }
        }
    }
}
