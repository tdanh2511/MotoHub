package com.example.motohub.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.adapters.MotorbikeAdapter;
import com.example.motohub.models.Motorbike;
import com.example.motohub.repository.FavoriteRepository;
import com.example.motohub.repository.MotorbikeRepository;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText edtSearch;
    private ImageView btnBack, btnSearch, btnOpenFilter;
    private RecyclerView rcvResult;
    private TextView tvResultTitle;

    private Button btnFilterAll, btnFilterHonda, btnFilterYamaha, btnPriceLow, btnPriceHigh, btnFilterSuzuki, btnFilterSYM, btnFilterPiaggio;

    private MotorbikeRepository repository;
    private FavoriteRepository favoriteRepository;
    private MotorbikeAdapter adapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edtSearch = findViewById(R.id.edtSearch);
        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        rcvResult = findViewById(R.id.rcvResult);
        tvResultTitle = findViewById(R.id.tvResultTitle);

        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterHonda = findViewById(R.id.btnFilterHonda);
        btnFilterYamaha = findViewById(R.id.btnFilterYamaha);
        btnPriceLow = findViewById(R.id.btnPriceLow);
        btnPriceHigh = findViewById(R.id.btnPriceHigh);
        btnOpenFilter = findViewById(R.id.btnOpenFilter);
        btnFilterSuzuki = findViewById(R.id.btnFilterSuzuki);
        btnFilterSYM = findViewById(R.id.btnFilterSYM);
        btnFilterPiaggio = findViewById(R.id.btnFilterPiaggio);

        userId = getSharedPreferences("motohub_session", MODE_PRIVATE)
                .getInt("user_id", -1);

        repository = new MotorbikeRepository(this);
        favoriteRepository = new FavoriteRepository(this);

        rcvResult.setLayoutManager(new GridLayoutManager(this, 2));

        loadData(repository.getAllMotorbikes(), "Tất cả xe");

        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> {
            String keyword = edtSearch.getText().toString().trim();
            List<Motorbike> result = repository.searchMotorbikes(keyword);

            if (result.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy xe phù hợp", Toast.LENGTH_SHORT).show();
            }

            loadData(result, "Kết quả tìm kiếm");
        });

        btnFilterAll.setOnClickListener(v -> {
            loadData(repository.getAllMotorbikes(), "Tất cả xe");
            setSelectedButton(btnFilterAll);
        });

        btnFilterHonda.setOnClickListener(v -> {
            loadData(repository.filterByBrand("Honda"), "Xe hãng Honda");
            setSelectedButton(btnFilterHonda);
        });

        btnFilterYamaha.setOnClickListener(v -> {
            loadData(repository.filterByBrand("Yamaha"), "Xe hãng Yamaha");
            setSelectedButton(btnFilterYamaha);
        });

        btnFilterSuzuki.setOnClickListener(v -> {
            loadData(repository.filterByBrand("Suzuki"), "Xe hãng Suzuki");
            setSelectedButton(btnFilterSuzuki);
        });

        btnFilterSYM.setOnClickListener(v -> {
            loadData(repository.filterByBrand("SYM"), "Xe hãng SYM");
            setSelectedButton(btnFilterSYM);
        });

        btnFilterPiaggio.setOnClickListener(v -> {
            loadData(repository.filterByBrand("Piaggio"), "Xe hãng Piaggio");
            setSelectedButton(btnFilterPiaggio);
        });

        btnPriceLow.setOnClickListener(v -> {
            loadData(repository.filterByPrice(0, 50000000), "Xe dưới 50 triệu");
            setSelectedButton(btnPriceLow);
        });

        btnPriceHigh.setOnClickListener(v -> {
            loadData(repository.filterByPrice(50000000, Double.MAX_VALUE), "Xe từ 50 triệu");
            setSelectedButton(btnPriceHigh);
        });

        btnOpenFilter.setOnClickListener(v -> {
            openFilterDialog();
        });
    }

    private void loadData(List<Motorbike> list, String title) {
        tvResultTitle.setText(title + " (" + list.size() + ")");
        adapter = new MotorbikeAdapter(
                this,
                list,
                motorbike -> {
                    Intent intent = new Intent(SearchActivity.this, MotorbikeDetailActivity.class);
                    intent.putExtra("motorbike_id", motorbike.getId());
                    startActivity(intent);
                },
                userId,
                favoriteRepository
        );
        rcvResult.setAdapter(adapter);
    }

    private void openFilterDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        builder.setView(view);

        android.app.AlertDialog dialog = builder.create();

        android.widget.RadioGroup radioBrand = view.findViewById(R.id.radioBrand);
        android.widget.RadioGroup radioPrice = view.findViewById(R.id.radioPrice);
        android.widget.Button btnClearFilter = view.findViewById(R.id.btnClearFilter);
        android.widget.Button btnApplyFilter = view.findViewById(R.id.btnApplyFilter);

        btnClearFilter.setOnClickListener(v -> {
            loadData(repository.getAllMotorbikes(), "Tất cả xe");
            setSelectedButton(btnFilterAll);
            dialog.dismiss();
        });

        btnApplyFilter.setOnClickListener(v -> {
            String brand = "";
            double minPrice = 0;
            double maxPrice = Double.MAX_VALUE;

            int brandId = radioBrand.getCheckedRadioButtonId();
            if (brandId == R.id.rbHonda) brand = "Honda";
            else if (brandId == R.id.rbYamaha) brand = "Yamaha";
            else if (brandId == R.id.rbSuzuki) brand = "Suzuki";
            else if (brandId == R.id.rbSYM) brand = "SYM";
            else if (brandId == R.id.rbPiaggio) brand = "Piaggio";

            int priceId = radioPrice.getCheckedRadioButtonId();
            if (priceId == R.id.rbPriceLow) {
                minPrice = 0;
                maxPrice = 50000000;
            } else if (priceId == R.id.rbPriceHigh) {
                minPrice = 50000000;
                maxPrice = Double.MAX_VALUE;
            }

            List<Motorbike> result = repository.filterMotorbikes(brand, minPrice, maxPrice);
            loadData(result, "Kết quả đã lọc");
            dialog.dismiss();
        });

        dialog.show();
    }

    private void resetFilterButtons() {
        Button[] buttons = {
                btnFilterAll,
                btnFilterHonda,
                btnFilterYamaha,
                btnFilterSuzuki,
                btnFilterSYM,
                btnFilterPiaggio,
                btnPriceLow,
                btnPriceHigh
        };

        for (Button btn : buttons) {
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
            btn.setTextColor(android.graphics.Color.parseColor("#D32F2F"));
        }
    }

    private void setSelectedButton(Button button) {
        resetFilterButtons();
        button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#D32F2F")
        ));
        button.setTextColor(android.graphics.Color.WHITE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload adapter to update favorite status when returning from detail screen
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}