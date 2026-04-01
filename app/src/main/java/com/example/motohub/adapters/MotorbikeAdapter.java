package com.example.motohub.adapters;

import android.content.Context;
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

    private final Context context;
    private final List<Motorbike> motorbikeList;
    private final OnMotorbikeClickListener listener;

    private final int userId;
    private final FavoriteRepository favoriteRepository;

    public MotorbikeAdapter(Context context,
                            List<Motorbike> motorbikeList,
                            OnMotorbikeClickListener listener,
                            int userId,
                            FavoriteRepository favoriteRepository) {
        this.context = context;
        this.motorbikeList = motorbikeList;
        this.listener = listener;
        this.userId = userId;
        this.favoriteRepository = favoriteRepository;
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

        int imageResId = context.getResources().getIdentifier(
                motorbike.getImage(),
                "drawable",
                context.getPackageName()
        );

        if (imageResId != 0) {
            holder.imgMotorbike.setImageResource(imageResId);
        } else {
            holder.imgMotorbike.setImageResource(R.drawable.ic_bike_placeholder);
        }

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

            favoriteRepository.toggleFavorite(userId, motorbike.getId());

            boolean newState = favoriteRepository.isFavorite(userId, motorbike.getId());
            holder.imgFavorite.setImageResource(
                    newState ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
            );
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
}