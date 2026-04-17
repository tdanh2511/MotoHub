package com.example.motohub.activities.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.adapters.CartAdapter;
import com.example.motohub.models.CartItem;
import com.example.motohub.repository.CartRepository;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemChangeListener {

    private ImageView btnBack;
    private RecyclerView rvCartItems;
    private LinearLayout layoutEmptyCart, layoutCartSummary;
    private TextView tvTotalPrice;
    private Button btnCheckout;

    private CartRepository cartRepository;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        loadUserData();
        cartRepository = new CartRepository(this);
        loadCartData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvCartItems = findViewById(R.id.rvCartItems);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
        layoutCartSummary = findViewById(R.id.layoutCartSummary);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);

        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
    }

    private void loadCartData() {
        cartItems = cartRepository.getCartItems(userId);

        if (cartItems.isEmpty()) {
            showEmptyCart();
        } else {
            showCartItems();
        }
    }

    private void showEmptyCart() {
        rvCartItems.setVisibility(View.GONE);
        layoutCartSummary.setVisibility(View.GONE);
        layoutEmptyCart.setVisibility(View.VISIBLE);
    }

    private void showCartItems() {
        layoutEmptyCart.setVisibility(View.GONE);
        rvCartItems.setVisibility(View.VISIBLE);
        layoutCartSummary.setVisibility(View.VISIBLE);

        if (cartAdapter == null) {
            cartAdapter = new CartAdapter(this, cartItems, this);
            rvCartItems.setAdapter(cartAdapter);
        } else {
            cartAdapter.updateCartItems(cartItems);
        }

        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = cartRepository.getCartTotal(userId);
        tvTotalPrice.setText(formatPrice(total));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("from_cart", true);
            startActivity(intent);
        });
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        boolean success = cartRepository.updateQuantity(item.getId(), newQuantity);
        if (success) {
            // Reload cart data from database to ensure consistency
            loadCartData();
            Toast.makeText(this, "Đã cập nhật số lượng", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemDeleted(CartItem item) {
        Log.d("Cart", "Before delete - cartItemId = " + item.getId()
                + ", motorbikeId = " + item.getMotorbikeId());

        boolean success = cartRepository.removeFromCart(item.getId());

        // Log.d("Cart", "Delete result = " + success);

        if (success) {
            Toast.makeText(this, "Đã xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
            loadCartData();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        // Log.e("Cart", "Delete failed - cartItemId = " + item.getId()
        //      + ", motorbikeId = " + item.getMotorbikeId());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartData();
    }

    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        return format.format(price) + " đ";
    }
}
