package com.example.asm_ad.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_ad.R;
import com.example.asm_ad.model.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void updateData(List<Transaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.tvCategory.setText(transaction.getCategory() != null ?
                transaction.getCategory() : transaction.getDescription());

        holder.tvAmount.setText(formatCurrency(transaction.getAmount()));

        // Đặt màu cho số tiền
        if ("income".equals(transaction.getType())) {
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50")); // Màu xanh lá cho thu nhập
        } else {
            holder.tvAmount.setTextColor(Color.parseColor("#F44336")); // Màu đỏ cho chi tiêu
        }

        // Định dạng ngày: từ yyyy-MM-dd thành dd/MM
        String formattedDate = formatDate(transaction.getDate());
        holder.tvDate.setText(formattedDate);
    }

    private String formatCurrency(double amount) {
        return String.format("%,.0f ₫", amount);
    }

    private String formatDate(String dateStr) {
        try {
            // Giả sử định dạng đầu vào là "yyyy-MM-dd"
            String[] parts = dateStr.split("-");
            if (parts.length >= 3) {
                return parts[2] + "/" + parts[1];
            }
            return dateStr;
        } catch (Exception e) {
            return dateStr;
        }
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        TextView tvAmount;
        TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}