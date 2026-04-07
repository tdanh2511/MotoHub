package com.example.motohub.activities.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.adapters.MotorbikeAdapter;
import com.example.motohub.models.Motorbike;
import com.example.motohub.repository.FavoriteRepository;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements MotorbikeAdapter.OnMotorbikeClickListener, MotorbikeAdapter.OnFavoriteChangeListener {

    private ImageView btnBack;
    private RecyclerView rvFavorites;
    private LinearLayout layoutEmptyFavorites;

    private FavoriteRepository favoriteRepository;
    private MotorbikeAdapter motorbikeAdapter;
    private List<Motorbike> favoriteMotorbikes;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initViews();
        loadUserData();
        favoriteRepository = new FavoriteRepository(this);
        loadFavorites();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvFavorites = findViewById(R.id.rvFavorites);
        layoutEmptyFavorites = findViewById(R.id.layoutEmptyFavorites);

        rvFavorites.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
    }

    private void loadFavorites() {
        favoriteMotorbikes = favoriteRepository.getFavoriteMotorbikes(userId);
        
        if (favoriteMotorbikes.isEmpty()) {
            showEmptyState();
        } else {
            showFavorites();
        }
    }

    private void showEmptyState() {
        rvFavorites.setVisibility(View.GONE);
        layoutEmptyFavorites.setVisibility(View.VISIBLE);
    }

    private void showFavorites() {
        layoutEmptyFavorites.setVisibility(View.GONE);
        rvFavorites.setVisibility(View.VISIBLE);

        motorbikeAdapter = new MotorbikeAdapter(this, favoriteMotorbikes, this, userId, favoriteRepository, this);
        rvFavorites.setAdapter(motorbikeAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onMotorbikeClick(Motorbike motorbike) {
        Intent intent = new Intent(this, MotorbikeDetailActivity.class);
        intent.putExtra("motorbike_id", motorbike.getId());
        startActivity(intent);
    }

    @Override
    public void onFavoriteChanged() {
        // Reload the favorites list when a favorite is removed
        loadFavorites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload favorites when returning from detail screen
        loadFavorites();
    }
}
