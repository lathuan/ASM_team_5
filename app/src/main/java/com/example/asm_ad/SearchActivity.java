package com.example.asm_ad;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Lấy userId từ Intent
        String userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo giao diện
        searchView = findViewById(R.id.search_view);
        listView = findViewById(R.id.list_view);

        // Khởi tạo dữ liệu giả lập
        searchResults = new ArrayList<>();
        searchResults.add("Chi tiêu: Ăn uống - 500.000 VND");
        searchResults.add("Thu nhập: Lương - 5.000.000 VND");
        searchResults.add("Chi tiêu: Mua sắm - 1.000.000 VND");

        // Thiết lập adapter cho ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResults);
        listView.setAdapter(adapter);

        // Xử lý sự kiện tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Xử lý khi nhấn tìm kiếm
                Toast.makeText(SearchActivity.this, "Tìm kiếm: " + query, Toast.LENGTH_SHORT).show();
                // Thêm logic thực tế, ví dụ: truy vấn cơ sở dữ liệu với userId
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Lọc kết quả theo thời gian thực
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}