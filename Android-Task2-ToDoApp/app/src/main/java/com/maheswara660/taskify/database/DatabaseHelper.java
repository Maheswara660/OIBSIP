package com.maheswara660.taskify.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.maheswara660.taskify.model.Task;
import com.maheswara660.taskify.model.User;
import com.maheswara660.taskify.utils.PasswordUtils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "taskify.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_TASKS = "tasks";

    // Users Table Columns
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD_HASH = "passwordHash";

    // Tasks Table Columns
    public static final String COLUMN_TASK_ID = "id";
    public static final String COLUMN_TASK_USER_ID = "userId";
    public static final String COLUMN_TASK_TEXT = "taskText";
    public static final String COLUMN_TASK_IS_COMPLETED = "isCompleted";
    public static final String COLUMN_TASK_CREATED_AT = "createdAt";

    // Create Table Users SQL
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_NAME + " TEXT NOT NULL, "
            + COLUMN_USER_EMAIL + " TEXT NOT NULL UNIQUE, "
            + COLUMN_USER_PASSWORD_HASH + " TEXT NOT NULL"
            + ");";

    // Create Table Tasks SQL
    private static final String CREATE_TABLE_TASKS = "CREATE TABLE " + TABLE_TASKS + " ("
            + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TASK_USER_ID + " INTEGER NOT NULL, "
            + COLUMN_TASK_TEXT + " TEXT NOT NULL, "
            + COLUMN_TASK_IS_COMPLETED + " INTEGER DEFAULT 0, "
            + COLUMN_TASK_CREATED_AT + " INTEGER DEFAULT 0, "
            + "FOREIGN KEY(" + COLUMN_TASK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE"
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_TASKS);
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

    // ==================== USER OPERATIONS ====================

    /**
     * Registers a new user with hashed password.
     * @return User ID if successful, -1 otherwise.
     */
    public long registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName().trim());
        values.put(COLUMN_USER_EMAIL, user.getEmail().trim().toLowerCase());
        values.put(COLUMN_USER_PASSWORD_HASH, PasswordUtils.hashPassword(user.getPasswordHash()));

        long id = db.insert(TABLE_USERS, null, values);
        if (id != -1) {
            user.setId((int) id);
        }
        return id;
    }

    /**
     * Checks if email is already registered.
     */
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID},
                COLUMN_USER_EMAIL + "=?", new String[]{email.trim().toLowerCase()},
                null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    /**
     * Authenticates user with email or username and raw password.
     */
    public User checkUserLogin(String emailOrName, String rawPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = PasswordUtils.hashPassword(rawPassword);
        String queryInput = emailOrName != null ? emailOrName.trim() : "";

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID, COLUMN_USER_NAME, COLUMN_USER_EMAIL},
                "(" + COLUMN_USER_EMAIL + "=? OR " + COLUMN_USER_NAME + "=?) AND " + COLUMN_USER_PASSWORD_HASH + "=?",
                new String[]{queryInput.toLowerCase(), queryInput, hashedPassword},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String userEmail = cursor.getString(2);

            user = new User(id, name, userEmail, "");
        }
        if (cursor != null) {
            cursor.close();
        }
        return user;
    }

    // ==================== TASK OPERATIONS ====================

    /**
     * Adds a new task for specified user.
     */
    public boolean addTask(Task task) {
        if (task == null || task.getUserId() <= 0) {
            android.util.Log.e("DatabaseHelper", "Cannot add task: Invalid userId=" + (task != null ? task.getUserId() : -1));
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_USER_ID, task.getUserId());
        values.put(COLUMN_TASK_TEXT, task.getTaskText().trim());
        values.put(COLUMN_TASK_IS_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_TASK_CREATED_AT, task.getCreatedAt());

        try {
            long result = db.insert(TABLE_TASKS, null, values);
            return result != -1;
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error inserting task into SQLite", e);
            return false;
        }
    }

    /**
     * Retrieves all tasks for a user, sorted by completion status (uncompleted first) and creation date.
     */
    public List<Task> getTasksForUser(int userId) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS,
                new String[]{COLUMN_TASK_ID, COLUMN_TASK_USER_ID, COLUMN_TASK_TEXT, COLUMN_TASK_IS_COMPLETED, COLUMN_TASK_CREATED_AT},
                COLUMN_TASK_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                COLUMN_TASK_IS_COMPLETED + " ASC, " + COLUMN_TASK_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_TASK_ID);
            int userIdIndex = cursor.getColumnIndex(COLUMN_TASK_USER_ID);
            int textIndex = cursor.getColumnIndex(COLUMN_TASK_TEXT);
            int completedIndex = cursor.getColumnIndex(COLUMN_TASK_IS_COMPLETED);
            int createdAtIndex = cursor.getColumnIndex(COLUMN_TASK_CREATED_AT);

            do {
                int id = cursor.getInt(idIndex);
                int uId = cursor.getInt(userIdIndex);
                String text = cursor.getString(textIndex);
                boolean completed = cursor.getInt(completedIndex) == 1;
                long createdAt = cursor.getLong(createdAtIndex);

                taskList.add(new Task(id, uId, text, completed, createdAt));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return taskList;
    }

    /**
     * Updates task completion state in SQLite database.
     */
    public boolean updateTaskCompletion(int taskId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_IS_COMPLETED, isCompleted ? 1 : 0);

        int rows = db.update(TABLE_TASKS, values, COLUMN_TASK_ID + "=?", new String[]{String.valueOf(taskId)});
        return rows > 0;
    }

    /**
     * Deletes a task from SQLite database.
     */
    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TASKS, COLUMN_TASK_ID + "=?", new String[]{String.valueOf(taskId)});
        return rows > 0;
    }
}
