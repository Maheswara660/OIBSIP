package com.maheswara660.taskify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.maheswara660.taskify.database.DatabaseHelper;
import com.maheswara660.taskify.model.User;
import com.maheswara660.taskify.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvSignUpLink;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Auto-login check
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, TaskListActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        View rootLayout = findViewById(R.id.rootLayout);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        dbHelper = new DatabaseHelper(this);

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUpLink = findViewById(R.id.tvSignUpLink);

        btnLogin.setOnClickListener(v -> performLogin());

        tvSignUpLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
    }

    private void performLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // Reset errors
        tilEmail.setError(null);
        tilPassword.setError(null);

        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.err_empty_fields));
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.err_empty_fields));
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, R.string.err_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        User user = dbHelper.checkUserLogin(email, password);

        if (user != null) {
            sessionManager.createLoginSession(user.getId(), user.getName(), user.getEmail());
            Toast.makeText(this, "Welcome back, " + user.getName() + "!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, TaskListActivity.class));
            finish();
        } else {
            Toast.makeText(this, R.string.err_invalid_credentials, Toast.LENGTH_LONG).show();
            tilEmail.setError(getString(R.string.err_invalid_credentials));
        }
    }
}
