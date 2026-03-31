package com.example.motohub.activities.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motohub.R;
import com.example.motohub.activities.auth.LoginActivity;

public class AdminHomeActivity extends AppCompatActivity {

    private TextView tvAdminName;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
        String role = prefs.getString("role", "");
        String fullname = prefs.getString("fullname", "Admin");

        if (!"admin".equalsIgnoreCase(role)) {
            finish();
            return;
        }

        tvAdminName = findViewById(R.id.tvAdminName);
        btnLogout = findViewById(R.id.btnLogout);

        tvAdminName.setText("Xin chào, " + fullname);

        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}