package com.example.asm_ad;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
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

public class ExpenseTrackingFragment extends Fragment {

    private TextView tvMonthExpense;
    private ProgressBar progressBar;
    private RecyclerView rvExpenses;
    private ExpenseAdapter expenseAdapter;
    private DatabaseHelper dbHelper;
    private Map<Integer, Boolean> selectedExpenses = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_tracking, container, false);

        // Khởi tạo các view
        tvMonthExpense = view.findViewById(R.id.tv_month_expense);
        progressBar = view.findViewById(R.id.progress_bar);
        rvExpenses = view.findViewById(R.id.rv_expenses);
        TextView btnDeleteSelected = view.findViewById(R.id.btn_delete_history);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Thiết lập RecyclerView
        rvExpenses.setLayoutManager(new LinearLayoutManager(requireContext()));
        expenseAdapter = new ExpenseAdapter(getExpenseHistory(), selectedExpenses);
        rvExpenses.setAdapter(expenseAdapter);

        // Cập nhật giao diện
        refreshData();

        // Xử lý nút Xóa đã chọn
        btnDeleteSelected.setOnClickListener(v -> {
            if (selectedExpenses.isEmpty() || !selectedExpenses.containsValue(true)) {
                Toast.makeText(requireContext(), "Vui lòng chọn ít nhất một giao dịch để xóa", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc muốn xóa các giao dịch đã chọn? Số dư sẽ được hồi lại.")
                    .setPositiveButton("Có", (dialog, which) -> deleteSelectedExpenses())
                    .setNegativeButton("Không", null)
                    .show();
        });

        return view;
    }

    public void refreshData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);
            double totalExpense = dbHelper.getUserTotalExpense(userId);
            tvMonthExpense.setText(String.format("%,.0f VND", totalExpense));
            progressBar.setProgress(calculateProgress(totalExpense));
            expenseAdapter.updateExpenses(getExpenseHistory());
            selectedExpenses.clear(); // Xóa trạng thái chọn khi làm mới
            expenseAdapter.notifyDataSetChanged();
        }
    }

    private List<Expense> getExpenseHistory() {
        List<Expense> expenses = new ArrayList<>();
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            SharedPreferences expensePrefs = requireActivity().getSharedPreferences("ExpenseHistory_" + userIdStr, requireActivity().MODE_PRIVATE);
            String expenseHistory = expensePrefs.getString("expenses", "");
            if (!expenseHistory.isEmpty()) {
                String[] expenseLines = expenseHistory.split("\n");
                for (String line : expenseLines) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        try {
                            double amount = Double.parseDouble(parts[0].trim());
                            String description = parts[1];
                            String timestamp = parts[2];
                            expenses.add(new Expense(amount, description, timestamp));
                        } catch (NumberFormatException e) {
                            // Bỏ qua nếu dữ liệu không hợp lệ
                        }
                    }
                }
            }
        }
        return expenses;
    }

    private void deleteSelectedExpenses() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
        String userIdStr = prefs.getString("userId", null);
        if (userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);
            SharedPreferences expensePrefs = requireActivity().getSharedPreferences("ExpenseHistory_" + userIdStr, requireActivity().MODE_PRIVATE);
            String expenseHistory = expensePrefs.getString("expenses", "");
            List<String> remainingLines = new ArrayList<>();
            double totalExpenseToRestore = 0;

            if (!expenseHistory.isEmpty()) {
                String[] expenseLines = expenseHistory.split("\n");
                for (int i = 0; i < expenseLines.length; i++) {
                    if (!selectedExpenses.getOrDefault(i, false)) {
                        remainingLines.add(expenseLines[i]);
                    } else {
                        String[] parts = expenseLines[i].split("\\|");
                        if (parts.length == 3) {
                            try {
                                double amount = Double.parseDouble(parts[0].trim());
                                totalExpenseToRestore += amount;
                            } catch (NumberFormatException e) {
                                // Bỏ qua nếu dữ liệu không hợp lệ
                            }
                        }
                    }
                }
            }

            // Cập nhật số dư trong cơ sở dữ liệu (hồi lại số tiền chi tiêu đã xóa)
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            double currentBalance = dbHelper.getUserBalance(userId);
            double currentExpense = dbHelper.getUserTotalExpense(userId);
            double newBalance = currentBalance + totalExpenseToRestore; // Hồi lại số dư
            double newExpense = Math.max(0, currentExpense - totalExpenseToRestore); // Giảm tổng chi tiêu
            dbHelper.updateUserBalance(userId, newBalance);
            dbHelper.updateUserExpense(userId, newExpense);

            // Lưu lại lịch sử đã lọc
            SharedPreferences.Editor editor = expensePrefs.edit();
            editor.putString("expenses", String.join("\n", remainingLines));
            editor.apply();

            // Làm mới giao diện
            refreshData();
            Toast.makeText(requireContext(), "Đã xóa các giao dịch và hồi lại số dư", Toast.LENGTH_SHORT).show();
        }
    }

    private int calculateProgress(double totalExpense) {
        double budgetLimit = 10000000; // Giả định ngân sách 10 triệu
        return (int) ((totalExpense / budgetLimit) * 100);
    }
}

class Expense {
    private double amount;
    private String description;
    private String timestamp;

    public Expense(double amount, String description, String timestamp) {
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

class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenses;
    private Map<Integer, Boolean> selectedExpenses;

    public ExpenseAdapter(List<Expense> expenses, Map<Integer, Boolean> selectedExpenses) {
        this.expenses = expenses;
        this.selectedExpenses = selectedExpenses;
        for (int i = 0; i < expenses.size(); i++) {
            this.selectedExpenses.put(i, false); // Khởi tạo tất cả là không chọn
        }
    }

    public void updateExpenses(List<Expense> newExpenses) {
        this.expenses = newExpenses;
        this.selectedExpenses.clear();
        for (int i = 0; i < newExpenses.size(); i++) {
            this.selectedExpenses.put(i, false); // Khởi tạo lại trạng thái chọn
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.tvAmount.setText(String.format("%,.0f VND", expense.getAmount()));
        holder.tvDescription.setText(expense.getDescription());
        holder.tvTimestamp.setText(expense.getTimestamp());
        holder.checkBox.setChecked(selectedExpenses.getOrDefault(position, false));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedExpenses.put(position, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvDescription, tvTimestamp;
        CheckBox checkBox;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_expense_amount);
            tvDescription = itemView.findViewById(R.id.tv_expense_description);
            tvTimestamp = itemView.findViewById(R.id.tv_expense_timestamp);
            checkBox = itemView.findViewById(R.id.checkbox_expense);
        }
    }
}