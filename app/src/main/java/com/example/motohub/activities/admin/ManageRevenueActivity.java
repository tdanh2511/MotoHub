package com.example.motohub.activities.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.adapters.OrderAdapter;
import com.example.motohub.models.Order;
import com.example.motohub.repository.OrderRepository;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ManageRevenueActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private OrderRepository orderRepository;
    private TextView tvTotalRevenue, tvTotalOrders, tvCompletedOrders;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_revenue);

        orderRepository = new OrderRepository(this);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvCompletedOrders = findViewById(R.id.tvCompletedOrders);
        btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadOrders();
        loadStatistics();

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadOrders() {
        List<Order> orders = orderRepository.getAllOrders();
        adapter = new OrderAdapter(this, orders, new OrderAdapter.OnOrderActionListener() {
            @Override
            public void onUpdateStatus(Order order) {
                showUpdateStatusDialog(order);
            }

            @Override
            public void onDelete(Order order) {
                showDeleteConfirmDialog(order);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadStatistics() {
        List<Order> allOrders = orderRepository.getAllOrders();
        int totalOrders = allOrders.size();
        
        long completedCount = allOrders.stream()
                .filter(order -> "completed".equals(order.getStatus()))
                .count();
        
        double totalRevenue = orderRepository.getTotalRevenue();

        tvTotalOrders.setText(String.valueOf(totalOrders));
        tvCompletedOrders.setText(String.valueOf(completedCount));
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedRevenue = formatter.format(totalRevenue).replace("₫", "đ");
        tvTotalRevenue.setText(formattedRevenue);
    }

    private void showUpdateStatusDialog(Order order) {
        String[] statuses = {"pending", "processing", "completed", "cancelled"};
        String[] statusLabels = {"Chờ xử lý", "Đang xử lý", "Hoàn thành", "Đã hủy"};

        new AlertDialog.Builder(this)
                .setTitle("Cập nhật trạng thái đơn hàng")
                .setItems(statusLabels, (dialog, which) -> {
                    String newStatus = statuses[which];
                    int result = orderRepository.updateOrderStatus(order.getId(), newStatus);
                    
                    if (result > 0) {
                        loadOrders();
                        loadStatistics();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteConfirmDialog(Order order) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa đơn hàng này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int result = orderRepository.deleteOrder(order.getId());
                    if (result > 0) {
                        loadOrders();
                        loadStatistics();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
