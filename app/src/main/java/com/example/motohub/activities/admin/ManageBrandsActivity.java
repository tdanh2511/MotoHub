package com.example.motohub.activities.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.adapters.BrandAdapter;
import com.example.motohub.models.Brand;
import com.example.motohub.repository.BrandRepository;

import java.util.List;

public class ManageBrandsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBrands;
    private Button btnAddBrand, btnBack;

    private BrandRepository brandRepository;
    private BrandAdapter brandAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_brands);

        brandRepository = new BrandRepository(this);

        recyclerViewBrands = findViewById(R.id.recyclerViewBrands);
        btnAddBrand = findViewById(R.id.btnAddBrand);
        btnBack = findViewById(R.id.btnBack);

        recyclerViewBrands.setLayoutManager(new LinearLayoutManager(this));

        loadBrands();

        btnAddBrand.setOnClickListener(v -> showAddBrandDialog());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadBrands() {
        List<Brand> brandList = brandRepository.getAllBrands();

        brandAdapter = new BrandAdapter(this, brandList, new BrandAdapter.OnBrandActionListener() {
            @Override
            public void onEdit(Brand brand) {
                showEditBrandDialog(brand);
            }

            @Override
            public void onDelete(Brand brand) {
                showDeleteBrandDialog(brand);
            }
        });

        recyclerViewBrands.setAdapter(brandAdapter);
    }

    // ================= ADD =================
    private void showAddBrandDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm hãng xe");

        EditText editText = new EditText(this);
        editText.setHint("Nhập tên hãng xe");
        builder.setView(editText);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = editText.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên hãng", Toast.LENGTH_SHORT).show();
                return;
            }

            long result = brandRepository.addBrand(name);

            if (result > 0) {
                Toast.makeText(this, "Thêm hãng thành công", Toast.LENGTH_SHORT).show();
                loadBrands();
            } else {
                Toast.makeText(this, "Thêm hãng thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    // ================= EDIT =================
    private void showEditBrandDialog(Brand brand) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa hãng xe");

        EditText editText = new EditText(this);
        editText.setText(brand.getName());
        builder.setView(editText);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = editText.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên hãng", Toast.LENGTH_SHORT).show();
                return;
            }

            int result = brandRepository.updateBrand(brand.getId(), newName);

            if (result > 0) {
                Toast.makeText(this, "Cập nhật hãng thành công", Toast.LENGTH_SHORT).show();
                loadBrands();
            } else {
                Toast.makeText(this, "Cập nhật hãng thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    // ================= DELETE =================
    private void showDeleteBrandDialog(Brand brand) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa hãng " + brand.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int result = brandRepository.deleteBrand(brand.getId());

                    if (result > 0) {
                        Toast.makeText(this, "Xóa hãng thành công", Toast.LENGTH_SHORT).show();
                        loadBrands();
                    } else {
                        Toast.makeText(this, "Xóa hãng thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}