package com.example.asm_ad;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class SavingsGoalsFragment extends Fragment {

    private CalendarView calendarView;
    private TextView tvNote;
    private Map<String, String> notes;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savings_goals, container, false);

        // Khởi tạo các view
        calendarView = view.findViewById(R.id.calendar_view);
        tvNote = view.findViewById(R.id.tv_note);

        // Khởi tạo SharedPreferences cho ghi chú
        prefs = requireActivity().getSharedPreferences("SavingsNotes", requireActivity().MODE_PRIVATE);
        notes = new HashMap<>();
        loadNotes();

        // Xử lý chọn ngày trên lịch
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            String note = notes.getOrDefault(date, "");
            tvNote.setText(note.isEmpty() ? "Không có ghi chú" : note);

            // Hiển thị dialog để thêm/sửa ghi chú
            showNoteDialog(date, note);
        });

        return view;
    }

    private void showNoteDialog(String date, String existingNote) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Ghi chú cho " + date);

        final EditText input = new EditText(requireContext());
        input.setText(existingNote);
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String note = input.getText().toString().trim();
            notes.put(date, note);
            tvNote.setText(note.isEmpty() ? "Không có ghi chú" : note);
            saveNotes();
            Toast.makeText(requireContext(), "Đã lưu ghi chú", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void loadNotes() {
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            notes.put(entry.getKey(), entry.getValue().toString());
        }
    }

    private void saveNotes() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        for (Map.Entry<String, String> entry : notes.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }
}