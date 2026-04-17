package com.example.motohub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.models.Order;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private List<Order> orders;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onUpdateStatus(Order order);

        void onDelete(Order order);
    }

    public OrderAdapter(Context context, List<Order> orders, OnOrderActionListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvCustomerName.setText("Khách hàng: " + order.getCustomerName());
        holder.tvMotorbikeName.setText("Xe: " + order.getMotorbikeName());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(formatter.format(order.getPrice()));

        holder.tvDate.setText("Ngày: " + order.getOrderDate());

        String statusText = getStatusText(order.getStatus());
        holder.tvStatus.setText("Trạng thái: " + statusText);

        holder.btnUpdateStatus.setOnClickListener(v -> listener.onUpdateStatus(order));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(order));
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
            default:
                return status;
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvMotorbikeName, tvPrice, tvDate, tvStatus;
        Button btnUpdateStatus, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvMotorbikeName = itemView.findViewById(R.id.tvMotorbikeName);
            tvPrice = itemView.findViewById(R.id.tvOrderPrice);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
