package com.example.asm_ad;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat switchTheme;
    private DatabaseHelper dbHelper;
    private int userId = -1;
    private boolean isChangingTheme = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Áp dụng theme trước khi tạo giao diện
        applyStoredTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new DatabaseHelper(this);
        switchTheme = findViewById(R.id.switch_theme);

        loadSettings();

//        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            isChangingTheme = true;
//            saveThemeSetting(isChecked);
//            applyTheme(isChecked, false); //
//        });
    }

    private void applyStoredTheme() {
//        SharedPreferences prefs = getSharedPreferences("AppThemePrefs", MODE_PRIVATE);
//        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
//
//        if (isDarkMode) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);

//        if (userIdStr != null) {
//            try {
//                userId = Integer.parseInt(userIdStr);
//                ContentValues settings = dbHelper.getUserSettings(userId);
//
//                if (settings.size() > 0) {
//                    boolean darkMode = settings.getAsInteger(DatabaseHelper.COLUMN_SETTINGS_DARK_MODE) == 1;
//                    switchTheme.setChecked(darkMode);
//                }
//            } catch (NumberFormatException e) {
//
//            }
//        }
    }

    private void saveThemeSetting(boolean isDarkMode) {
        // Lưu vào cả SharedPreferences tạm thời
        SharedPreferences themePrefs = getSharedPreferences("AppThemePrefs", MODE_PRIVATE);
        themePrefs.edit().putBoolean("dark_mode", isDarkMode).apply();

        // Lưu vào database nếu có user
//        if (userId != -1) {
//            ContentValues values = new ContentValues();
//            values.put(DatabaseHelper.COLUMN_SETTINGS_DARK_MODE, isDarkMode ? 1 : 0);
//            dbHelper.updateUserSettings(userId, values);
//        }
    }

    private void applyTheme(boolean isDarkMode, boolean recreate) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (recreate) {
            recreate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Chỉ recreate nếu đang trong quá trình thay đổi theme
        if (isChangingTheme) {
            isChangingTheme = false;
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}