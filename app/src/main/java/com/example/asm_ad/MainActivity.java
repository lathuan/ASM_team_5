package com.example.asm_ad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView iconSearch;
    private ImageView iconNotifications;
    private TextView notificationBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Khởi tạo DrawerLayout và NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Thiết lập Toolbar
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Thiết lập Navigation Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Xử lý sự kiện chọn mục trong Navigation Drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            handleNavigationItemSelected(id);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Khởi tạo các biểu tượng trên Toolbar
        iconSearch = findViewById(R.id.icon_search);
        iconNotifications = findViewById(R.id.icon_notifications);
        notificationBadge = findViewById(R.id.notification_badge);

        // Xử lý sự kiện nhấn nút Tìm kiếm
        iconSearch.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                String userId = prefs.getString("userId", null);
                if (userId != null) {
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show();
                    redirectToLogin();
                }
            } else {
                Toast.makeText(MainActivity.this, "Vui lòng đăng nhập để tìm kiếm", Toast.LENGTH_SHORT).show();
                redirectToLogin();
            }
        });

        // Xử lý sự kiện nhấn nút Thông báo
        iconNotifications.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
                int unreadCount = getUnreadNotificationCount();
                intent.putExtra("unreadCount", unreadCount);
                startActivity(intent);
                updateNotificationBadge(unreadCount);
            } else {
                Toast.makeText(MainActivity.this, "Vui lòng đăng nhập để xem thông báo", Toast.LENGTH_SHORT).show();
                redirectToLogin();
            }
        });

        // Xử lý thanh điều hướng dưới
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navToi = findViewById(R.id.nav_toi);

        navHome.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Đã chọn Trang Chủ", Toast.LENGTH_SHORT).show());

        navToi.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Đã chọn Hồ Sơ", Toast.LENGTH_SHORT).show());

        // Xử lý Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cập nhật thông tin người dùng
        updateUserInfo();
        updateNotificationBadge(getUnreadNotificationCount());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        long lastActiveTime = prefs.getLong("lastActiveTime", 0);
        long timeout = 60 * 1000; // 1 phút

        long currentTime = System.currentTimeMillis();
        if (!isLoggedIn || (currentTime - lastActiveTime > timeout)) {
            // Phiên hết hạn -> đăng xuất
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            redirectToLogin();
        } else {
            // Cập nhật thời gian hoạt động
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("lastActiveTime", currentTime);
            editor.apply();
            updateNotificationBadge(getUnreadNotificationCount());
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("lastActiveTime", System.currentTimeMillis());
        editor.apply();
    }

    // Kiểm tra trạng thái đăng nhập
    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    // Chuyển hướng đến LoginActivity
    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Lấy số lượng thông báo chưa đọc (thay bằng logic thực tế)
    private int getUnreadNotificationCount() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getInt("unreadNotifications", 0); // Ví dụ
    }

    // Cập nhật huy hiệu thông báo
    private void updateNotificationBadge(int count) {
        if (notificationBadge != null) {
            if (count > 0) {
                notificationBadge.setVisibility(View.VISIBLE);
                notificationBadge.setText(String.valueOf(count));
            } else {
                notificationBadge.setVisibility(View.GONE);
            }
        }
    }

    // Cập nhật thông tin người dùng trong giao diện
    private void updateUserInfo() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = prefs.getString("username", "Tên Người Dùng");
        String email = prefs.getString("email", "email@example.com");

        // Cập nhật header của Navigation Drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textViewUserName = headerView.findViewById(R.id.textViewUserName);
        TextView textViewUserEmail = headerView.findViewById(R.id.textViewUserEmail);
        textViewUserName.setText(username);
        textViewUserEmail.setText(email);

        // Cập nhật nội dung chính
        TextView tvUsername = findViewById(R.id.tvUsername);
        tvUsername.setText(username);
    }

    // Xử lý chọn mục trong Navigation Drawer
    private void handleNavigationItemSelected(int id) {
        if (id == R.id.nav_home_drawer) {
            Toast.makeText(this, "Đã chọn Trang Chủ", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_expense_tracking) {
            Toast.makeText(this, "Đã chọn Theo Dõi Chi Tiêu", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_income_tracking) {
            Toast.makeText(this, "Đã chọn Theo Dõi Thu Nhập", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_budget_setting) {
            Toast.makeText(this, "Đã chọn Cài Đặt Ngân Sách", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_expense_overview) {
            Toast.makeText(this, "Đã chọn Tổng Quan Chi Tiêu", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_savings_goals) {
            Toast.makeText(this, "Đã chọn Mục Tiêu Tiết Kiệm", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_salary) {
            Toast.makeText(this, "Đã chọn Lương", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_statistical) {
            Toast.makeText(this, "Đã chọn Thống Kê", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_report) {
            Toast.makeText(this, "Đã chọn Báo Cáo", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_search_drawer) {
            iconSearch.performClick();
        } else if (id == R.id.nav_notifications_drawer) {
            iconNotifications.performClick();
        } else if (id == R.id.nav_settings_drawer) {
            Toast.makeText(this, "Đã chọn Cài Đặt", Toast.LENGTH_SHORT).show();
        }
    }

    // Xử lý nút Back để đóng Drawer
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Chuỗi cho Drawer Toggle
    private static final int R_string_navigation_drawer_open = R.string.navigation_drawer_open;
    private static final int R_string_navigation_drawer_close = R.string.navigation_drawer_close;
}