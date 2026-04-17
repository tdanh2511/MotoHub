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
import com.example.motohub.models.Brand;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    public interface OnBrandActionListener {
        void onEdit(Brand brand);

        void onDelete(Brand brand);
    }

    private final Context context;
    private final List<Brand> brandList;
    private final OnBrandActionListener listener;

    public BrandAdapter(Context context, List<Brand> brandList, OnBrandActionListener listener) {
        this.context = context;
        this.brandList = brandList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_brand, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Brand brand = brandList.get(position);

        holder.tvBrandName.setText(brand.getName());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(brand);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(brand);
            }
        });
    }

    @Override
    public int getItemCount() {
        return brandList == null ? 0 : brandList.size();
    }

    static class BrandViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrandName;
        ImageView btnEdit, btnDelete;

        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}