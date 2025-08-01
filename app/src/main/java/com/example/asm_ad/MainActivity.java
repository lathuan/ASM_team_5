package com.example.asm_ad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATIONS_REQUEST_CODE = 1;
    private DrawerLayout drawerLayout;
    private ImageView iconSearch;
    private ImageView iconNotifications;
    private TextView notificationBadge;
    private TextView tvBalance;
    private TextView tvTotalExpense;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Khởi tạo các view từ activity_main
        drawerLayout = findViewById(R.id.drawer_layout);
        iconSearch = findViewById(R.id.icon_search);
        iconNotifications = findViewById(R.id.icon_notifications);
        fabAdd = findViewById(R.id.fab_add);

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                handleNavigationItemSelected(id); // Gọi phương thức xử lý
                return true;
            });
        }

        // Thiết lập Toolbar
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        // Thiết lập Navigation Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawerLayout != null) {
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }


        // Xử lý sự kiện nhấn nút Tìm kiếm
        if (iconSearch != null) {
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
        }



        // Xử lý sự kiện nhấn nút Thông báo
        if (iconNotifications != null) {
            iconNotifications.setOnClickListener(v -> {
                if (isUserLoggedIn()) {
                    Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
                    int unreadCount = getUnreadNotificationCount();
                    intent.putExtra("unreadCount", unreadCount);
                    intent.putExtra("notifications", getNotifications());
                    startActivityForResult(intent, NOTIFICATIONS_REQUEST_CODE);
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng đăng nhập để xem thông báo", Toast.LENGTH_SHORT).show();
                    redirectToLogin();
                }
            });
        }

        // Xử lý thanh điều hướng dưới
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navProfile = findViewById(R.id.nav_toi);

        navHome.setOnClickListener(v -> showFragment(new HomeFragment()));

        navProfile.setOnClickListener(v -> {
            // Chuyển đến trang hồ sơ
            ProfileFragment profileFragment = new ProfileFragment();
            showFragment(profileFragment);
        });

        // Xử lý Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Xử lý nút Thêm mới
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> showAddBalanceDialog());
        }

        // Hiển thị Fragment mặc định
        showFragment(new HomeFragment());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOTIFICATIONS_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            int newUnreadCount = data.getIntExtra("unreadCount", getUnreadNotificationCount());
            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("unreadNotifications", newUnreadCount);
            editor.apply();
            updateNotificationBadge(newUnreadCount);
        }
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
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            redirectToLogin();
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("lastActiveTime", currentTime);
            editor.apply();
            updateBalance();
            updateTotalExpense();
            if (notificationBadge != null) updateNotificationBadge(getUnreadNotificationCount());
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

    // Lấy số lượng thông báo chưa đọc
    private int getUnreadNotificationCount() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getInt("unreadNotifications", 0);
    }

    // Lấy danh sách thông báo
    private String getNotifications() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getString("notifications", "");
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

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView textViewUserName = headerView.findViewById(R.id.textViewUserName);
            TextView textViewUserEmail = headerView.findViewById(R.id.textViewUserEmail);
            if (textViewUserName != null) textViewUserName.setText(username);
            if (textViewUserEmail != null) textViewUserEmail.setText(email);
        }

//        TextView tvUsername = findViewById(R.id.tvUsername);
//        if (tvUsername != null) tvUsername.setText(username);
    }

    // Cập nhật số dư từ SharedPreferences
    private void updateBalance() {
    }

    // Cập nhật tổng chi tiêu từ SharedPreferences
    private void updateTotalExpense() {
    }

    // Hiển thị dialog để thêm số dư hoặc chi tiêu
    private void showAddBalanceDialog() {
        if (!isUserLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm số dư", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn hành động");

        // Tạo dialog với hai lựa chọn
        final String[] options = {"Thêm số dư", "Thêm chi tiêu"};
        builder.setItems(options, (dialog, which) -> {
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint(which == 0 ? "Nhập số tiền để thêm (VND)" : "Nhập số tiền chi tiêu (VND)");
            AlertDialog.Builder inputBuilder = new AlertDialog.Builder(this);
            inputBuilder.setTitle(options[which]);
            inputBuilder.setView(input);

            inputBuilder.setPositiveButton("Xác nhận", (innerDialog, innerWhich) -> {
                String amountStr = input.getText().toString().trim();
                if (!amountStr.isEmpty()) {
                    try {
                        long amount = Long.parseLong(amountStr);
                        if (amount > 0) {
                            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            long currentBalance = prefs.getLong("balance", 0);
                            long currentExpense = prefs.getLong("totalExpense", 0);
                            String actionMessage = "";

                            if (which == 0) { // Thêm số dư
                                long newBalance = currentBalance + amount;
                                editor.putLong("balance", newBalance);
                                actionMessage = "Đã thêm " + String.format("%,d VND", amount) + " vào số dư";
                            } else { // Thêm chi tiêu
                                if (currentBalance >= amount) {
                                    long newBalance = currentBalance - amount;
                                    long newExpense = currentExpense + amount;
                                    editor.putLong("balance", newBalance);
                                    editor.putLong("totalExpense", newExpense);
                                    actionMessage = "Đã chi tiêu " + String.format("%,d VND", amount);
                                } else {
                                    Toast.makeText(MainActivity.this, "Số dư không đủ để chi tiêu!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            editor.apply();

                            // CẬP NHẬT DỮ LIỆU TRONG FRAGMENT HIỆN TẠI
                            refreshCurrentFragment();

                            addNotification(actionMessage);
                            Toast.makeText(MainActivity.this, actionMessage + " - Số dư: " + String.format("%,d VND", prefs.getLong("balance", 0)) + ", Chi tiêu: " + String.format("%,d VND", prefs.getLong("totalExpense", 0)), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                }
            });

            inputBuilder.setNegativeButton("Hủy", (innerDialog, innerWhich) -> innerDialog.cancel());
            inputBuilder.show();
        });

        builder.show();
    }

    // Thêm phương thức này để cập nhật Fragment hiện tại
    private void refreshCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof HomeFragment) {
            ((HomeFragment) currentFragment).refreshData();
        }
        // Fragment khác
    }

    // Thêm thông báo về hoạt động
    private void addNotification(String message) {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String notificationsStr = prefs.getString("notifications", "");
        String newNotification = message + " - " + getCurrentDateTime() + "\n" + notificationsStr;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("notifications", newNotification);
        int unreadCount = prefs.getInt("unreadNotifications", 0) + 1;
        editor.putInt("unreadNotifications", unreadCount);
        editor.apply();
        if (notificationBadge != null) updateNotificationBadge(unreadCount);
    }

    // Lấy ngày giờ hiện tại
    private String getCurrentDateTime() {
        return "07/31/2025 10:25 PM +07"; // Cập nhật theo thời gian hiện tại
    }

    // Xử lý chọn mục trong Navigation Drawer
    private void handleNavigationItemSelected(int id) {
        Fragment fragment = null;

        if (id == R.id.nav_home_drawer) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_expense_tracking) {
            fragment = new ExpenseTrackingFragment();
        } else if (id == R.id.nav_income_tracking) {
            fragment = new IncomeTrackingFragment();
        } else if (id == R.id.nav_budget_setting) {
            fragment = new BudgetSettingFragment();
        } else if (id == R.id.nav_expense_overview) {
            fragment = new ExpenseOverviewFragment();
        } else if (id == R.id.nav_savings_goals) {
            fragment = new SavingsGoalsFragment();
        } else if (id == R.id.nav_salary) {
            fragment = new SalaryFragment();
        } else if (id == R.id.nav_statistical) {
            fragment = new StatisticalFragment();
        } else if (id == R.id.nav_report) {
            fragment = new ReportFragment();
        } else if (id == R.id.nav_search_drawer) {
            if (iconSearch != null) iconSearch.performClick();
            return;
        } else if (id == R.id.nav_notifications_drawer) {
            if (iconNotifications != null) iconNotifications.performClick();
            return;
        } else if (id == R.id.nav_settings_drawer) {
            // Xử lý cài đặt
            Toast.makeText(this, "Chức năng cài đặt", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fragment != null) {
            showFragment(fragment);
        } else {
            // Đóng Drawer ngay cả khi không chuyển Fragment
            drawerLayout.closeDrawer(GravityCompat.START, true);
        }

//        if (id == R.id.nav_home_drawer) {
//            Toast.makeText(this, "Đã chọn Trang Chủ", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.nav_expense_tracking) {
//            Toast.makeText(this, "Đã chọn Theo Dõi Chi Tiêu", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.nav_income_tracking) {
//            Toast.makeText(this, "Đã chọn Theo Dõi Thu Nhập", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.nav_budget_setting) {
//            Toast.makeText(this, "Đã chọn Cài Đặt Ngân Sách", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.nav_expense_overview) {
//            Toast.makeText(this, "Đã chọn Tổng Quan Chi Tiêu", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.nav_savings_goals) {
//            Toast.makeText(this, "Đã chọn Mục Tiêu Tiết Kiệm", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.nav_salary) {
//            Toast.makeText(this, "Đã chọn Lương", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.nav_statistical) {
//            Toast.makeText(this, "Đã chọn Thống Kê", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.nav_report) {
//            Toast.makeText(this, "Đã chọn Báo Cáo", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.nav_search_drawer) {
//            if (iconSearch != null) iconSearch.performClick();
//        } else if (id == R.id.nav_notifications_drawer) {
//            if (iconNotifications != null) iconNotifications.performClick();
//        } else if (id == R.id.nav_settings_drawer) {
//            Toast.makeText(this, "Đã chọn Cài Đặt", Toast.LENGTH_SHORT).show();
//        }
    }

    private void showFragment(Fragment fragment) {
        // Đóng Drawer với hiệu ứng mượt
        drawerLayout.closeDrawer(GravityCompat.START, true);

        // Hiệu ứng chuyển Fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Xử lý nút Back để đóng Drawer
    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Chuỗi cho Drawer Toggle
    private static final int R_string_navigation_drawer_open = R.string.navigation_drawer_open;
    private static final int R_string_navigation_drawer_close = R.string.navigation_drawer_close;
}