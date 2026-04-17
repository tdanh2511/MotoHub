package com.example.motohub.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.models.Motorbike;

import android.widget.Toast;

import com.example.motohub.repository.FavoriteRepository;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MotorbikeAdapter extends RecyclerView.Adapter<MotorbikeAdapter.MotorbikeViewHolder> {

    public interface OnMotorbikeClickListener {
        void onMotorbikeClick(Motorbike motorbike);
    }

    public interface OnFavoriteChangeListener {
        void onFavoriteChanged();
    }
    private final Context context;
    private final List<Motorbike> motorbikeList;
    private final OnMotorbikeClickListener listener;
    private final int userId;
    private final FavoriteRepository favoriteRepository;
    private final OnFavoriteChangeListener favoriteChangeListener;

    public MotorbikeAdapter(Context context,
                            List<Motorbike> motorbikeList,
                            OnMotorbikeClickListener listener,
                            int userId,
                            FavoriteRepository favoriteRepository) {
        this(context, motorbikeList, listener, userId, favoriteRepository, null);
    }

    public MotorbikeAdapter(Context context,
                            List<Motorbike> motorbikeList,
                            OnMotorbikeClickListener listener,
                            int userId,
                            FavoriteRepository favoriteRepository,
                            OnFavoriteChangeListener favoriteChangeListener) {
        this.context = context;
        this.motorbikeList = motorbikeList;
        this.listener = listener;
        this.userId = userId;
        this.favoriteRepository = favoriteRepository;
        this.favoriteChangeListener = favoriteChangeListener;
    }

    @NonNull
    @Override
    public MotorbikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_motorbike, parent, false);
        return new MotorbikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MotorbikeViewHolder holder, int position) {
        Motorbike motorbike = motorbikeList.get(position);

        holder.tvName.setText(motorbike.getName());
        holder.tvBrand.setText(motorbike.getBrand());
        holder.tvPrice.setText(formatPrice(motorbike.getPrice()));

        loadImage(holder.imgMotorbike, motorbike.getImage());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMotorbikeClick(motorbike);
        });

        boolean isFav = favoriteRepository.isFavorite(userId, motorbike.getId());
        holder.imgFavorite.setImageResource(
                isFav ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
        );

        holder.imgFavorite.setOnClickListener(v -> {
            if (userId <= 0) {
                Toast.makeText(context, "Vui lòng đăng nhập để thêm yêu thích", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean wasAdded = favoriteRepository.toggleFavorite(userId, motorbike.getId());
            boolean newState = favoriteRepository.isFavorite(userId, motorbike.getId());

            holder.imgFavorite.setImageResource(
                    newState ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
            );

            // Show toast notification
            String message = newState ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            // Notify the parent activity if callback is set
            if (favoriteChangeListener != null) {
                favoriteChangeListener.onFavoriteChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return motorbikeList == null ? 0 : motorbikeList.size();
    }

    static class MotorbikeViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMotorbike, imgFavorite;
        TextView tvName, tvBrand, tvPrice;

        public MotorbikeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMotorbike = itemView.findViewById(R.id.imgMotorbike);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
            tvName = itemView.findViewById(R.id.tvBikeName);
            tvBrand = itemView.findViewById(R.id.tvBikeBrand);
            tvPrice = itemView.findViewById(R.id.tvBikePrice);
        }
    }

    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        return format.format(price) + " đ";
    }

    private void loadImage(ImageView img, String imageValue) {
        if (imageValue == null || imageValue.isEmpty()) {
            img.setImageResource(R.drawable.ic_bike_placeholder);
            return;
        }

        try {
            if (imageValue.startsWith("content://") || imageValue.startsWith("file://")) {
                img.setImageURI(Uri.parse(imageValue));
                return;
            }

            int resId = context.getResources().getIdentifier(
                    imageValue,
                    "drawable",
                    context.getPackageName()
            );

            if (resId != 0) {
                img.setImageResource(resId);
            } else {
                img.setImageResource(R.drawable.ic_bike_placeholder);
            }

        } catch (Exception e) {
            img.setImageResource(R.drawable.ic_bike_placeholder);
        }
    }
}