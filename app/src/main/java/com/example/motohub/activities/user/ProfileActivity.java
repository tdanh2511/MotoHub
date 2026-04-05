package com.example.motohub.activities.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motohub.R;
import com.example.motohub.activities.auth.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREF_NAME = "motohub_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_ROLE = "role";

    private ImageView btnBack;
    private TextView txtUsername, txtFullname, txtRole;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtUsername = findViewById(R.id.txtUsername);
        txtFullname = findViewById(R.id.txtFullname);
        txtRole = findViewById(R.id.txtRole);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        String username = prefs.getString(KEY_USERNAME, "");
        String fullname = prefs.getString(KEY_FULLNAME, "");
        String role = prefs.getString(KEY_ROLE, "user");

        if (isLoggedIn) {
            txtUsername.setText(username);

            txtFullname.setText("");

            txtRole.setText("Vai trò: " + role);
        } else {
            txtUsername.setText("Chưa đăng nhập");
            txtFullname.setText("");
            txtRole.setText("");
        }

        findViewById(R.id.itemPersonalInfo).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, PersonalInfoActivity.class));
        });

        findViewById(R.id.itemChangePassword).setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        findViewById(R.id.itemWarranty).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, WarrantyPolicyActivity.class));
        });

        findViewById(R.id.itemPrivacyPolicy).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, PrivacyPolicyActivity.class));
        });

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}