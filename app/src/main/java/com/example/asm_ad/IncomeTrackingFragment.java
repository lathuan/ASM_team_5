package com.example.asm_ad;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncomeTrackingFragment extends Fragment {

    private TextView tvBalance;
    private RecyclerView rvIncomes;
    private IncomeAdapter incomeAdapter;
    private DatabaseHelper dbHelper;
    private Map<Integer, Boolean> selectedIncomes = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_tracking, container, false);

        // Khởi tạo các view
        tvBalance = view.findViewById(R.id.tv_balance);
        rvIncomes = view.findViewById(R.id.rv_incomes);
        TextView btnDeleteSelected = view.findViewById(R.id.btn_delete_history);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Thiết lập RecyclerView
        rvIncomes.setLayoutManager(new LinearLayoutManager(requireContext()));
        incomeAdapter = new IncomeAdapter(getIncomeHistory(), selectedIncomes);
        rvIncomes.setAdapter(incomeAdapter);

        // Cập nhật giao diện
        refreshData();

        // Xử lý nút Xóa đã chọn
        btnDeleteSelected.setOnClickListener(v -> {
            if (selectedIncomes.isEmpty() || !selectedIncomes.containsValue(true)) {
                Toast.makeText(requireContext(), "Please select at least one transaction to delete", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete the selected transactions?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteSelectedIncomes())
                    .setNegativeButton("No", null)
                    .show();
        });

        return view;
    }

    public void refreshData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);
            double balance = dbHelper.getUserBalance(userId);
            tvBalance.setText(String.format("%,.0f VND", balance));
            incomeAdapter.updateIncomes(getIncomeHistory());
            selectedIncomes.clear(); // Xóa trạng thái chọn khi làm mới
            incomeAdapter.notifyDataSetChanged();
        }
    }

    private List<Income> getIncomeHistory() {
        List<Income> incomes = new ArrayList<>();
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            SharedPreferences incomePrefs = requireActivity().getSharedPreferences("IncomeHistory_" + userIdStr, requireActivity().MODE_PRIVATE);
            String incomeHistory = incomePrefs.getString("incomes", "");
            if (!incomeHistory.isEmpty()) {
                String[] incomeLines = incomeHistory.split("\n");
                for (String line : incomeLines) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        try {
                            double amount = Double.parseDouble(parts[0].trim());
                            String description = parts[1];
                            String timestamp = parts[2];
                            incomes.add(new Income(amount, description, timestamp));
                        } catch (NumberFormatException e) {
                            // Bỏ qua nếu dữ liệu không hợp lệ
                        }
                    }
                }
            }
        }
        return incomes;
    }

    private void deleteSelectedIncomes() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);
            SharedPreferences incomePrefs = requireActivity().getSharedPreferences("IncomeHistory_" + userIdStr, requireActivity().MODE_PRIVATE);
            String incomeHistory = incomePrefs.getString("incomes", "");
            List<String> remainingLines = new ArrayList<>();
            double totalIncomeToRemove = 0;

            if (!incomeHistory.isEmpty()) {
                String[] incomeLines = incomeHistory.split("\n");
                for (int i = 0; i < incomeLines.length; i++) {
                    if (!selectedIncomes.getOrDefault(i, false)) {
                        remainingLines.add(incomeLines[i]);
                    } else {
                        String[] parts = incomeLines[i].split("\\|");
                        if (parts.length == 3) {
                            try {
                                double amount = Double.parseDouble(parts[0].trim());
                                totalIncomeToRemove += amount;
                            } catch (NumberFormatException e) {
                                // Bỏ qua nếu dữ liệu không hợp lệ
                            }
                        }
                    }
                }
            }

            // Cập nhật số dư trong cơ sở dữ liệu (giảm số dư bằng tổng thu nhập đã xóa)
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            double currentBalance = dbHelper.getUserBalance(userId);
            double newBalance = Math.max(0, currentBalance - totalIncomeToRemove);
            dbHelper.updateUserBalance(userId, newBalance);

            // Lưu lại lịch sử đã lọc
            SharedPreferences.Editor editor = incomePrefs.edit();
            editor.putString("incomes", String.join("\n", remainingLines));
            editor.apply();

            // Làm mới giao diện
            refreshData();
            Toast.makeText(requireContext(), "Selected transactions deleted", Toast.LENGTH_SHORT).show();
        }
    }
}

class Income {
    private double amount;
    private String description;
    private String timestamp;

    public Income(double amount, String description, String timestamp) {
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {
    private List<Income> incomes;
    private Map<Integer, Boolean> selectedIncomes;

    public IncomeAdapter(List<Income> incomes, Map<Integer, Boolean> selectedIncomes) {
        this.incomes = incomes;
        this.selectedIncomes = selectedIncomes;
        for (int i = 0; i < incomes.size(); i++) {
            this.selectedIncomes.put(i, false); // Khởi tạo tất cả là không chọn
        }
    }

    public void updateIncomes(List<Income> newIncomes) {
        this.incomes = newIncomes;
        this.selectedIncomes.clear();
        for (int i = 0; i < newIncomes.size(); i++) {
            this.selectedIncomes.put(i, false); // Khởi tạo lại trạng thái chọn
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        Income income = incomes.get(position);
        holder.tvAmount.setText(String.format("%,.0f VND", income.getAmount()));
        holder.tvDescription.setText(income.getDescription());
        holder.tvTimestamp.setText(income.getTimestamp());
        holder.checkBox.setChecked(selectedIncomes.getOrDefault(position, false));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedIncomes.put(position, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return incomes.size();
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvDescription, tvTimestamp;
        CheckBox checkBox;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_income_amount);
            tvDescription = itemView.findViewById(R.id.tv_income_description);
            tvTimestamp = itemView.findViewById(R.id.tv_income_timestamp);
            checkBox = itemView.findViewById(R.id.checkbox_income);
        }
    }
}