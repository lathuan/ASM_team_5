package com.example.asm_ad;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    private TextView tvUnreadCount;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Lấy số lượng thông báo chưa đọc từ Intent
        int unreadCount = getIntent().getIntExtra("unreadCount", 0);

        // Khởi tạo giao diện
        tvUnreadCount = findViewById(R.id.tv_unread_count);
        listView = findViewById(R.id.list_view);

        // Cập nhật số lượng thông báo chưa đọc
        tvUnreadCount.setText("Thông báo chưa đọc: " + unreadCount);

        // Khởi tạo dữ liệu giả lập
        notifications = new ArrayList<>();
        notifications.add("Thông báo: Chi tiêu vượt ngân sách - 10/07/2025");
        notifications.add("Thông báo: Nhận lương - 01/07/2025");
        notifications.add("Thông báo: Cập nhật ứng dụng - 30/06/2025");

        // Thiết lập adapter cho ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notifications);
        listView.setAdapter(adapter);

        // Xử lý khi nhấn vào thông báo
        listView.setOnItemClickListener((parent, view, position, id) -> {

        });
    }
}