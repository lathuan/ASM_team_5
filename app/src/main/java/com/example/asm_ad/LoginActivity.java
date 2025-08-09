package com.example.asm_ad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText edtLoginUN, edtLoginPassword;
    private Button btnLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        edtLoginUN = findViewById(R.id.edtLoginUN);
        if (edtLoginUN == null) {
            Toast.makeText(this, "Error: EditText username not found", Toast.LENGTH_SHORT).show();
            return;
        }
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        if (edtLoginPassword == null) {
            Toast.makeText(this, "Error: EditText password not found", Toast.LENGTH_SHORT).show();
            return;
        }
        btnLogin = findViewById(R.id.btnLogin);
        if (btnLogin == null) {
            Toast.makeText(this, "Error: Login Button not found", Toast.LENGTH_SHORT).show();
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

        // Thêm sự kiện cho TextView đăng ký
        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Xử lý nút quên mật khẩu
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String username = edtLoginUN.getText().toString().trim();
            String password = edtLoginPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter complete information!", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] columns = {
                    DatabaseHelper.COLUMN_USER_ID,
                    DatabaseHelper.COLUMN_USER_EMAIL,
                    DatabaseHelper.COLUMN_USER_FULLNAME,
                    DatabaseHelper.COLUMN_USER_PHONE,
                    DatabaseHelper.COLUMN_USER_ROLE_ID
            };

            String selection = DatabaseHelper.COLUMN_USER_USERNAME + " = ? AND " +
                    DatabaseHelper.COLUMN_USER_PASSWORD + " = ?";
            String[] selectionArgs = {username, password};

            try (Cursor cursor = db.query(
                    DatabaseHelper.TABLE_USER,
                    columns,
                    selection,
                    selectionArgs,
                    null, null, null
            )) {
                if (cursor.moveToFirst()) {
                    // Lấy chỉ số cột AN TOÀN
                    int userIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID);
                    int emailIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_EMAIL);
                    int fullNameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_FULLNAME);
                    int phoneIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PHONE);
                    int roleIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ROLE_ID);

                    if (userIdIndex == -1 || phoneIndex == -1) {
                        Toast.makeText(this, "Error: Required column not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (roleIdIndex == -1) {
                        Toast.makeText(this, "Error: RoleID column not found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int roleId = cursor.getInt(roleIdIndex);
                    int userId = cursor.getInt(userIdIndex);
                    String email = emailIndex != -1 ? cursor.getString(emailIndex) : "";
                    String fullName = fullNameIndex != -1 ? cursor.getString(fullNameIndex) : "";
                    String phone = phoneIndex != -1 ? cursor.getString(phoneIndex) : ""; // << LẤY GIÁ TRỊ PHONE TỪ CURSOR

                    // Sử dụng dbHelper instance đã có, KHÔNG tạo mới
                    if (!dbHelper.hasFinanceRecord(userId)) {
                        dbHelper.addFinanceRecord(userId);
                    }

                    // Lưu session
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("userId", String.valueOf(userId));
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("username", username);
                    editor.putString("fullName", fullName);
                    editor.putString("email", email);
                    editor.putString("phone", phone);
                    editor.putLong("lastActiveTime", System.currentTimeMillis());
                    editor.putString("notifications", "");
                    editor.putInt("unreadNotifications", 0);
                    editor.putInt("roleId", roleId);
                    editor.apply();

                    Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show();
                    Intent intent;

                    if (roleId == 2) { // 2 = Admin ✅
                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                    } else if (roleId == 1) { // 1 = Học sinh ✅
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    }else { // 2 = Student (hoặc giá trị khác)
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
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