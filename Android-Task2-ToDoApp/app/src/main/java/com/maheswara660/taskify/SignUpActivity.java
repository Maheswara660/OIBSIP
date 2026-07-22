package com.maheswara660.taskify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvSignInLink;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        dbHelper = new DatabaseHelper(this);

        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvSignInLink = findViewById(R.id.tvSignInLink);

        btnRegister.setOnClickListener(v -> performRegistration());

        tvSignInLink.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim()
                : "";

        // Reset errors
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, R.string.err_empty_fields, Toast.LENGTH_SHORT).show();
            if (TextUtils.isEmpty(name))
                tilName.setError(getString(R.string.err_empty_fields));
            if (TextUtils.isEmpty(email))
                tilEmail.setError(getString(R.string.err_empty_fields));
            if (TextUtils.isEmpty(password))
                tilPassword.setError(getString(R.string.err_empty_fields));
            if (TextUtils.isEmpty(confirmPassword))
                tilConfirmPassword.setError(getString(R.string.err_empty_fields));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 4) {
            tilPassword.setError(getString(R.string.err_short_password));
            Toast.makeText(this, R.string.err_short_password, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.err_passwords_dont_match));
            Toast.makeText(this, R.string.err_passwords_dont_match, Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.isEmailExists(email)) {
            tilEmail.setError(getString(R.string.err_email_exists));
            Toast.makeText(this, R.string.err_email_exists, Toast.LENGTH_LONG).show();
            return;
        }

        User newUser = new User(name, email, password);
        long newUserId = dbHelper.registerUser(newUser);

        if (newUserId != -1) {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.createLoginSession((int) newUserId, name, email);
            Toast.makeText(this, "Welcome, " + name + "!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignUpActivity.this, TaskListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
