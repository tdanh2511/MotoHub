package com.example.motohub.activities.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.adapters.UserOrderAdapter;
import com.example.motohub.models.Order;
import com.example.motohub.repository.OrderRepository;

import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity implements UserOrderAdapter.OnOrderClickListener {

    private static final String PREF_NAME = "motohub_session";
    private static final String KEY_USER_ID = "user_id";

    private RecyclerView recyclerOrders;
    private LinearLayout layoutEmpty;
    private UserOrderAdapter adapter;
    private OrderRepository orderRepository;

    private Button btnFilterAll, btnFilterPending, btnFilterProcessing, btnFilterCompleted, btnFilterCancelled;
    private String currentFilter = "all";
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        userId = prefs.getInt(KEY_USER_ID, -1);

        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderRepository = new OrderRepository(this);

        initViews();
        setupFilterButtons();
        loadOrders();

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        recyclerOrders = findViewById(R.id.recyclerOrders);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));

        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterPending = findViewById(R.id.btnFilterPending);
        btnFilterProcessing = findViewById(R.id.btnFilterProcessing);
        btnFilterCompleted = findViewById(R.id.btnFilterCompleted);
        btnFilterCancelled = findViewById(R.id.btnFilterCancelled);
    }

    private void setupFilterButtons() {
        btnFilterAll.setOnClickListener(v -> {
            currentFilter = "all";
            updateFilterButtonStyles();
            loadOrders();
        });

        btnFilterPending.setOnClickListener(v -> {
            currentFilter = "pending";
            updateFilterButtonStyles();
            loadOrders();
        });

        btnFilterProcessing.setOnClickListener(v -> {
            currentFilter = "processing";
            updateFilterButtonStyles();
            loadOrders();
        });

        btnFilterCompleted.setOnClickListener(v -> {
            currentFilter = "completed";
            updateFilterButtonStyles();
            loadOrders();
        });

        btnFilterCancelled.setOnClickListener(v -> {
            currentFilter = "cancelled";
            updateFilterButtonStyles();
            loadOrders();
        });
    }

    private void updateFilterButtonStyles() {
        resetFilterButton(btnFilterAll);
        resetFilterButton(btnFilterPending);
        resetFilterButton(btnFilterProcessing);
        resetFilterButton(btnFilterCompleted);
        resetFilterButton(btnFilterCancelled);

        Button selectedButton = null;
        switch (currentFilter) {
            case "all": selectedButton = btnFilterAll; break;
            case "pending": selectedButton = btnFilterPending; break;
            case "processing": selectedButton = btnFilterProcessing; break;
            case "completed": selectedButton = btnFilterCompleted; break;
            case "cancelled": selectedButton = btnFilterCancelled; break;
        }

        if (selectedButton != null) {
            selectedButton.setBackgroundTintList(getColorStateList(R.color.red));
            selectedButton.setTextColor(getColor(android.R.color.holo_red_dark));
        }
    }

    private void resetFilterButton(Button button) {
        button.setBackgroundTintList(getColorStateList(R.color.background_light));
        button.setTextColor(getColor(R.color.text_secondary));
    }

    private void loadOrders() {
        List<Order> orders;

        if (currentFilter.equals("all")) {
            orders = orderRepository.getOrdersByUserId(userId);
        } else {
            orders = orderRepository.getOrdersByUserIdAndStatus(userId, currentFilter);
        }

        if (orders.isEmpty()) {
            recyclerOrders.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerOrders.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new UserOrderAdapter(this, orders, this);
                recyclerOrders.setAdapter(adapter);
            } else {
                adapter.updateOrders(orders);
            }
        }
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("order_id", order.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}
