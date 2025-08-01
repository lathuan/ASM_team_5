package com.example.asm_ad;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class IncomeTrackingFragment extends Fragment {

    private TextView tvBalance;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_tracking, container, false);

        // Khởi tạo các view
        tvBalance = view.findViewById(R.id.tv_balance);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Cập nhật giao diện
        refreshData();

        return view;
    }

    public void refreshData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);
            double balance = dbHelper.getUserBalance(userId);
            tvBalance.setText(String.format("%,.0f VND", balance));
        }
    }
}