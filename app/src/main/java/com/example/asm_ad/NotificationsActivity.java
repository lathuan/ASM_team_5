package com.example.asm_ad;

import android.app.Activity;
import android.content.Intent;
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
    private int unreadCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Lấy số lượng thông báo chưa đọc và danh sách thông báo từ Intent
        unreadCount = getIntent().getIntExtra("unreadCount", 0);
        String notificationsStr = getIntent().getStringExtra("notifications");

        // Khởi tạo giao diện
        tvUnreadCount = findViewById(R.id.tv_unread_count);
        listView = findViewById(R.id.list_view);

        // Cập nhật số lượng thông báo chưa đọc
        tvUnreadCount.setText("Thông báo chưa đọc: " + unreadCount);

        // Khởi tạo danh sách thông báo từ chuỗi nhận được
        notifications = new ArrayList<>();
        if (notificationsStr != null && !notificationsStr.isEmpty()) {
            String[] notificationArray = notificationsStr.split("\n");
            for (String notification : notificationArray) {
                if (!notification.trim().isEmpty()) {
                    notifications.add(notification.trim());
                }
            }
        }

        // Thiết lập adapter cho ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notifications);
        listView.setAdapter(adapter);

        // Xử lý khi nhấn vào thông báo (đánh dấu đã đọc)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (unreadCount > 0) {
                unreadCount--;
                tvUnreadCount.setText("Thông báo chưa đọc: " + unreadCount);
                notifications.remove(position);
                adapter.notifyDataSetChanged();

                // Gửi kết quả quay lại MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("unreadCount", unreadCount);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}