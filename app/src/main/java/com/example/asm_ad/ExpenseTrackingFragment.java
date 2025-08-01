package com.example.asm_ad;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExpenseTrackingFragment extends Fragment {

    private TextView tvMonthExpense;
    private ProgressBar progressBar;
    private RecyclerView rvExpenses;
    private ExpenseAdapter expenseAdapter;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_tracking, container, false);

        // Khởi tạo các view
        tvMonthExpense = view.findViewById(R.id.tv_month_expense);
        progressBar = view.findViewById(R.id.progress_bar);
        rvExpenses = view.findViewById(R.id.rv_expenses);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Thiết lập RecyclerView
        rvExpenses.setLayoutManager(new LinearLayoutManager(requireContext()));
        expenseAdapter = new ExpenseAdapter(getExpenseHistory());
        rvExpenses.setAdapter(expenseAdapter);

        // Cập nhật giao diện
        refreshData();

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

    public ExpenseAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public void updateExpenses(List<Expense> newExpenses) {
        this.expenses = newExpenses;
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
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvDescription, tvTimestamp;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_expense_amount);
            tvDescription = itemView.findViewById(R.id.tv_expense_description);
            tvTimestamp = itemView.findViewById(R.id.tv_expense_timestamp);
        }
    }
}