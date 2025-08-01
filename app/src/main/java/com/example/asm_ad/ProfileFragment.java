package com.example.asm_ad;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hiển thị thông tin người dùng
        SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "Tên Người Dùng");
        String email = prefs.getString("email", "email@example.com");
        String phone = prefs.getString("phone", "0123456789");

        TextView tvUsername = view.findViewById(R.id.tvProfileUsername);
        TextView tvEmail = view.findViewById(R.id.tvProfileEmail);
        TextView tvPhone = view.findViewById(R.id.tvProfilePhone);

        tvUsername.setText(username);
        tvEmail.setText(email);
        tvPhone.setText(phone);
    }
}