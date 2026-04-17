package com.example.motohub.adapters;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.models.Motorbike;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminMotorbikeAdapter extends RecyclerView.Adapter<AdminMotorbikeAdapter.ViewHolder> {

    private final Context context;
    private final List<Motorbike> motorbikes;
    private final OnBikeActionListener listener;

    public interface OnBikeActionListener {
        void onEdit(Motorbike bike);

        void onDelete(Motorbike bike);
    }

    public AdminMotorbikeAdapter(Context context, List<Motorbike> motorbikes, OnBikeActionListener listener) {
        this.context = context;
        this.motorbikes = motorbikes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_motorbike, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Motorbike bike = motorbikes.get(position);

        holder.tvName.setText(bike.getName());
        holder.tvBrand.setText("Hãng: " + bike.getBrand());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(formatter.format(bike.getPrice()));

        loadBikeImage(holder.imgBike, bike.getImage());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(bike));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(bike));
    }

    private void loadBikeImage(ImageView imageView, String imageValue) {
        if (TextUtils.isEmpty(imageValue)) {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }

        try {
            if (imageValue.startsWith("content://") || imageValue.startsWith("file://")) {
                imageView.setImageURI(Uri.parse(imageValue));
                return;
            }

            int resId = context.getResources().getIdentifier(
                    imageValue,
                    "drawable",
                    context.getPackageName()
            );

            if (resId != 0) {
                imageView.setImageResource(resId);
            } else {
                imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } catch (Exception e) {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return motorbikes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBike;
        TextView tvName, tvBrand, tvPrice;
        Button btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            imgBike = itemView.findViewById(R.id.imgBike);
            tvName = itemView.findViewById(R.id.tvBikeName);
            tvBrand = itemView.findViewById(R.id.tvBikeBrand);
            tvPrice = itemView.findViewById(R.id.tvBikePrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}