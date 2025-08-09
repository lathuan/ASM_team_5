package com.example.asm_ad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_ad.adapter.UserAdapter;
import com.example.asm_ad.model.UserModel;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity implements UserAdapter.OnUserChangeListener {

    private RecyclerView rvUserList;
    private Button btnAddUser, btnBackToLogin;
    private DatabaseHelper dbHelper;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Khởi tạo view
        rvUserList = findViewById(R.id.rvUserList);
        btnAddUser = findViewById(R.id.btnAddUser);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        dbHelper = new DatabaseHelper(this);
        rvUserList.setLayoutManager(new LinearLayoutManager(this));

        // Load danh sách người dùng
        loadUsers();

        // Nút đăng xuất
        btnBackToLogin.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            prefs.edit().clear().apply();
            startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            finish();
        });

        // Nút thêm user
        btnAddUser.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng thêm người dùng sẽ được cập nhật sau", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUsers() {
        ArrayList<UserModel> userList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllUsers();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_USERNAME));
                String fullname = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_FULLNAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PHONE));
                int roleId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ROLE_ID));

                userList.add(new UserModel(id, username, fullname, email, phone, roleId));
            } while (cursor.moveToNext());
            cursor.close();
        }

        userAdapter = new UserAdapter(userList, dbHelper, this, this);
        rvUserList.setAdapter(userAdapter);
    }

    @Override
    public void onUserChanged() {
        loadUsers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.close();
    }
}
