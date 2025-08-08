package com.example.asm_ad;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout; // Import LinearLayout
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RecurringExpenseActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView listView;
    private RecurringExpenseAdapter adapter;
    private List<RecurringExpense> recurringExpenseList;
    private LinearLayout emptyView;

    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_expense);

        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.recurring_expense_list_view);
        emptyView = findViewById(R.id.empty_view);
        Button addButton = findViewById(R.id.add_recurring_expense_button);

        recurringExpenseList = new ArrayList<>();
        adapter = new RecurringExpenseAdapter(this, recurringExpenseList);
        listView.setAdapter(adapter);

        loadRecurringExpenses();

        addButton.setOnClickListener(v -> {
            showAddRecurringExpenseDialog();
        });
    }

    private void loadRecurringExpenses() {
        recurringExpenseList.clear();
        Cursor cursor = dbHelper.getRecurringExpensesByUserId(currentUserId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECURRING_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECURRING_USER_ID));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECURRING_CATEGORY_ID));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECURRING_AMOUNT));
                String frequency = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECURRING_FREQUENCY));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECURRING_START));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECURRING_END));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECURRING_DESCRIPTION));

                RecurringExpense expense = new RecurringExpense();
                expense.setRecurringExpenseId(id);
                expense.setUserId(userId);
                expense.setCategoryId(categoryId);
                expense.setAmount(amount);
                expense.setFrequency(frequency);
                expense.setStartDate(startDate);
                expense.setEndDate(endDate);
                expense.setDescription(description);

                recurringExpenseList.add(expense);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        adapter.notifyDataSetChanged();

        if (recurringExpenseList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void showAddRecurringExpenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm chi tiêu định kỳ");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_recurring_expense, null);
        builder.setView(dialogView);

        // Khai báo và ánh xạ các EditText
        EditText etAmount = dialogView.findViewById(R.id.et_amount);
        EditText etDescription = dialogView.findViewById(R.id.et_description);
        EditText etStartDate = dialogView.findViewById(R.id.et_start_date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etStartDate.setText(sdf.format(new Date()));

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String amountStr = etAmount.getText().toString();
            String description = etDescription.getText().toString();
            String startDate = etStartDate.getText().toString();

            if (amountStr.isEmpty() || description.isEmpty() || startDate.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                int categoryId = 1;
                String frequency = "Monthly";

                RecurringExpense newExpense = new RecurringExpense();
                newExpense.setUserId(currentUserId);
                newExpense.setCategoryId(categoryId);
                newExpense.setAmount(amount);
                newExpense.setFrequency(frequency);
                newExpense.setStartDate(startDate);
                newExpense.setDescription(description);

                long result = dbHelper.addRecurringExpense(newExpense);

                if (result > 0) {
                    Toast.makeText(this, "Đã thêm chi tiêu định kỳ!", Toast.LENGTH_SHORT).show();
                    loadRecurringExpenses();
                } else {
                    Toast.makeText(this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private class RecurringExpenseAdapter extends ArrayAdapter<RecurringExpense> {

        private Context mContext;

        public RecurringExpenseAdapter(@NonNull Context context, @NonNull List<RecurringExpense> objects) {
            super(context, 0, objects);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_recurring_expense, parent, false);
            }

            RecurringExpense currentExpense = getItem(position);

            TextView descriptionTextView = convertView.findViewById(R.id.item_description);
            TextView frequencyTextView = convertView.findViewById(R.id.item_frequency);
            TextView amountTextView = convertView.findViewById(R.id.item_amount);
            ImageButton deleteButton = convertView.findViewById(R.id.item_delete_button);

            descriptionTextView.setText(currentExpense.getDescription());
            frequencyTextView.setText("Tần suất: " + currentExpense.getFrequency());
            amountTextView.setText("Số tiền: " + String.format("%,.0f VNĐ", currentExpense.getAmount()));

            deleteButton.setOnClickListener(v -> {
                dbHelper.deleteRecurringExpense(currentExpense.getRecurringExpenseId());
                Toast.makeText(mContext, "Đã xóa chi tiêu: " + currentExpense.getDescription(), Toast.LENGTH_SHORT).show();
                loadRecurringExpenses();
            });

            return convertView;
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