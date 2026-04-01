package com.example.motohub.activities.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motohub.R;
import com.example.motohub.activities.admin.AdminHomeActivity;
import com.example.motohub.activities.user.HomeActivity;
import com.example.motohub.models.User;
import com.example.motohub.repository.UserRepository;

public class LoginActivity extends AppCompatActivity {

    private static final String PREF_NAME = "motohub_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_ROLE = "role";

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private UserRepository userRepository;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        if (isUserLoggedIn()) {
            redirectByRole(prefs.getString(KEY_ROLE, ""));
            return;
        }

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        findViewById(R.id.tvBackHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.tvGoRegister).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        userRepository = new UserRepository(this);

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private boolean isUserLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            edtUsername.setError("Vui lòng nhập tài khoản");
            edtUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        User user = userRepository.login(username, password);

        if (user == null) {
            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        saveSession(user);
        redirectByRole(user.getRole());
    }

    private void saveSession(User user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_FULLNAME, user.getFullname());
        editor.putString(KEY_ROLE, user.getRole());
        editor.apply();
    }

    private void redirectByRole(String role) {
        Intent intent;
        if ("admin".equalsIgnoreCase(role)) {
            intent = new Intent(this, AdminHomeActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}