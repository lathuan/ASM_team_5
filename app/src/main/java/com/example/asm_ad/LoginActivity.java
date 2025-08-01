package com.example.asm_ad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText edtLoginUN, edtLoginPassword;
    private Button btnLogin, btnSignin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        edtLoginUN = findViewById(R.id.edtLoginUN);
        if (edtLoginUN == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy EditText username", Toast.LENGTH_SHORT).show();
            return;
        }
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        if (edtLoginPassword == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy EditText password", Toast.LENGTH_SHORT).show();
            return;
        }
        btnLogin = findViewById(R.id.btnLogin);
        if (btnLogin == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy Button login", Toast.LENGTH_SHORT).show();
            return;
        }
        btnSignin = findViewById(R.id.btnSignin);
        if (btnSignin == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy Button signin", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        long lastActiveTime = prefs.getLong("lastActiveTime", 0);
        long timeout = 60 * 1000; // 1 phút
        long currentTime = System.currentTimeMillis();

        if (isLoggedIn && (currentTime - lastActiveTime <= timeout)) {
            // Cập nhật lại thời gian hoạt động
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("lastActiveTime", currentTime);
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(v -> {
            String username = edtLoginUN.getText().toString().trim();
            String password = edtLoginPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] columns = {DatabaseHelper.COLUMN_USER_ID};
            String selection = DatabaseHelper.COLUMN_USER_USERNAME + " = ? AND " + DatabaseHelper.COLUMN_USER_PASSWORD + " = ?";
            String[] selectionArgs = {username, password};
            Cursor cursor = db.query(DatabaseHelper.TABLE_USER, columns, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                // Lấy userId từ cơ sở dữ liệu
                int userIdColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID);
                String userId = (userIdColumnIndex != -1) ? cursor.getString(userIdColumnIndex) : "user123"; // Giá trị mặc định nếu không tìm thấy

                // Lưu session vào SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("username", username);
                editor.putString("userId", userId);
                editor.putString("email", username + "@example.com"); // Giá trị mặc định, có thể lấy từ DB
                editor.putLong("lastActiveTime", System.currentTimeMillis());
                editor.putLong("balance", 0); // Số dư mặc định là 0
                editor.putString("notifications", ""); // Danh sách hoạt động rỗng
                editor.putInt("unreadNotifications", 0); // Số thông báo chưa đọc mặc định
                editor.apply();

                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        });

        btnSignin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}