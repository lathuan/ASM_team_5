package com.example.asm_ad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        setContentView(R.layout.activity_main);

        // Khởi tạo các view
        drawerLayout = findViewById(R.id.drawer_layout);
        iconSearch = findViewById(R.id.icon_search);
        iconNotifications = findViewById(R.id.icon_notifications);
        fabAdd = findViewById(R.id.fab_add);

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                handleNavigationItemSelected(id);
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

        // Xử lý nút Tìm kiếm
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

        // Xử lý nút Thông báo
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
        navProfile.setOnClickListener(v -> showFragment(new ProfileFragment()));

        // Xử lý Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Xử lý FAB
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> showAddBalanceDialog());
        }

        // Hiển thị fragment mặc định
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

    private void updateBalance() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            double balance = dbHelper.getUserBalance(userId);
            if (tvBalance != null) tvBalance.setText(String.format("%,.0f VND", balance));
        }
    }

    private void updateTotalExpense() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            double totalExpense = dbHelper.getUserTotalExpense(userId);
            if (tvTotalExpense != null) tvTotalExpense.setText(String.format("%,.0f VND", totalExpense));
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

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private int getUnreadNotificationCount() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getInt("unreadNotifications", 0);
    }

    private String getNotifications() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getString("notifications", "");
    }

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
    }

    private void showAddBalanceDialog() {
        if (!isUserLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm số dư", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }


        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn hành động");

        final String[] options = {"Thêm số dư", "Thêm chi tiêu"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // Thêm số dư
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setHint("Nhập số tiền để thêm (VND)");
                AlertDialog.Builder inputBuilder = new AlertDialog.Builder(this);
                inputBuilder.setTitle(options[which]);
                inputBuilder.setView(input);

                inputBuilder.setPositiveButton("Xác nhận", (innerDialog, innerWhich) -> {
                    String amountStr = input.getText().toString().trim();
                    if (!amountStr.isEmpty()) {
                        try {
                            double amount = Double.parseDouble(amountStr);
                            if (amount > 0 && userIdStr != null) {
                                int userId = Integer.parseInt(userIdStr);

                                DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                                double currentBalance = dbHelper.getUserBalance(userId);
                                double newBalance = currentBalance + amount;
                                dbHelper.updateUserBalance(userId, newBalance);
                                // Lưu lịch sử thu nhập
                                saveIncomeToHistory(userId, amount, "Thêm số dư từ trang chủ", getCurrentDateTime());
                                String actionMessage = "Đã thêm " + String.format("%,.0f VND", amount) + " vào số dư";

                                refreshCurrentFragment();
                                addNotification(actionMessage);
                                Toast.makeText(MainActivity.this, actionMessage, Toast.LENGTH_SHORT).show();
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
            } else { // Thêm chi tiêu
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_add_expense, null);


                EditText etAmount = dialogView.findViewById(R.id.et_amount);
                EditText etDescription = dialogView.findViewById(R.id.et_description);
                EditText etDate = dialogView.findViewById(R.id.et_date);

                Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

                // Đặt ngày giờ hiện tại làm giá trị mặc định
                String currentDateTime = getCurrentDateTime();
                etDate.setText(currentDateTime);

                List<String> categoryNames = new ArrayList<>();
                final List<Integer> categoryIds = new ArrayList<>();
                if (userIdStr != null) {
                    int userId = Integer.parseInt(userIdStr);
                    DatabaseHelper dbHelper = new DatabaseHelper(this);
                    Cursor cursor = dbHelper.getCategoriesByUserId(userId);
                    if (cursor.moveToFirst()) {
                        do {
                            categoryNames.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME)));
                            categoryIds.add(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID)));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                }

                AlertDialog expenseDialog = builder.setView(dialogView).create();

                btnCancel.setOnClickListener(v -> expenseDialog.dismiss());
                btnConfirm.setOnClickListener(v -> {
                    String amountStr = etAmount.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String date = etDate.getText().toString().trim();

                    if ( !amountStr.isEmpty() && !date.isEmpty() && userIdStr != null) {
                        try {
                            double amount = Double.parseDouble(amountStr);
                            if (amount > 0) {
                                int userId = Integer.parseInt(userIdStr);
                                DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                                double currentBalance = dbHelper.getUserBalance(userId);
                                double currentExpense = dbHelper.getUserTotalExpense(userId);

                                if (currentBalance >= amount) {
                                    double newBalance = currentBalance - amount;
                                    double newExpense = currentExpense + amount;

                                    // Lưu chi tiêu vào bảng Expense
                                    ContentValues values = new ContentValues();
                                    values.put(DatabaseHelper.COLUMN_EXPENSE_USER_ID, userId);

                                    values.put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, amount);
                                    values.put(DatabaseHelper.COLUMN_EXPENSE_DATE, date);
                                    values.put(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION, description.isEmpty() ? null : description);
                                    dbHelper.getWritableDatabase().insert(DatabaseHelper.TABLE_EXPENSE, null, values);

                                    // Cập nhật Finance
                                    dbHelper.updateUserBalance(userId, newBalance);
                                    dbHelper.updateUserExpense(userId, newExpense);

                                    // Lưu lịch sử chi tiêu
                                    saveExpenseToHistory(userId, amount, description.isEmpty() ? "Chi tiêu" : description, date);
                                    String actionMessage = "Đã chi tiêu " + String.format("%,.0f VND", amount);

                                    refreshCurrentFragment();
                                    addNotification(actionMessage);
                                    Toast.makeText(MainActivity.this, actionMessage, Toast.LENGTH_SHORT).show();
                                    expenseDialog.dismiss();
                                } else {
                                    Toast.makeText(MainActivity.this, "Số dư không đủ để chi tiêu!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    }
                });

                expenseDialog.show();
            }
        });

        builder.show();
    }

    private void saveExpenseToHistory(int userId, double amount, String description, String timestamp) {
        SharedPreferences prefs = getSharedPreferences("ExpenseHistory_" + userId, MODE_PRIVATE);
        String existingHistory = prefs.getString("expenses", "");
        String newExpense = amount + "|" + description + "|" + timestamp + "\n";
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("expenses", newExpense + existingHistory);
        editor.apply();
    }

    private void saveIncomeToHistory(int userId, double amount, String description, String timestamp) {
        SharedPreferences prefs = getSharedPreferences("IncomeHistory_" + userId, MODE_PRIVATE);
        String existingHistory = prefs.getString("incomes", "");
        String newIncome = amount + "|" + description + "|" + timestamp + "\n";
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("incomes", newIncome + existingHistory);
        editor.apply();
    }

    private void refreshCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof HomeFragment) {
            ((HomeFragment) currentFragment).refreshData();
        } else if (currentFragment instanceof ExpenseTrackingFragment) {
            ((ExpenseTrackingFragment) currentFragment).refreshData();
        } else if (currentFragment instanceof IncomeTrackingFragment) {
            ((IncomeTrackingFragment) currentFragment).refreshData();
        }
    }

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

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(calendar.getTime());
    }

    private void handleNavigationItemSelected(int id) {
        Fragment fragment = null;

        if (id == R.id.nav_home_drawer) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_expense_tracking) {
            fragment = new ExpenseTrackingFragment();
        } else if (id == R.id.nav_income_tracking) {
            fragment = new IncomeTrackingFragment();
        } else if (id == R.id.nav_savings_goals) {
            fragment = new SavingsGoalsFragment();
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
            // Khởi chạy RecurringExpenseActivity
            Intent intent = new Intent(MainActivity.this, RecurringExpenseActivity.class);
            startActivity(intent);
            // Đóng Navigation Drawer
            drawerLayout.closeDrawer(GravityCompat.START);
            return; // Đảm bảo không tiếp tục xử lý fragment
        }

        if (fragment != null) {
            showFragment(fragment);
        } else {
            drawerLayout.closeDrawer(GravityCompat.START, true);
        }
    }
    private void showFragment(Fragment fragment) {
        drawerLayout.closeDrawer(GravityCompat.START, true);
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

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}