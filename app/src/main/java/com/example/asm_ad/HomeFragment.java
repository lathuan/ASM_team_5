package com.example.asm_ad;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private TextView tvWelcome;
    private TextView tvUsername;
    private TextView tvBalance;
    private TextView tvTotalIncome;
    private TextView tvTotalExpense;

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo các view từ fragment_home.xml
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvBalance = view.findViewById(R.id.tvBalance);
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense);

        // Cập nhật dữ liệu
        updateUserInfo();
        updateBalance();
        updateTotalExpense();

        return view;
    }
    private void updateUserInfo() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "Tên Người Dùng");
        if (tvUsername != null) tvUsername.setText(username);
    }

    private void updateBalance() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        long balance = prefs.getLong("balance", 0);
        if (tvBalance != null) tvBalance.setText(String.format("%,d VND", balance));
    }

    private void updateTotalExpense() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        long totalExpense = prefs.getLong("totalExpense", 0);
        if (tvTotalExpense != null) tvTotalExpense.setText(String.format("%,d VND", totalExpense));
    }

    // Có thể thêm phương thức để MainActivity gọi khi cần cập nhật
    public void refreshData() {
        updateUserInfo();
        updateBalance();
        updateTotalExpense();
    }
}