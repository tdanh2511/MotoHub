package com.example.motohub.activities.auth;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motohub.R;
import com.example.motohub.activities.user.HomeActivity;
import com.example.motohub.database.MotoHubDbHelper;
import com.example.motohub.models.User;
import com.example.motohub.repository.UserRepository;

public class RegisterActivity extends AppCompatActivity {

    private static final String PREF_NAME = "motohub_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_ROLE = "role";

    private EditText edtUsername, edtPassword, edtConfirmPassword;
    private Button btnRegister;

    private UserRepository userRepository;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        userRepository = new UserRepository(this);
        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        findViewById(R.id.tvBackHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.tvGoLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirm = edtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) ||
                TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirm)) {
            Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("user");

        boolean success = registerUser(user);

        if (success) {
            User loggedInUser = userRepository.login(username, password);

            if (loggedInUser != null) {
                saveSession(loggedInUser);

                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Đăng ký thành công nhưng tự đăng nhập thất bại", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Username đã tồn tại", Toast.LENGTH_SHORT).show();
        }
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

    private boolean registerUser(User user) {
        try {
            return userRepositoryInsert(user);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean userRepositoryInsert(User user) {
        SQLiteDatabase db = new MotoHubDbHelper(this).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("fullname", user.getFullname());
        values.put("role", user.getRole());

        long result = db.insert("users", null, values);
        db.close();

        return result != -1;
    }
}