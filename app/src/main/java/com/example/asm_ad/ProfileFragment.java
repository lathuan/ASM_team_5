package com.example.asm_ad;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private ImageView btnMore;
    private TextView tvUsername, tvEmail, tvPhone;
    private MaterialButton btnEditProfile, btnLogout;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Khởi tạo view
        profileImage = view.findViewById(R.id.profile_image);
        btnMore = view.findViewById(R.id.btn_more);
        tvUsername = view.findViewById(R.id.tvProfileUsername);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        tvPhone = view.findViewById(R.id.tvProfilePhone);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Load user info
        loadUserInfo();

        // Xử lý sự kiện click avatar
        profileImage.setOnClickListener(v -> showProfileOptions());

        // Xử lý sự kiện nút menu
        btnMore.setOnClickListener(v -> showProfileOptions());

        // Xử lý sự kiện chỉnh sửa hồ sơ
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        // Xử lý sự kiện đăng xuất
        btnLogout.setOnClickListener(v -> logout());

        // Xử lý sự kiện cho nút Cài đặt

        view.findViewById(R.id.settings_card).setOnClickListener(v -> {
            // Mở màn hình cài đặt
            Intent intent = new Intent(requireContext(), SettingsActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserInfo() {
        SharedPreferences userPrefs = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = userPrefs.getString("username", "UserName");
        String email = userPrefs.getString("email", "email@example.com");
        String phone = userPrefs.getString("phone", "0123456789");

        tvUsername.setText(username);
        tvEmail.setText(email);
        tvPhone.setText(phone);
    }

    private void showProfileOptions() {
        // Tạo popup menu
        PopupMenu popupMenu = new PopupMenu(requireContext(), btnMore);

        // Thêm menu items
        popupMenu.getMenu().add(0, 1, 0, "Delete Account");
        popupMenu.getMenu().add(0, 2, 1, "Logout");

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == 1) {
                deleteAccount();
                return true;
            } else if (id == 2) {
                logout();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void deleteAccount() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete this account?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Xử lý xóa tài khoản
                    deleteAccountFromDatabase();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccountFromDatabase() {
        SharedPreferences userPrefs = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userIdStr = userPrefs.getString("userId", null);

        if (userIdStr != null) {
            try {
                int userId = Integer.parseInt(userIdStr);
                boolean success = dbHelper.deleteAllUserData(userId);

                if (success) {
                    // Đăng xuất sau khi xóa
                    logout();
                    Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Account deletion failed", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit profile");

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText edtFullName = view.findViewById(R.id.edtFullName);
        EditText edtEmail = view.findViewById(R.id.edtEmail);
        EditText edtPhone = view.findViewById(R.id.edtPhone);

        // Hiển thị thông tin hiện tại
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        edtFullName.setText(prefs.getString("fullName", ""));
        edtEmail.setText(prefs.getString("email", ""));
        edtPhone.setText(prefs.getString("phone", ""));

        builder.setView(view);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newFullName = edtFullName.getText().toString();
            String newEmail = edtEmail.getText().toString();
            String newPhone = edtPhone.getText().toString();

            updateUserProfile(newFullName, newEmail, newPhone);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateUserProfile(String fullName, String email, String phone) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);

        if (userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_FULLNAME, fullName);
            values.put(DatabaseHelper.COLUMN_USER_EMAIL, email);
            values.put(DatabaseHelper.COLUMN_USER_PHONE, phone);

            boolean success = dbHelper.updateUserProfile(userId, values);

            if (success) {
                // Cập nhật session
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("fullName", fullName);
                editor.putString("email", email);
                editor.putString("phone", phone);
                editor.apply();

                // Cập nhật UI
                loadUserInfo();
                Toast.makeText(requireContext(), "Profile update successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Profile update failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logout() {
        // Xóa session người dùng
        SharedPreferences userPrefs = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userPrefs.edit().clear().apply();

        // Chuyển đến màn hình đăng nhập
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật thông tin khi quay lại fragment
        loadUserInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}