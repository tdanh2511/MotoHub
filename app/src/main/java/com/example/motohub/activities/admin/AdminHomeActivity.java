package com.example.motohub.activities.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.motohub.R;
import com.example.motohub.activities.auth.LoginActivity;
import com.example.motohub.repository.MotorbikeRepository;
import com.example.motohub.repository.OrderRepository;
import com.example.motohub.repository.UserRepository;

public class AdminHomeActivity extends AppCompatActivity {

    private TextView tvAdminName, tvTotalBikes, tvTotalUsers, tvTotalOrders;
    private CardView btnManageBikes, btnManageUsers, btnManageRevenue;
    private Button btnLogout;
    private MotorbikeRepository motorbikeRepository;
    private UserRepository userRepository;
    private OrderRepository orderRepository;

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

        motorbikeRepository = new MotorbikeRepository(this);
        userRepository = new UserRepository(this);
        orderRepository = new OrderRepository(this);

        tvAdminName = findViewById(R.id.tvAdminName);
        tvTotalBikes = findViewById(R.id.tvTotalBikes);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        btnManageBikes = findViewById(R.id.btnManageBikes);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageRevenue = findViewById(R.id.btnManageRevenue);
        btnLogout = findViewById(R.id.btnLogout);

        tvAdminName.setText("Xin chào, " + fullname);
        loadStatistics();

        btnManageBikes.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageBikesActivity.class);
            startActivity(intent);
        });

        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageUsersActivity.class);
            startActivity(intent);
        });

        btnManageRevenue.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageRevenueActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics();
    }

    private void loadStatistics() {
        int totalBikes = motorbikeRepository.getAllMotorbikes().size();
        int totalUsers = userRepository.getAllUsers().size();
        int totalOrders = orderRepository.getAllOrders().size();

        tvTotalBikes.setText(String.valueOf(totalBikes));
        tvTotalUsers.setText(String.valueOf(totalUsers));
        tvTotalOrders.setText(String.valueOf(totalOrders));
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}