package com.example.asm_ad;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private TextView tvWelcome;
    private TextView tvUsername;
    private TextView tvBalance;
    private TextView tvTotalIncome;
    private TextView tvTotalExpense;
    private LineChart sparkLineChart;

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
        sparkLineChart = view.findViewById(R.id.sparkLineChart);

        // Cập nhật dữ liệu
        updateUserInfo();
        updateBalance();
        updateTotalExpense();
        updateTotalIncome();
        setupLineChart();

        return view;
    }

    private void updateUserInfo() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "Tên Người Dùng");
        if (tvUsername != null) tvUsername.setText(username);
    }

    private void updateBalance() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
            double balance = dbHelper.getUserBalance(userId);
            if (tvBalance != null) tvBalance.setText(String.format("%,.0f VND", balance));
        }
    }

    private void updateTotalExpense() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
            double totalExpense = dbHelper.getUserTotalExpense(userId);
            if (tvTotalExpense != null) tvTotalExpense.setText(String.format("%,.0f VND", totalExpense));
        }
    }

    private void updateTotalIncome() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
            double totalIncome = dbHelper.getUserTotalIncome(userId);
            if (tvTotalIncome != null) {
                tvTotalIncome.setText(String.format("%,.0f VND", totalIncome));
            }
        }
    }

    private void setupLineChart() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
            float[] monthlyExpenses = dbHelper.getMonthlyExpenses(userId);

            // Tạo dữ liệu cho biểu đồ
            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                entries.add(new Entry(i + 1, monthlyExpenses[i]));
            }

            LineDataSet dataSet = new LineDataSet(entries, "Chi tiêu hàng tháng");
            dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
            dataSet.setLineWidth(2f);
            dataSet.setDrawCircles(true); // Hiển thị điểm trên đường
            dataSet.setCircleColor(getResources().getColor(android.R.color.holo_blue_dark));
            dataSet.setCircleRadius(4f);
            dataSet.setDrawValues(true); // Hiển thị giá trị trên điểm
            dataSet.setValueTextSize(10f);
            dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Đường cong mượt mà

            LineData lineData = new LineData(dataSet);
            sparkLineChart.setData(lineData);

            // Tùy chỉnh giao diện Line Chart
            sparkLineChart.getDescription().setEnabled(false); // Tắt mô tả
            sparkLineChart.getLegend().setEnabled(true); // Bật chú thích
            sparkLineChart.getXAxis().setDrawGridLines(true); // Bật lưới trục X
            sparkLineChart.getAxisLeft().setDrawGridLines(true); // Bật lưới trục Y
            sparkLineChart.getXAxis().setDrawLabels(true);
            sparkLineChart.getXAxis().setLabelCount(12, true);
            sparkLineChart.getXAxis().setGranularity(1f);
            sparkLineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return "Th" + (int) value; // Nhãn tháng: Th1, Th2, ...
                }
            });
            sparkLineChart.getAxisLeft().setDrawLabels(true); // Bật nhãn trục Y
            sparkLineChart.getAxisRight().setEnabled(false); // Tắt trục Y phải
            sparkLineChart.setTouchEnabled(true); // Bật tương tác
            sparkLineChart.setPinchZoom(false); // Tắt zoom
            sparkLineChart.invalidate(); // Vẽ lại biểu đồ
        }
    }

    public void refreshData() {
        updateUserInfo();
        updateBalance();
        updateTotalExpense();
        updateTotalIncome();
        setupLineChart();
    }
}