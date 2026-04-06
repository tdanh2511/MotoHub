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
import com.example.motohub.models.Motorbike;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminMotorbikeAdapter extends RecyclerView.Adapter<AdminMotorbikeAdapter.ViewHolder> {

    private Context context;
    private List<Motorbike> motorbikes;
    private OnBikeActionListener listener;

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

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(bike));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(bike));
    }

    @Override
    public int getItemCount() {
        return motorbikes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBrand, tvPrice;
        Button btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBikeName);
            tvBrand = itemView.findViewById(R.id.tvBikeBrand);
            tvPrice = itemView.findViewById(R.id.tvBikePrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
