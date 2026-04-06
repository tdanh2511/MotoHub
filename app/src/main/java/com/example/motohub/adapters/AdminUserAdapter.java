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
import com.example.motohub.models.User;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEdit(User user);
        void onDelete(User user);
    }

    public AdminUserAdapter(Context context, List<User> users, OnUserActionListener listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        
        holder.tvFullname.setText(user.getFullname());
        holder.tvUsername.setText("Username: " + user.getUsername());
        holder.tvRole.setText("Vai trò: " + (user.getRole().equals("admin") ? "Quản trị viên" : "Người dùng"));
        holder.tvPhone.setText("SĐT: " + (user.getPhone() != null ? user.getPhone() : "Chưa có"));

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullname, tvUsername, tvRole, tvPhone;
        Button btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvFullname = itemView.findViewById(R.id.tvUserFullname);
            tvUsername = itemView.findViewById(R.id.tvUserUsername);
            tvRole = itemView.findViewById(R.id.tvUserRole);
            tvPhone = itemView.findViewById(R.id.tvUserPhone);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
