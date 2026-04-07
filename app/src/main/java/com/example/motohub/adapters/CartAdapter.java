package com.example.motohub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.models.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartItemChangeListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemDeleted(CartItem item);
    }

    private final Context context;
    private final List<CartItem> cartItems;
    private final OnCartItemChangeListener listener;

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.tvName.setText(item.getMotorbike().getName());
        holder.tvBrand.setText(item.getMotorbike().getBrand());
        holder.tvPrice.setText(formatPrice(item.getTotalPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        int imageResId = context.getResources().getIdentifier(
                item.getMotorbike().getImage(),
                "drawable",
                context.getPackageName()
        );

        if (imageResId != 0) {
            holder.imgMotorbike.setImageResource(imageResId);
        } else {
            holder.imgMotorbike.setImageResource(R.drawable.ic_bike_placeholder);
        }

        holder.btnDecrease.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition < cartItems.size()) {
                CartItem currentItem = cartItems.get(currentPosition);
                int currentQuantity = currentItem.getQuantity();
                if (currentQuantity > 1) {
                    if (listener != null) {
                        listener.onQuantityChanged(currentItem, currentQuantity - 1);
                    }
                } else {
                    Toast.makeText(context, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition < cartItems.size()) {
                CartItem currentItem = cartItems.get(currentPosition);
                int currentQuantity = currentItem.getQuantity();
                int maxStock = currentItem.getMotorbike().getStock();
                
                if (currentQuantity < maxStock) {
                    if (listener != null) {
                        listener.onQuantityChanged(currentItem, currentQuantity + 1);
                    }
                } else {
                    Toast.makeText(context, "Đã đạt số lượng tối đa", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition < cartItems.size()) {
                CartItem currentItem = cartItems.get(currentPosition);
                if (listener != null) {
                    listener.onItemDeleted(currentItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems == null ? 0 : cartItems.size();
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems.clear();
        this.cartItems.addAll(newCartItems);
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMotorbike, btnDecrease, btnIncrease, btnDelete;
        TextView tvName, tvBrand, tvPrice, tvQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMotorbike = itemView.findViewById(R.id.imgMotorbike);
            tvName = itemView.findViewById(R.id.tvBikeName);
            tvBrand = itemView.findViewById(R.id.tvBikeBrand);
            tvPrice = itemView.findViewById(R.id.tvBikePrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        return format.format(price) + " đ";
    }
}
