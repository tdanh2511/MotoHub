package com.example.motohub.activities.user;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motohub.R;
import com.example.motohub.models.Order;
import com.example.motohub.repository.OrderRepository;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private static final String PREF_NAME = "motohub_session";
    private static final String KEY_USER_ID = "user_id";

    private OrderRepository orderRepository;
    private Order currentOrder;
    private int orderId;

    private TextView tvOrderIdHeader, tvStatus, tvMotorbikeName, tvQuantity, tvPrice;
    private TextView tvCustomerName, tvPhone, tvAddress, tvOrderDate;
    private ImageView imgMotorbike;
    private Button btnCancelOrder, btnRequestRefund;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderRepository = new OrderRepository(this);

        initViews();
        loadOrderDetail();

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        tvOrderIdHeader = findViewById(R.id.tvOrderIdHeader);
        tvStatus = findViewById(R.id.tvStatus);
        tvMotorbikeName = findViewById(R.id.tvMotorbikeName);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvPrice = findViewById(R.id.tvPrice);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        imgMotorbike = findViewById(R.id.imgMotorbike);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnRequestRefund = findViewById(R.id.btnRequestRefund);

        btnCancelOrder.setOnClickListener(v -> showCancelDialog());
        btnRequestRefund.setOnClickListener(v -> showRefundDialog());
    }

    private void loadOrderDetail() {
        currentOrder = orderRepository.getOrderById(orderId);

        if (currentOrder == null) {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvOrderIdHeader.setText("Đơn hàng #" + currentOrder.getId());
        tvMotorbikeName.setText(currentOrder.getMotorbikeName());
        tvQuantity.setText("Số lượng: " + currentOrder.getQuantity());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvPrice.setText(formatter.format(currentOrder.getPrice()));

        tvCustomerName.setText(currentOrder.getCustomerName());
        tvPhone.setText(currentOrder.getPhone());
        tvAddress.setText(currentOrder.getAddress());
        tvOrderDate.setText(currentOrder.getOrderDate());

        String statusText = getStatusText(currentOrder.getStatus());
        tvStatus.setText(statusText);
        tvStatus.setBackgroundColor(getStatusColor(currentOrder.getStatus()));

        int imageResId = getMotorbikeImageResource(currentOrder.getMotorbikeId());
        imgMotorbike.setImageResource(imageResId);

        updateActionButtons();
    }

    private void updateActionButtons() {
        String status = currentOrder.getStatus();

        btnCancelOrder.setVisibility(View.GONE);
        btnRequestRefund.setVisibility(View.GONE);

        if (status.equals("pending") || status.equals("processing")) {
            btnCancelOrder.setVisibility(View.VISIBLE);
        }

        if (status.equals("completed")) {
            btnRequestRefund.setVisibility(View.VISIBLE);
        }

        // Hiển thị thông báo nếu refund bị từ chối
        if (status.equals("refund_rejected")) {
            new AlertDialog.Builder(this)
                    .setTitle("Thông báo")
                    .setMessage("Yêu cầu hoàn trả của bạn đã bị từ chối. Vui lòng liên hệ với chúng tôi để biết thêm chi tiết.")
                    .setPositiveButton("Đã hiểu", null)
                    .show();
        }
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này không?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    cancelOrder();
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void showRefundDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu hoàn trả")
                .setMessage("Bạn có muốn yêu cầu hoàn trả đơn hàng này không? Chúng tôi sẽ xem xét và phản hồi trong vòng 24h.")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    requestRefund();
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void cancelOrder() {
        int result = orderRepository.updateOrderStatus(orderId, "cancelled");

        if (result > 0) {
            Toast.makeText(this, "Đã hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
            loadOrderDetail();
        } else {
            Toast.makeText(this, "Không thể hủy đơn hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestRefund() {
        int result = orderRepository.updateOrderStatus(orderId, "refund_requested");

        if (result > 0) {
            Toast.makeText(this, "Đã gửi yêu cầu hoàn trả. Chúng tôi sẽ liên hệ với bạn sớm nhất.", Toast.LENGTH_LONG).show();
            loadOrderDetail();
        } else {
            Toast.makeText(this, "Không thể gửi yêu cầu hoàn trả", Toast.LENGTH_SHORT).show();
        }
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending":
                return "Chờ xử lý";
            case "processing":
                return "Đang xử lý";
            case "completed":
                return "Hoàn thành";
            case "cancelled":
                return "Đã hủy";
            case "refund_requested":
                return "Đang xử lý hoàn trả";
            case "refunded":
                return "Đã hoàn trả";
            case "refund_rejected":
                return "Hoàn trả thất bại";
            default:
                return status;
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "pending":
                return Color.parseColor("#FF9800");
            case "processing":
                return Color.parseColor("#2196F3");
            case "completed":
                return Color.parseColor("#4CAF50");
            case "cancelled":
                return Color.parseColor("#F44336");
            case "refund_requested":
                return Color.parseColor("#9C27B0");
            case "refunded":
                return Color.parseColor("#607D8B");
            case "refund_rejected":
                return Color.parseColor("#E91E63");
            default:
                return Color.parseColor("#999999");
        }
    }

    private int getMotorbikeImageResource(int motorbikeId) {
        switch (motorbikeId) {
            case 1:
                return R.drawable.honda_sh_160i;
            case 2:
                return R.drawable.yamaha_exciter_155;
            case 3:
                return R.drawable.honda_vision;
            case 4:
                return R.drawable.air_blade_160;
            case 5:
                return R.drawable.yamaha_grande;
            default:
                return R.drawable.ic_bike_placeholder;
        }
    }
}
