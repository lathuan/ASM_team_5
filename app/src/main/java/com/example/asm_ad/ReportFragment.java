package com.example.asm_ad;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ReportFragment extends Fragment {

        private MaterialButton btnTimeFilter;
        private MaterialButton btnCategoryFilter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_report, container, false);

            // Thiết lập toolbar
            MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);

            // Khởi tạo view
            btnTimeFilter = view.findViewById(R.id.btnTimeFilter);
            btnCategoryFilter = view.findViewById(R.id.btnCategoryFilter);

            // Thiết lập sự kiện
            setupEventListeners();

            return view;
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

        private void setupEventListeners() {
            // Sự kiện lọc thời gian
            btnTimeFilter.setOnClickListener(v -> showTimeFilterDialog());

            // Sự kiện lọc danh mục
            btnCategoryFilter.setOnClickListener(v -> showCategoryFilterDialog());

            // Sự kiện xem thêm giao dịch
//            view.findViewById(R.id.btnViewMore).setOnClickListener(v -> {
//                // Chuyển đến màn hình xem tất cả giao dịch
//                Navigation.findNavController(v).navigate(R.id.action_to_transactions);
//            });
        }

        private void showTimeFilterDialog() {
            String[] timeOptions = {"Hôm nay", "Tuần này", "Tháng này", "Quý này", "Năm nay", "Tùy chỉnh"};

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Chọn khoảng thời gian")
                    .setItems(timeOptions, (dialog, which) -> {
                        btnTimeFilter.setText(timeOptions[which]);
                        // Cập nhật dữ liệu theo thời gian đã chọn
                        loadReportData(which);
                    })
                    .show();
        }

        private void showCategoryFilterDialog() {
            // Giả sử danh sách danh mục từ database
            String[] categories = {"Tất cả", "Ăn uống", "Di chuyển", "Giải trí", "Học tập", "Lương", "Khác"};

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Chọn danh mục")
                    .setItems(categories, (dialog, which) -> {
                        btnCategoryFilter.setText(categories[which]);
                        // Cập nhật dữ liệu theo danh mục đã chọn
                        filterByCategory(categories[which]);
                    })
                    .show();
        }

        private void exportReport() {
            // Logic xuất báo cáo (PDF, Excel, ...)
            Toast.makeText(requireContext(), "Xuất báo cáo thành công", Toast.LENGTH_SHORT).show();

            // Mở dialog chọn định dạng
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Xuất báo cáo")
                    .setItems(new String[]{"PDF", "Excel", "Hình ảnh"}, (dialog, which) -> {
                        String format = "";
                        switch (which) {
                            case 0: format = "PDF"; break;
                            case 1: format = "Excel"; break;
                            case 2: format = "Hình ảnh"; break;
                        }
                        Toast.makeText(requireContext(), "Đang xuất dạng " + format, Toast.LENGTH_SHORT).show();
                    })
                    .show();
        }

        private void shareReport() {
            // Logic chia sẻ báo cáo
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Báo cáo tài chính");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Xem báo cáo tài chính của tôi...");
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
        }

        private void openReportSettings() {
            // Mở cài đặt báo cáo
            Toast.makeText(requireContext(), "Mở cài đặt báo cáo", Toast.LENGTH_SHORT).show();
        }

        private void loadReportData(int timeRange) {
            // Tải dữ liệu từ database theo khoảng thời gian
            // Cập nhật UI với dữ liệu mới
        }

        private void filterByCategory(String category) {
            // Lọc dữ liệu theo danh mục
            // Cập nhật UI với dữ liệu đã lọc
        }
    }