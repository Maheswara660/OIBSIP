package com.maheswara660.taskify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.maheswara660.taskify.adapter.TaskAdapter;
import com.maheswara660.taskify.database.DatabaseHelper;
import com.maheswara660.taskify.model.Task;
import com.maheswara660.taskify.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity implements TaskAdapter.OnTaskChangeListener {

    private TextView tvUserGreeting, tvProgressSubtitle;
    private LinearProgressIndicator progressIndicator;
    private ImageButton btnLogout;
    private RecyclerView recyclerViewTasks;
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabAddTask;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_task_list);

        View rootLayout = findViewById(R.id.rootLayout);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        dbHelper = new DatabaseHelper(this);
        currentUserId = sessionManager.getUserId();

        if (currentUserId <= 0) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        tvUserGreeting = findViewById(R.id.tvUserGreeting);
        tvProgressSubtitle = findViewById(R.id.tvProgressSubtitle);
        progressIndicator = findViewById(R.id.progressIndicator);
        btnLogout = findViewById(R.id.btnLogout);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        fabAddTask = findViewById(R.id.fabAddTask);

        // Display user name
        String userName = sessionManager.getUserName();
        tvUserGreeting.setText(getString(R.string.hello_user, userName));

        // Setup RecyclerView
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, this);
        recyclerViewTasks.setAdapter(taskAdapter);

        // Listeners
        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
        fabAddTask.setOnClickListener(v -> showAddTaskDialog());

        loadUserTasks();
    }

    private void loadUserTasks() {
        if (currentUserId == -1)
            return;

        taskList = dbHelper.getTasksForUser(currentUserId);
        taskAdapter.updateTasks(taskList);

        updateProgressAndEmptyState();
    }

    private void updateProgressAndEmptyState() {
        int totalTasks = taskList.size();
        int completedCount = 0;

        for (Task task : taskList) {
            if (task.isCompleted()) {
                completedCount++;
            }
        }

        if (totalTasks == 0) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerViewTasks.setVisibility(View.GONE);
            tvProgressSubtitle.setText("0 of 0 tasks completed");
            progressIndicator.setProgress(0);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerViewTasks.setVisibility(View.VISIBLE);

            tvProgressSubtitle.setText(getString(R.string.tasks_progress, completedCount, totalTasks));
            int percentage = (int) (((float) completedCount / totalTasks) * 100);
            progressIndicator.setProgress(percentage);
        }
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextInputLayout tilTaskInput = dialogView.findViewById(R.id.tilTaskInput);
        TextInputEditText etTaskInput = dialogView.findViewById(R.id.etTaskInput);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelTask);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSaveTask);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String taskText = etTaskInput.getText() != null ? etTaskInput.getText().toString().trim() : "";

            if (TextUtils.isEmpty(taskText)) {
                tilTaskInput.setError("Please enter task description");
                Toast.makeText(TaskListActivity.this, "Task description cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Task newTask = new Task(currentUserId, taskText, false, System.currentTimeMillis());
            boolean success = dbHelper.addTask(newTask);

            if (success) {
                Toast.makeText(TaskListActivity.this, R.string.msg_task_added, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadUserTasks();
            } else {
                Toast.makeText(TaskListActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onTaskStatusChanged(Task task, boolean isCompleted) {
        dbHelper.updateTaskCompletion(task.getId(), isCompleted);
        updateProgressAndEmptyState();
    }

    @Override
    public void onTaskDeleteRequested(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteTask(task.getId());
                    if (deleted) {
                        Toast.makeText(TaskListActivity.this, R.string.msg_task_deleted, Toast.LENGTH_SHORT).show();
                        loadUserTasks();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_logout_title)
                .setMessage(R.string.confirm_logout_message)
                .setPositiveButton("Logout", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        sessionManager.logoutUser();
        Toast.makeText(this, R.string.msg_logged_out, Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(TaskListActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
