package com.example.motohub.activities.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motohub.R;
import com.example.motohub.models.Motorbike;
import com.example.motohub.repository.CartRepository;
import com.example.motohub.repository.FavoriteRepository;
import com.example.motohub.repository.MotorbikeRepository;

import java.text.NumberFormat;
import java.util.Locale;

public class MotorbikeDetailActivity extends AppCompatActivity {

    private ImageView imgMotorbike, imgFavorite, btnBack, btnCart;
    private ImageView btnDecrease, btnIncrease;
    private TextView tvBikeName, tvBikeBrand, tvBikePrice, tvStock, tvQuantity;
    private Button btnAddToCart, btnBuyNow;

    private Motorbike motorbike;
    private int userId;
    private int quantity = 1;

    private MotorbikeRepository motorbikeRepository;
    private CartRepository cartRepository;
    private FavoriteRepository favoriteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorbike_detail);

        initViews();
        initRepositories();
        loadUserData();
        loadMotorbikeData();
        setupListeners();
    }

    private void initViews() {
        imgMotorbike = findViewById(R.id.imgMotorbike);
        imgFavorite = findViewById(R.id.imgFavorite);
        btnBack = findViewById(R.id.btnBack);
        btnCart = findViewById(R.id.btnCart);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        
        tvBikeName = findViewById(R.id.tvBikeName);
        tvBikeBrand = findViewById(R.id.tvBikeBrand);
        tvBikePrice = findViewById(R.id.tvBikePrice);
        tvStock = findViewById(R.id.tvStock);
        tvQuantity = findViewById(R.id.tvQuantity);
        
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
    }

    private void initRepositories() {
        motorbikeRepository = new MotorbikeRepository(this);
        cartRepository = new CartRepository(this);
        favoriteRepository = new FavoriteRepository(this);
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
    }

    private void loadMotorbikeData() {
        int motorbikeId = getIntent().getIntExtra("motorbike_id", -1);
        if (motorbikeId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        motorbike = motorbikeRepository.getMotorbikeById(motorbikeId);
        if (motorbike == null) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayMotorbikeInfo();
    }

    private void displayMotorbikeInfo() {
        tvBikeName.setText(motorbike.getName());
        tvBikeBrand.setText(motorbike.getBrand());
        tvBikePrice.setText(formatPrice(motorbike.getPrice()));
        tvStock.setText(String.valueOf(motorbike.getStock()));

        loadImage(motorbike.getImage());
        updateFavoriteIcon();
    }

    private void updateFavoriteIcon() {
        if (userId > 0) {
            boolean isFavorite = favoriteRepository.isFavorite(userId, motorbike.getId());
            imgFavorite.setImageResource(
                    isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
            );
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });

        imgFavorite.setOnClickListener(v -> {
            if (userId <= 0) {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }

            favoriteRepository.toggleFavorite(userId, motorbike.getId());
            updateFavoriteIcon();
            
            // Show toast notification
            boolean isFavorite = favoriteRepository.isFavorite(userId, motorbike.getId());
            String message = isFavorite ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnIncrease.setOnClickListener(v -> {
            if (quantity < motorbike.getStock()) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Đã đạt số lượng tối đa", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddToCart.setOnClickListener(v -> addToCart());

        btnBuyNow.setOnClickListener(v -> buyNow());
    }

    private void addToCart() {
        if (userId <= 0) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        if (motorbike.getStock() < quantity) {
            Toast.makeText(this, "Không đủ hàng trong kho", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = cartRepository.addToCart(userId, motorbike.getId(), quantity);
        if (success) {
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        }
    }

    private void buyNow() {
        if (userId <= 0) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        if (motorbike.getStock() < quantity) {
            Toast.makeText(this, "Không đủ hàng trong kho", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("from_cart", false);
        intent.putExtra("motorbike_id", motorbike.getId());
        intent.putExtra("quantity", quantity);
        startActivity(intent);
    }

    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        return format.format(price) + " đ";
    }

    private void loadImage(String imageValue) {
        if (imageValue == null || imageValue.isEmpty()) {
            imgMotorbike.setImageResource(R.drawable.ic_bike_placeholder);
            return;
        }

        try {
            if (imageValue.startsWith("content://") || imageValue.startsWith("file://")) {
                imgMotorbike.setImageURI(android.net.Uri.parse(imageValue));
                return;
            }

            int resId = getResources().getIdentifier(
                    imageValue,
                    "drawable",
                    getPackageName()
            );

            if (resId != 0) {
                imgMotorbike.setImageResource(resId);
            } else {
                imgMotorbike.setImageResource(R.drawable.ic_bike_placeholder);
            }

        } catch (Exception e) {
            imgMotorbike.setImageResource(R.drawable.ic_bike_placeholder);
        }
    }
}
