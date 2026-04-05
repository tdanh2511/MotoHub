package com.example.motohub.activities.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motohub.R;
import com.example.motohub.models.User;
import com.example.motohub.repository.UserRepository;

public class PersonalInfoActivity extends AppCompatActivity {

    private EditText edtFullname, edtPhone, edtEmail, edtAddress;
    private TextView txtUsername, txtEmailDisplay;
    private ImageView btnBack;
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        txtUsername = findViewById(R.id.txtUsername);
        txtEmailDisplay = findViewById(R.id.txtEmailDisplay);
        btnBack = findViewById(R.id.btnBack);
        edtFullname = findViewById(R.id.edtFullname);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        btnUpdate = findViewById(R.id.btnUpdate);

        btnBack.setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        UserRepository repo = new UserRepository(this);
        User user = repo.getUserById(userId);

        if (user != null) {
            edtFullname.setText(user.getFullname());
            edtPhone.setText(user.getPhone());
            edtEmail.setText(user.getEmail());
            edtAddress.setText(user.getAddress());

            txtUsername.setText(user.getUsername());
            txtEmailDisplay.setText(user.getEmail());
        }

        btnUpdate.setOnClickListener(v -> {
            String fullname = edtFullname.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();

            if (TextUtils.isEmpty(fullname)) {
                edtFullname.setError("Không được để trống");
                edtFullname.requestFocus();
                return;
            }

            if (!TextUtils.isEmpty(email)
                    && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Email không hợp lệ");
                edtEmail.requestFocus();
                return;
            }

            repo.updateUserInfo(userId, fullname, phone, email, address);

            txtEmailDisplay.setText(email);

            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}