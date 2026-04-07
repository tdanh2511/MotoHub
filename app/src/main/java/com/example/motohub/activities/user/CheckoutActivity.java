package com.example.motohub.activities.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motohub.R;
import com.example.motohub.models.CartItem;
import com.example.motohub.models.Motorbike;
import com.example.motohub.repository.CartRepository;
import com.example.motohub.repository.MotorbikeRepository;
import com.example.motohub.repository.OrderRepository;
import com.example.motohub.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputEditText etFullName, etPhone, etAddress;
    private TextView tvSubtotal, tvTotal;
    private Button btnPlaceOrder;

    private boolean fromCart;
    private int motorbikeId;
    private int quantity;
    private int userId;
    private String fullName;

    private CartRepository cartRepository;
    private MotorbikeRepository motorbikeRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initViews();
        initRepositories();
        loadUserData();
        loadIntentData();
        loadOrderSummary();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotal = findViewById(R.id.tvTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
    }

    private void initRepositories() {
        cartRepository = new CartRepository(this);
        motorbikeRepository = new MotorbikeRepository(this);
        orderRepository = new OrderRepository(this);
        userRepository = new UserRepository(this);
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        fullName = prefs.getString("fullname", "");

        etFullName.setText(fullName);
        
        // Load user info from database
        var user = userRepository.getUserById(userId);
        if (user != null) {
            if (!TextUtils.isEmpty(user.getPhone())) {
                etPhone.setText(user.getPhone());
            }
            if (!TextUtils.isEmpty(user.getAddress())) {
                etAddress.setText(user.getAddress());
            }
        }
    }

    private void loadIntentData() {
        Intent intent = getIntent();
        fromCart = intent.getBooleanExtra("from_cart", false);
        
        if (!fromCart) {
            motorbikeId = intent.getIntExtra("motorbike_id", -1);
            quantity = intent.getIntExtra("quantity", 1);
        }
    }

    private void loadOrderSummary() {
        double total;
        
        if (fromCart) {
            total = cartRepository.getCartTotal(userId);
        } else {
            Motorbike motorbike = motorbikeRepository.getMotorbikeById(motorbikeId);
            total = motorbike.getPrice() * quantity;
        }

        tvSubtotal.setText(formatPrice(total));
        tvTotal.setText(formatPrice(total));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        String name = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etFullName.setError("Vui lòng nhập họ tên");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Vui lòng nhập địa chỉ");
            etAddress.requestFocus();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (fromCart) {
            // Create orders from cart
            List<CartItem> cartItems = cartRepository.getCartItems(userId);
            
            for (CartItem item : cartItems) {
                long orderId = orderRepository.createOrder(
                        userId,
                        item.getMotorbikeId(),
                        name,
                        item.getMotorbike().getName(),
                        item.getMotorbike().getPrice(),
                        item.getQuantity(),
                        currentDate,
                        "pending",
                        phone,
                        address
                );

                if (orderId == -1) {
                    Toast.makeText(this, "Có lỗi xảy ra khi tạo đơn hàng", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Clear cart
            cartRepository.clearCart(userId);
        } else {
            // Create single order
            Motorbike motorbike = motorbikeRepository.getMotorbikeById(motorbikeId);
            
            long orderId = orderRepository.createOrder(
                    userId,
                    motorbikeId,
                    name,
                    motorbike.getName(),
                    motorbike.getPrice(),
                    quantity,
                    currentDate,
                    "pending",
                    phone,
                    address
            );

            if (orderId == -1) {
                Toast.makeText(this, "Có lỗi xảy ra khi tạo đơn hàng", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
        
        // Go to home
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        return format.format(price) + " đ";
    }
}
