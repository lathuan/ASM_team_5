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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatisticalFragment extends Fragment {

    private TextView tvBalance, tvTotalExpense;
    private BarChart barChart;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistical, container, false);

        tvBalance = view.findViewById(R.id.tv_balance);
        tvTotalExpense = view.findViewById(R.id.tv_total_expense);
        barChart = view.findViewById(R.id.bar_chart);

        dbHelper = new DatabaseHelper(requireContext());

        refreshData();

        return view;
    }

    private void refreshData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            try {
                int userId = Integer.parseInt(userIdStr);
                double balance = dbHelper.getUserBalance(userId);
                double totalExpense = dbHelper.getUserTotalExpense(userId);
                tvBalance.setText(String.format("Balance: %,.0f VND", balance));
                tvTotalExpense.setText(String.format("Total expenditure: %,.0f VND", totalExpense));
                setupBarChart(userId);
            } catch (NumberFormatException e) {
                tvBalance.setText("Balance: 0 VND");
                tvTotalExpense.setText("Total expenditure: 0 VND");
            }
        }
    }

    private void setupBarChart(int userId) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        double[] weeklyExpenses = getWeeklyExpenses(userId);
        for (int i = 0; i < weeklyExpenses.length; i++) {
            entries.add(new BarEntry(i, (float) weeklyExpenses[i]));
            labels.add("Week " + (i + 1));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Weekly expenses");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.2f);

        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(1000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);

        barChart.invalidate();
    }

    private double[] getWeeklyExpenses(int userId) {
        SharedPreferences expensePrefs = requireActivity().getSharedPreferences("ExpenseHistory_" + userId, requireActivity().MODE_PRIVATE);
        String expenseHistory = expensePrefs.getString("expenses", "");
        String[] lines = expenseHistory.split("\n");

        double[] weeklyExpenses = new double[4];
        Calendar current = Calendar.getInstance();
        int currentWeek = current.get(Calendar.WEEK_OF_YEAR);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    double amount = Double.parseDouble(parts[0]);
                    Date date = format.parse(parts[2]);
                    Calendar entryCal = Calendar.getInstance();
                    entryCal.setTime(date);
                    int weekOfYear = entryCal.get(Calendar.WEEK_OF_YEAR);

                    int weekDiff = currentWeek - weekOfYear;
                    if (weekDiff >= 0 && weekDiff < 4) {
                        weeklyExpenses[3 - weekDiff] += amount;
                    }
                }
            } catch (Exception ignored) {}
        }
        return weeklyExpenses;
    }
}
