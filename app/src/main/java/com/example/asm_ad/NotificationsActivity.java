package com.example.asm_ad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        ImageView btnClose = findViewById(R.id.btn_close);

        // Cập nhật số lượng thông báo chưa đọc
        tvUnreadCount.setText("Unread notifications: " + unreadCount);

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

        // Thiết lập adapter cho ListView với custom layout
        adapter = new ArrayAdapter<>(this, R.layout.notification_item, R.id.tv_notification, notifications);
        listView.setAdapter(adapter);

        // Xử lý khi nhấn vào thông báo (đánh dấu đã đọc)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (unreadCount > 0) {
                unreadCount--;
                tvUnreadCount.setText("Unread notifications: " + unreadCount);
                notifications.remove(position);
                adapter.notifyDataSetChanged();

                // Gửi kết quả quay lại MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("unreadCount", unreadCount);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        // Xử lý nút Đóng
        btnClose.setOnClickListener(v -> finish());
    }
}