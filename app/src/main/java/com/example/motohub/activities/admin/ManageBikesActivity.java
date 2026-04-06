package com.example.motohub.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.adapters.AdminMotorbikeAdapter;
import com.example.motohub.models.Motorbike;
import com.example.motohub.repository.MotorbikeRepository;

import java.util.List;

public class ManageBikesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminMotorbikeAdapter adapter;
    private MotorbikeRepository motorbikeRepository;
    private Button btnAddBike, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bikes);

        motorbikeRepository = new MotorbikeRepository(this);

        recyclerView = findViewById(R.id.recyclerViewBikes);
        btnAddBike = findViewById(R.id.btnAddBike);
        btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadBikes();

        btnAddBike.setOnClickListener(v -> showAddBikeDialog());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadBikes() {
        List<Motorbike> bikes = motorbikeRepository.getAllMotorbikes();
        adapter = new AdminMotorbikeAdapter(this, bikes, new AdminMotorbikeAdapter.OnBikeActionListener() {
            @Override
            public void onEdit(Motorbike bike) {
                showEditBikeDialog(bike);
            }

            @Override
            public void onDelete(Motorbike bike) {
                showDeleteConfirmDialog(bike);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddBikeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm xe mới");

        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_bike_form, null);
        EditText etName = view.findViewById(R.id.etBikeName);
        EditText etBrand = view.findViewById(R.id.etBikeBrand);
        EditText etPrice = view.findViewById(R.id.etBikePrice);
        EditText etImage = view.findViewById(R.id.etBikeImage);

        builder.setView(view);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String brand = etBrand.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String image = etImage.getText().toString().trim();

            if (name.isEmpty() || brand.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            Motorbike bike = new Motorbike(0, name, brand, price, image, false);
            long result = motorbikeRepository.addMotorbike(bike);

            if (result > 0) {
                Toast.makeText(this, "Thêm xe thành công", Toast.LENGTH_SHORT).show();
                loadBikes();
            } else {
                Toast.makeText(this, "Thêm xe thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showEditBikeDialog(Motorbike bike) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa thông tin xe");

        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_bike_form, null);
        EditText etName = view.findViewById(R.id.etBikeName);
        EditText etBrand = view.findViewById(R.id.etBikeBrand);
        EditText etPrice = view.findViewById(R.id.etBikePrice);
        EditText etImage = view.findViewById(R.id.etBikeImage);

        etName.setText(bike.getName());
        etBrand.setText(bike.getBrand());
        etPrice.setText(String.valueOf(bike.getPrice()));
        etImage.setText(bike.getImage());

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String brand = etBrand.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String image = etImage.getText().toString().trim();

            if (name.isEmpty() || brand.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            bike.setName(name);
            bike.setBrand(brand);
            bike.setPrice(Double.parseDouble(priceStr));
            bike.setImage(image);

            int result = motorbikeRepository.updateMotorbike(bike);
            if (result > 0) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                loadBikes();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showDeleteConfirmDialog(Motorbike bike) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa xe " + bike.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int result = motorbikeRepository.deleteMotorbike(bike.getId());
                    if (result > 0) {
                        Toast.makeText(this, "Xóa xe thành công", Toast.LENGTH_SHORT).show();
                        loadBikes();
                    } else {
                        Toast.makeText(this, "Xóa xe thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
