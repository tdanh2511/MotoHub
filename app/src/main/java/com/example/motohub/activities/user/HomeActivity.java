package com.example.motohub.activities.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.activities.admin.AdminHomeActivity;
import com.example.motohub.activities.auth.LoginActivity;
import com.example.motohub.activities.auth.RegisterActivity;
import com.example.motohub.adapters.MotorbikeAdapter;
import com.example.motohub.models.Motorbike;
import com.example.motohub.repository.FavoriteRepository;
import com.example.motohub.repository.MotorbikeRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rcvMotorbikes;
    private MotorbikeAdapter adapter;
    private List<Motorbike> motorbikeList;
    private MotorbikeRepository repository;
    private FavoriteRepository favoriteRepository;
    private int userId;

    private ImageView imgSearch;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgSearch = findViewById(R.id.imgSearch);
        bottomNav = findViewById(R.id.bottomNav);
        rcvMotorbikes = findViewById(R.id.rcvMotorbikes);

        SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        repository = new MotorbikeRepository(this);
        favoriteRepository = new FavoriteRepository(this);
        motorbikeList = repository.getAllMotorbikes();

        adapter = new MotorbikeAdapter(
                this,
                motorbikeList,
                motorbike -> {
                    Intent intent = new Intent(HomeActivity.this, MotorbikeDetailActivity.class);
                    intent.putExtra("motorbike_id", motorbike.getId());
                    startActivity(intent);
                },
                userId,
                favoriteRepository
        );

        rcvMotorbikes.setLayoutManager(new GridLayoutManager(this, 2));
        rcvMotorbikes.setAdapter(adapter);

        imgSearch.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
        });

        // chọn item ở bottom nav
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
                return true;
            } else if (id == R.id.nav_favorite) {
                startActivity(new Intent(HomeActivity.this, FavoritesActivity.class));
                return true;
            } else if (id == R.id.nav_account) {
                showAccountMenu();
                return true;
            }

            return false;
        });
    }

    private void showAccountMenu() {
        SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        String role = prefs.getString("role", "");

        PopupMenu popupMenu = new PopupMenu(HomeActivity.this, bottomNav, Gravity.END);

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
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
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
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}