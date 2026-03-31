package com.example.motohub.activities.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motohub.R;
import com.example.motohub.activities.admin.AdminHomeActivity;
import com.example.motohub.activities.auth.LoginActivity;
import com.example.motohub.activities.auth.RegisterActivity;

public class HomeActivity extends AppCompatActivity {

    private ImageView imgAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgAccount = findViewById(R.id.imgAccount);

        imgAccount.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Click avatar OK", Toast.LENGTH_SHORT).show();
            showAccountMenu();
        });
    }

    private void showAccountMenu() {
        SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        String role = prefs.getString("role", "");

        PopupMenu popupMenu = new PopupMenu(HomeActivity.this, imgAccount, Gravity.END);

        if (!isLoggedIn) {
            popupMenu.inflate(R.menu.menu_account_guest);
        } else if ("admin".equalsIgnoreCase(role)) {
            popupMenu.inflate(R.menu.menu_account_admin);
        } else {
            popupMenu.inflate(R.menu.menu_account_user);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menuLogin) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                return true;
            } else if (id == R.id.menuRegister) {
                startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
                return true;
            } else if (id == R.id.menuProfile) {
                Toast.makeText(HomeActivity.this, "Màn tài khoản làm sau", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.menuAdmin) {
                startActivity(new Intent(HomeActivity.this, AdminHomeActivity.class));
                return true;
            } else if (id == R.id.menuLogout) {
                logout();
                return true;
            }

            return false;
        });

        popupMenu.show();
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
        prefs.edit().clear().apply();
        Toast.makeText(HomeActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }
}