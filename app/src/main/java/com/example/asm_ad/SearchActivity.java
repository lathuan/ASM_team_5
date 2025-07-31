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
    private ArrayList<String> expenseList;

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

        // Khởi tạo danh sách chi tiêu giả lập (thay bằng dữ liệu thực tế)
        expenseList = new ArrayList<>();
        expenseList.add("Chi tiêu: Ăn uống - 500.000 VND - 07/31/2025");
        expenseList.add("Chi tiêu: Mua sắm - 1.000.000 VND - 07/30/2025");
        expenseList.add("Chi tiêu: Tiền nhà - 3.000.000 VND - 07/29/2025");

        // Thiết lập adapter cho ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenseList);
        listView.setAdapter(adapter);

        // Xử lý sự kiện tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(SearchActivity.this, "Tìm kiếm: " + query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Lọc danh sách chi tiêu theo từ khóa
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}