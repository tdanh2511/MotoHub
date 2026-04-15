package com.example.motohub.activities.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.adapters.AdminMotorbikeAdapter;
import com.example.motohub.models.Motorbike;
import com.example.motohub.repository.MotorbikeRepository;
import com.google.android.material.button.MaterialButton;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.motohub.models.Brand;
import com.example.motohub.repository.BrandRepository;

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

    // ================= ADD =================
    private void showAddBikeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm xe mới");

        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_bike_form, null);

        EditText etName = view.findViewById(R.id.etBikeName);
        Spinner spinnerBrand = view.findViewById(R.id.spinnerBrand);

        BrandRepository brandRepository = new BrandRepository(this);
        List<Brand> brandList = brandRepository.getAllBrands();

        List<String> brandNames = new java.util.ArrayList<>();
        for (Brand brand : brandList) {
            brandNames.add(brand.getName());
        }

        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                brandNames
        );

        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(brandAdapter);
        EditText etPrice = view.findViewById(R.id.etBikePrice);
        EditText etImage = view.findViewById(R.id.etBikeImage);
        ImageView imgPreview = view.findViewById(R.id.imgBikePreview);
        MaterialButton btnPreview = view.findViewById(R.id.btnPreviewImage);

        // preview ảnh từ drawable
        btnPreview.setOnClickListener(v -> {
            String imageName = etImage.getText().toString().trim();

            if (imageName.isEmpty()) {
                etImage.setError("Nhập tên ảnh");
                return;
            }

            int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());

            if (resId != 0) {
                imgPreview.setImageResource(resId);
            } else {
                etImage.setError("Không tìm thấy ảnh");
                imgPreview.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        });

        builder.setView(view);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String brand = spinnerBrand.getSelectedItem().toString();
            String priceStr = etPrice.getText().toString().trim();
            String image = etImage.getText().toString().trim();

            if (name.isEmpty() || brand.isEmpty() || priceStr.isEmpty() || image.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int resId = getResources().getIdentifier(image, "drawable", getPackageName());
            if (resId == 0) {
                Toast.makeText(this, "Tên ảnh không tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                Motorbike bike = new Motorbike(0, name, brand, price, image, false);

                long result = motorbikeRepository.addMotorbike(bike);

                if (result > 0) {
                    Toast.makeText(this, "Thêm xe thành công", Toast.LENGTH_SHORT).show();
                    loadBikes();
                } else {
                    Toast.makeText(this, "Thêm xe thất bại", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá xe không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    // ================= EDIT =================
    private void showEditBikeDialog(Motorbike bike) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa thông tin xe");

        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_bike_form, null);

        EditText etName = view.findViewById(R.id.etBikeName);
        Spinner spinnerBrand = view.findViewById(R.id.spinnerBrand);
        EditText etPrice = view.findViewById(R.id.etBikePrice);
        EditText etImage = view.findViewById(R.id.etBikeImage);
        ImageView imgPreview = view.findViewById(R.id.imgBikePreview);
        MaterialButton btnPreview = view.findViewById(R.id.btnPreviewImage);

        // ================= LOAD BRAND =================
        BrandRepository brandRepository = new BrandRepository(this);
        List<Brand> brandList = brandRepository.getAllBrands();

        List<String> brandNames = new java.util.ArrayList<>();
        for (Brand brand : brandList) {
            brandNames.add(brand.getName());
        }

        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                brandNames
        );
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(brandAdapter);

        // ================= SET DATA CŨ =================
        etName.setText(bike.getName());
        etPrice.setText(String.valueOf(bike.getPrice()));
        etImage.setText(bike.getImage());

        // set hãng đang chọn
        int selectedPosition = 0;
        for (int i = 0; i < brandNames.size(); i++) {
            if (brandNames.get(i).equalsIgnoreCase(bike.getBrand())) {
                selectedPosition = i;
                break;
            }
        }
        spinnerBrand.setSelection(selectedPosition);

        loadPreviewImage(imgPreview, bike.getImage());

        // ================= PREVIEW IMAGE =================
        btnPreview.setOnClickListener(v -> {
            String imageName = etImage.getText().toString().trim();
            int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());

            if (resId != 0) {
                imgPreview.setImageResource(resId);
            } else {
                Toast.makeText(this, "Không tìm thấy ảnh", Toast.LENGTH_SHORT).show();
            }
        });

        // ================= SAVE =================
        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String brand = spinnerBrand.getSelectedItem().toString();
            String priceStr = etPrice.getText().toString().trim();
            String image = etImage.getText().toString().trim();

            if (name.isEmpty() || brand.isEmpty() || priceStr.isEmpty() || image.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
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
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    // ================= PREVIEW =================
    private void loadPreviewImage(ImageView imageView, String imageValue) {
        int resId = getResources().getIdentifier(imageValue, "drawable", getPackageName());

        if (resId != 0) {
            imageView.setImageResource(resId);
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    // ================= DELETE =================
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