package com.example.motohub.activities.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motohub.R;
import com.example.motohub.models.User;
import com.example.motohub.repository.UserRepository;

public class ProfileActivity extends AppCompatActivity {

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

        // lấy userId từ session
        SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            UserRepository repo = new UserRepository(this);

            // ⚠️ hiện tại repo chưa có hàm getById → t sẽ fix bên dưới
            User user = repo.getUserById(userId);

            if (user != null) {
                txtUsername.setText("Username: " + user.getUsername());
                txtFullname.setText("Họ tên: " + user.getFullname());
                txtRole.setText("Vai trò: " + user.getRole());
            }
        }

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            finish(); // quay lại Home
        });
    }
}