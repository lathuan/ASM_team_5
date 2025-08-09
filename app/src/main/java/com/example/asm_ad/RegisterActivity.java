package com.example.asm_ad;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtUN, edtFN, emailText, phoneText, edtPassword;
    private Button btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        edtUN = findViewById(R.id.edtUN);
        edtFN = findViewById(R.id.edtFN);
        emailText = findViewById(R.id.EmailText);
        phoneText = findViewById(R.id.PhoneText);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Thêm sự kiện cho TextView đăng nhập
        TextView tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            String username = edtUN.getText().toString().trim();
            String fullName = edtFN.getText().toString().trim();
            String email = emailText.getText().toString().trim();
            String phone = phoneText.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter complete information!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.length() < 3) {
                edtUN.setError("Username must be at least 3 characters!");
                return;
            }

            if (fullName.matches(".*\\d.*")) {
                edtFN.setError("Full name cannot contain numbers!");
                return;
            }

            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                emailText.setError("Invalid email!");
                return;
            }

            if (!phone.matches("^[0-9]{10}$")) {
                phoneText.setError("Phone number must be 10 digits!");
                return;
            }

            if (password.length() <= 8) {
                edtPassword.setError("Password must be greater than 8 characters!");
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_USERNAME, username);
            values.put(DatabaseHelper.COLUMN_USER_PASSWORD, password);
            values.put(DatabaseHelper.COLUMN_USER_FULLNAME, fullName);
            values.put(DatabaseHelper.COLUMN_USER_EMAIL, email);
            values.put(DatabaseHelper.COLUMN_USER_PHONE, phone);
            values.put(DatabaseHelper.COLUMN_USER_ROLE_ID, 1);
            long newRowId = db.insert(DatabaseHelper.TABLE_USER, null, values);

            if (newRowId != -1) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                int newUserId = (int) newRowId;
                dbHelper.addFinanceRecord(newUserId);
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();


                try {
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Registration failed! Username already exists.", Toast.LENGTH_SHORT).show();
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