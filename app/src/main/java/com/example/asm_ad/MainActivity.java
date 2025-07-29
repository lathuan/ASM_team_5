// Lớp chính quản lý giao diện sau khi đăng nhập
package com.example.asm_ad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

// Chứa các hằng số và biến toàn cục
public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final long INACTIVITY_TIMEOUT = 5 * 1000; // 5 giây timeout
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable logoutRunnable;
    private DatabaseHelper dbHelper;

    // Khởi tạo giao diện và hiển thị thông tin người dùng
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvUsername = findViewById(R.id.tvUsername);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = prefs.getString(KEY_USERNAME, "User");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DatabaseHelper.COLUMN_USER_FULLNAME, DatabaseHelper.COLUMN_USER_ROLE_ID};
        String selection = DatabaseHelper.COLUMN_USER_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER, projection, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_FULLNAME));
            int roleId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ROLE_ID));
            tvWelcome.setText("Xin chào,");
            tvUsername.setText(fullName + " (" + (roleId == 1 ? "Học sinh" : "Admin") + ")");
        } else {
            tvWelcome.setText("Xin chào,");
            tvUsername.setText(username);
        }
        cursor.close();

        setupInactivityTimeout();
    }

    // Thiết lập timer và listener cho tương tác
    private void setupInactivityTimeout() {
        logoutRunnable = new Runnable() {
            @Override
            public void run() {
                logout();
            }
        };

        // Reset timer khi chạm hoặc lướt
        findViewById(android.R.id.content).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                resetInactivityTimeout();
            }
            return false;
        });

//        // Reset timer khi cuộn trong NestedScrollView
//        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
//        if (nestedScrollView != null) {
//            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    resetInactivityTimeout();
//                }
//            });
//        }

        // Khởi động timer ngay khi vào activity
        resetInactivityTimeout();
    }

    // Reset timer khi có tương tác
    private void resetInactivityTimeout() {
        handler.removeCallbacks(logoutRunnable);
        handler.postDelayed(logoutRunnable, INACTIVITY_TIMEOUT);
    }

    // Thoát ứng dụng khi timeout
    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
        finishAffinity();
    }

    // Quản lý timer khi quay lại activity
    @Override
    protected void onResume() {
        super.onResume();
        resetInactivityTimeout();
    }

    // Ngừng timer khi rời activity
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(logoutRunnable);
    }

    // Giải phóng tài nguyên khi destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
        handler.removeCallbacks(logoutRunnable);
    }
}