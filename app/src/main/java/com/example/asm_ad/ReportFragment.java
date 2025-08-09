package com.example.asm_ad;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_ad.DatabaseHelper;
import com.example.asm_ad.R;
import com.example.asm_ad.adapter.TransactionAdapter;
import com.example.asm_ad.model.Transaction;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportFragment extends Fragment {

    private MaterialButton btnTimeFilter;
    private MaterialButton btnCategoryFilter;
    private TextView tvTotalIncome;
    private TextView tvTotalExpense;
    private TextView tvTotalSavings;
    private RecyclerView recyclerViewTransactions;
    private TransactionAdapter transactionAdapter;

    private DatabaseHelper dbHelper;
    private int userId;
    private String currentStartDate = "";
    private String currentEndDate = "";
    private String selectedCategory = "All categories";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        dbHelper = new DatabaseHelper(requireContext());

        // Khởi tạo view
        initViews(view);

        // Thiết lập khoảng thời gian mặc định (tháng này)
        setupDefaultDateRange();

        // Tải dữ liệu báo cáo
        loadReportData();

        return view;
    }


    private void initViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((androidx.appcompat.app.AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        btnTimeFilter = view.findViewById(R.id.btnTimeFilter);
        btnCategoryFilter = view.findViewById(R.id.btnCategoryFilter);
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense);
        tvTotalSavings = view.findViewById(R.id.tvTotalSavings);
        recyclerViewTransactions = view.findViewById(R.id.recyclerViewTransactions);

        // Thiết lập RecyclerView
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionAdapter = new TransactionAdapter(new ArrayList<>());
        recyclerViewTransactions.setAdapter(transactionAdapter);

        // Thiết lập sự kiện
        setupEventListeners();
    }

    private void setupDefaultDateRange() {
        // Mặc định: tháng hiện tại
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        // Định dạng: yyyy-MM
        currentStartDate = String.format(Locale.getDefault(), "%04d-%02d-01", year, month);

        // Ngày cuối cùng của tháng
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        currentEndDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
    }

    private void setupEventListeners() {
        // Sự kiện lọc thời gian
        btnTimeFilter.setOnClickListener(v -> showTimeFilterDialog());

        // Sự kiện lọc danh mục
        btnCategoryFilter.setOnClickListener(v -> showCategoryFilterDialog());
    }

    private void showTimeFilterDialog() {
        String[] timeOptions = {"Today", "This Week", "This Month", "This Quarter", "This Year", "Custom"};

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Select time period")
                .setItems(timeOptions, (dialog, which) -> {
                    btnTimeFilter.setText(timeOptions[which]);
                    updateDateRange(which);
                    loadReportData();
                })
                .show();
    }

    private void updateDateRange(int timeRange) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        switch (timeRange) {
            case 0: // Hôm nay
                currentStartDate = sdf.format(calendar.getTime());
                currentEndDate = currentStartDate;
                break;

            case 1: // Tuần này
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                currentStartDate = sdf.format(calendar.getTime());
                calendar.add(Calendar.DAY_OF_WEEK, 6);
                currentEndDate = sdf.format(calendar.getTime());
                break;

            case 2: // Tháng này
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                currentStartDate = sdf.format(calendar.getTime());
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                currentEndDate = sdf.format(calendar.getTime());
                break;

            case 3: // Quý này
                int currentMonth = calendar.get(Calendar.MONTH);
                int quarterStartMonth = currentMonth - (currentMonth % 3);
                calendar.set(Calendar.MONTH, quarterStartMonth);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                currentStartDate = sdf.format(calendar.getTime());

                calendar.add(Calendar.MONTH, 2);
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                currentEndDate = sdf.format(calendar.getTime());
                break;

            case 4: // Năm nay
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                currentStartDate = sdf.format(calendar.getTime());
                calendar.set(Calendar.MONTH, 11);
                calendar.set(Calendar.DAY_OF_MONTH, 31);
                currentEndDate = sdf.format(calendar.getTime());
                break;

            case 5: // Tùy chỉnh
                // TODO: Triển khai chọn ngày tùy chỉnh
                Toast.makeText(requireContext(), "Custom date picker functionality is under development", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showCategoryFilterDialog() {
        // Lấy danh sách danh mục từ database
        List<String> categories = getCategoriesFromDatabase();

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Select category")
                .setItems(categories.toArray(new String[0]), (dialog, which) -> {
                    selectedCategory = categories.get(which);
                    btnCategoryFilter.setText(selectedCategory);
                    loadReportData();
                })
                .show();
    }

    private List<String> getCategoriesFromDatabase() {
        List<String> categories = new ArrayList<>();
        categories.add("All categories");

        Cursor cursor = dbHelper.getCategoriesByUserId(userId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                categories.add(categoryName);
            }
            cursor.close();
        }
        return categories;
    }

    private void loadReportData() {
        // Tải tổng thu nhập
        double totalIncome = getTotalIncome();
        tvTotalIncome.setText(formatCurrency(totalIncome));
        tvTotalIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));

        // Tải tổng chi tiêu
        double totalExpense = getTotalExpense();
        tvTotalExpense.setText(formatCurrency(totalExpense));
        tvTotalExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));

        // Tính tiết kiệm (thu - chi)
        double savings = totalIncome - totalExpense;
        tvTotalSavings.setText(formatCurrency(savings));
        tvTotalSavings.setTextColor(ContextCompat.getColor(requireContext(),
                savings >= 0 ? R.color.blue : R.color.red));

        // Tải giao dịch
        List<Transaction> transactions = getTransactions();
        transactionAdapter.updateData(transactions);
    }

    private double getTotalIncome() {
        String query = "SELECT SUM(" + DatabaseHelper.COLUMN_INCOME_AMOUNT + ") " +
                "FROM " + DatabaseHelper.TABLE_INCOME + " " +
                "WHERE " + DatabaseHelper.COLUMN_INCOME_USER_ID + " = ? " +
                "AND " + DatabaseHelper.COLUMN_INCOME_DATE + " BETWEEN ? AND ?";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId),
                currentStartDate,
                currentEndDate
        });

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    private double getTotalExpense() {
        String query = "SELECT SUM(" + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + ") " +
                "FROM " + DatabaseHelper.TABLE_EXPENSE + " e " +
                "JOIN " + DatabaseHelper.TABLE_CATEGORY + " c ON e." +
                DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID + " = c." +
                DatabaseHelper.COLUMN_CATEGORY_ID + " " +
                "WHERE e." + DatabaseHelper.COLUMN_EXPENSE_USER_ID + " = ? " +
                "AND e." + DatabaseHelper.COLUMN_EXPENSE_DATE + " BETWEEN ? AND ?";

        // Thêm điều kiện lọc danh mục nếu cần
        if (!selectedCategory.equals("All categories")) {
            query += " AND c." + DatabaseHelper.COLUMN_CATEGORY_NAME + " = ?";
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] params;

        if (!selectedCategory.equals("All categories")) {
            params = new String[]{
                    String.valueOf(userId),
                    currentStartDate,
                    currentEndDate,
                    selectedCategory
            };
        } else {
            params = new String[]{
                    String.valueOf(userId),
                    currentStartDate,
                    currentEndDate
            };
        }

        Cursor cursor = db.rawQuery(query, params);
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    private List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        // Truy vấn thu nhập
        String incomeQuery = "SELECT " +
                "i." + DatabaseHelper.COLUMN_INCOME_ID + " AS id, " +
                "'income' AS type, " +
                "i." + DatabaseHelper.COLUMN_INCOME_AMOUNT + " AS amount, " +
                "i." + DatabaseHelper.COLUMN_INCOME_DATE + " AS date, " +
                "i." + DatabaseHelper.COLUMN_INCOME_SOURCE + " AS description, " +
                "NULL AS category " +
                "FROM " + DatabaseHelper.TABLE_INCOME + " i " +
                "WHERE i." + DatabaseHelper.COLUMN_INCOME_USER_ID + " = ? " +
                "AND i." + DatabaseHelper.COLUMN_INCOME_DATE + " BETWEEN ? AND ?";

        // Truy vấn chi tiêu
        String expenseQuery = "SELECT " +
                "e." + DatabaseHelper.COLUMN_EXPENSE_ID + " AS id, " +
                "'expense' AS type, " +
                "e." + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + " AS amount, " +
                "e." + DatabaseHelper.COLUMN_EXPENSE_DATE + " AS date, " +
                "e." + DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION + " AS description, " +
                "c." + DatabaseHelper.COLUMN_CATEGORY_NAME + " AS category " +
                "FROM " + DatabaseHelper.TABLE_EXPENSE + " e " +
                "JOIN " + DatabaseHelper.TABLE_CATEGORY + " c ON e." +
                DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID + " = c." +
                DatabaseHelper.COLUMN_CATEGORY_ID + " " +
                "WHERE e." + DatabaseHelper.COLUMN_EXPENSE_USER_ID + " = ? " +
                "AND e." + DatabaseHelper.COLUMN_EXPENSE_DATE + " BETWEEN ? AND ?";

        // Thêm điều kiện lọc danh mục cho chi tiêu
        if (!selectedCategory.equals("All categories")) {
            expenseQuery += " AND c." + DatabaseHelper.COLUMN_CATEGORY_NAME + " = ?";
        }

        // Kết hợp cả hai truy vấn
        String unionQuery = incomeQuery + " UNION ALL " + expenseQuery + " ORDER BY date DESC";



        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] params;

        if (!selectedCategory.equals("All categories")) {
            params = new String[]{
                    String.valueOf(userId), currentStartDate, currentEndDate,
                    String.valueOf(userId), currentStartDate, currentEndDate, selectedCategory
            };
        } else {
            params = new String[]{
                    String.valueOf(userId), currentStartDate, currentEndDate,
                    String.valueOf(userId), currentStartDate, currentEndDate
            };
        }

        Cursor cursor = db.rawQuery(unionQuery, params);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Transaction transaction = new Transaction();
                transaction.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                transaction.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                transaction.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                transaction.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                transaction.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                transactions.add(transaction);
            }
            cursor.close();
        }

        return transactions;
    }

    private String formatCurrency(double amount) {
        return String.format(Locale.getDefault(), "%,.0f ₫", amount);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.report_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_export) {
            exportReport();
            return true;
        } else if (id == R.id.action_share) {
            shareReport();
            return true;
        } else if (id == R.id.action_settings) {
            openReportSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void exportReport() {
        // TODO: Triển khai xuất báo cáo
        Toast.makeText(requireContext(), "Report export successful", Toast.LENGTH_SHORT).show();
    }

    private void shareReport() {
        // TODO: Triển khai chia sẻ báo cáo
        Toast.makeText(requireContext(), "Share report", Toast.LENGTH_SHORT).show();
    }

    private void openReportSettings() {
        // TODO: Triển khai cài đặt báo cáo
        Toast.makeText(requireContext(), "Open report settings", Toast.LENGTH_SHORT).show();
    }
}