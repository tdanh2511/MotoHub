package com.example.motohub.activities.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motohub.R;
import com.example.motohub.adapters.AdminUserAdapter;
import com.example.motohub.models.User;
import com.example.motohub.repository.UserRepository;

import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminUserAdapter adapter;
    private UserRepository userRepository;
    private Button btnAddUser, btnBack;
    private static final String[] ROLE_DISPLAY_VALUES = {"Người dùng", "Quản trị viên"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        userRepository = new UserRepository(this);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        btnAddUser = findViewById(R.id.btnAddUser);
        btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadUsers();

        btnAddUser.setOnClickListener(v -> showAddUserDialog());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUsers() {
        List<User> users = userRepository.getAllUsers();
        adapter = new AdminUserAdapter(this, users, new AdminUserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(User user) {
                showEditUserDialog(user);
            }

            @Override
            public void onDelete(User user) {
                showDeleteConfirmDialog(user);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm người dùng mới");

        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_user_form, null);
        EditText etUsername = view.findViewById(R.id.etUsername);
        EditText etPassword = view.findViewById(R.id.etPassword);
        EditText etFullname = view.findViewById(R.id.etFullname);
        EditText etPhone = view.findViewById(R.id.etPhone);
        EditText etEmail = view.findViewById(R.id.etEmail);
        Spinner spinnerRole = view.findViewById(R.id.spinnerRole);
        EditText etAddress = view.findViewById(R.id.etAddress);
        setupRoleSpinner(spinnerRole, "user");

        builder.setView(view);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String fullname = etFullname.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String role = getRoleValue(spinnerRole);
            String address = etAddress.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(0, username, password, fullname, role, phone, email, address);
            long result = userRepository.addUser(user);

            if (result > 0) {
                Toast.makeText(this, "Thêm người dùng thành công", Toast.LENGTH_SHORT).show();
                loadUsers();
            } else {
                Toast.makeText(this, "Thêm người dùng thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showEditUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa thông tin người dùng");

        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_user_form, null);
        EditText etUsername = view.findViewById(R.id.etUsername);
        EditText etPassword = view.findViewById(R.id.etPassword);
        EditText etFullname = view.findViewById(R.id.etFullname);
        EditText etPhone = view.findViewById(R.id.etPhone);
        EditText etEmail = view.findViewById(R.id.etEmail);
        Spinner spinnerRole = view.findViewById(R.id.spinnerRole);
        EditText etAddress = view.findViewById(R.id.etAddress);
        setupRoleSpinner(spinnerRole, user.getRole());

        etUsername.setText(user.getUsername());
        etUsername.setEnabled(false);
        etPassword.setText(user.getPassword());
        etFullname.setText(user.getFullname());
        etPhone.setText(user.getPhone());
        etEmail.setText(user.getEmail());
        etAddress.setText(user.getAddress());

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String password = etPassword.getText().toString().trim();
            String fullname = etFullname.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String role = getRoleValue(spinnerRole);
            String address = etAddress.getText().toString().trim();

            if (password.isEmpty() || fullname.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }

            user.setPassword(password);
            user.setFullname(fullname);
            user.setPhone(phone);
            user.setEmail(email);
            user.setRole(role);
            user.setAddress(address);

            int result = userRepository.updateUser(user);
            if (result > 0) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                loadUsers();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showDeleteConfirmDialog(User user) {
        if ("admin".equalsIgnoreCase(user.getRole())) {
            Toast.makeText(this, "Không thể xóa tài khoản admin", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa người dùng " + user.getFullname() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int result = userRepository.deleteUser(user.getId());
                    if (result > 0) {
                        Toast.makeText(this, "Xóa người dùng thành công", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(this, "Xóa người dùng thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void setupRoleSpinner(Spinner spinnerRole, String selectedRole) {
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                ROLE_DISPLAY_VALUES
        );
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);
        spinnerRole.setSelection("admin".equalsIgnoreCase(selectedRole) ? 1 : 0);
    }

    private String getRoleValue(Spinner spinnerRole) {
        return spinnerRole.getSelectedItemPosition() == 1 ? "admin" : "user";
    }
}
