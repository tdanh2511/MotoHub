package com.example.motohub.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.models.Order;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.ViewHolder> {

    private Context context;
    private List<Order> orders;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public UserOrderAdapter(Context context, List<Order> orders, OnOrderClickListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("Đơn hàng #" + order.getId());
        holder.tvMotorbikeName.setText(order.getMotorbikeName());
        holder.tvQuantity.setText("Số lượng: " + order.getQuantity());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(formatter.format(order.getPrice()));

        holder.tvDate.setText("📅 " + order.getOrderDate());

        String statusText = getStatusText(order.getStatus());
        holder.tvStatus.setText(statusText);
        holder.tvStatus.setBackgroundColor(getStatusColor(order.getStatus()));

        int imageResId = getMotorbikeImageResource(order.getMotorbikeId());
        holder.imgMotorbike.setImageResource(imageResId);

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "Chờ xử lý";
            case "processing": return "Đang xử lý";
            case "completed": return "Hoàn thành";
            case "cancelled": return "Đã hủy";
            case "refund_requested": return "Đang xử lý hoàn trả";
            case "refunded": return "Đã hoàn trả";
            case "refund_rejected": return "Hoàn trả thất bại";
            default: return status;
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "pending": return Color.parseColor("#FF9800");
            case "processing": return Color.parseColor("#2196F3");
            case "completed": return Color.parseColor("#4CAF50");
            case "cancelled": return Color.parseColor("#F44336");
            case "refund_requested": return Color.parseColor("#9C27B0");
            case "refunded": return Color.parseColor("#607D8B");
            case "refund_rejected": return Color.parseColor("#E91E63");
            default: return Color.parseColor("#999999");
        }
    }

    private int getMotorbikeImageResource(int motorbikeId) {
        switch (motorbikeId) {
            case 1: return R.drawable.honda_sh_160i;
            case 2: return R.drawable.yamaha_exciter_155;
            case 3: return R.drawable.honda_vision;
            case 4: return R.drawable.air_blade_160;
            case 5: return R.drawable.yamaha_grande;
            default: return R.drawable.ic_bike_placeholder;
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvMotorbikeName, tvQuantity, tvPrice, tvDate, tvStatus;
        ImageView imgMotorbike;

        ViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvMotorbikeName = itemView.findViewById(R.id.tvMotorbikeName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvOrderPrice);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            imgMotorbike = itemView.findViewById(R.id.imgMotorbike);
        }
    }
}
