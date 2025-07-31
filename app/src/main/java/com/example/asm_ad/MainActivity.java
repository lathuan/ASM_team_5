package com.example.asm_ad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Setup Toolbar
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup Navigation Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle Navigation Drawer Item Clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            handleNavigationItemSelected(id);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Handle Toolbar Icon Clicks

        ImageView iconSearch = findViewById(R.id.icon_search);
        ImageView iconNotifications = findViewById(R.id.icon_notifications);



        // Search Icon Click - Start SearchActivity
        iconSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // Notifications Icon Click - Start NotificationsActivity
        iconNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        // Handle Bottom Navigation Bar Clicks
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navToi = findViewById(R.id.nav_toi);

        navHome.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Home clicked", Toast.LENGTH_SHORT).show());

        navToi.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show());

        // Handle Floating Action Button (FAB) Click with Popup Menu
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, fabAdd);
            popupMenu.getMenuInflater().inflate(R.menu.main_popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                handlePopupMenuItemSelected(item.getItemId());
                return true;
            });
            popupMenu.show();
        });

        // Handle Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
            // session hết hạn -> logout
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            // Cập nhật thời gian hoạt động
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("lastActiveTime", currentTime);
            editor.apply();
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


    // Handle Navigation Drawer Menu Item Selection
    private void handleNavigationItemSelected(int id) {
        if (id == R.id.nav_home_drawer) {
            Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_expense_tracking) {
            Toast.makeText(this, "Expense Tracking selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_income_tracking) {
            Toast.makeText(this, "Income Tracking selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_budget_setting) {
            Toast.makeText(this, "Budget Setting selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_expense_overview) {
            Toast.makeText(this, "Expense Overview selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_savings_goals) {
            Toast.makeText(this, "Savings Goals selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_salary) {
            Toast.makeText(this, "Salary selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_statistical) {
            Toast.makeText(this, "Statistical selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_report) {
            Toast.makeText(this, "Report selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_search_drawer) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_notifications_drawer) {
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings_drawer) {
            Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle Popup Menu Item Selection
    private void handlePopupMenuItemSelected(int id) {
        if (id == R.id.action_statistical) {
            Toast.makeText(this, "Statistical selected from FAB", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_report) {
            Toast.makeText(this, "Report selected from FAB", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_overview) {
            Toast.makeText(this, "Expense Overview selected from FAB", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_tracking) {
            Toast.makeText(this, "Expense Tracking selected from FAB", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_budget_setting) {
            Toast.makeText(this, "Budget Setting selected from FAB", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_save_money) {
            Toast.makeText(this, "Save Money selected from FAB", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_salary) {
            Toast.makeText(this, "Salary selected from FAB", Toast.LENGTH_SHORT).show();
        }
    }


    // Handle Back Press to Close Drawer if Open
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Strings for Drawer Toggle
    private static final int R_string_navigation_drawer_open = R.string.navigation_drawer_open;
    private static final int R_string_navigation_drawer_close = R.string.navigation_drawer_close;
}