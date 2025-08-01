package com.example.asm_ad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private EditText edtEmail, edtVerificationCode, edtNewPassword, edtConfirmPassword;
    private Button btnSendCode, btnVerifyCode, btnResetPassword;
    private ImageView iconTogglePassword1, iconTogglePassword2;
    private ProgressBar progressBar;
    private TextView tvMessage, tvResendCode;

    private String generatedCode;
    private String userEmail;
    private boolean isPasswordVisible1 = false;
    private boolean isPasswordVisible2 = false;

    private DatabaseHelper dbHelper;


    private Handler handler = new Handler();
    private int resendCountdown = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Ánh xạ view
        viewFlipper = findViewById(R.id.viewFlipper);
        edtEmail = findViewById(R.id.edtEmail);
        edtVerificationCode = findViewById(R.id.edtVerificationCode);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        iconTogglePassword1 = findViewById(R.id.iconTogglePassword1);
        iconTogglePassword2 = findViewById(R.id.iconTogglePassword2);
        progressBar = findViewById(R.id.progressBar);
        tvMessage = findViewById(R.id.tvMessage);
        tvResendCode = findViewById(R.id.tvResendCode);

        // Xử lý nút về đăng nhập
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Gửi mã xác nhận
        btnSendCode.setOnClickListener(v -> sendVerificationCode());

        // Xác nhận mã
        btnVerifyCode.setOnClickListener(v -> verifyCode());

        // Đặt lại mật khẩu
        btnResetPassword.setOnClickListener(v -> resetPassword());

        // Toggle hiển thị mật khẩu
        iconTogglePassword1.setOnClickListener(v -> togglePasswordVisibility(edtNewPassword, iconTogglePassword1, 1));
        iconTogglePassword2.setOnClickListener(v -> togglePasswordVisibility(edtConfirmPassword, iconTogglePassword2, 2));

        // Gửi lại mã
        tvResendCode.setOnClickListener(v -> resendCode());
    }

    private void sendVerificationCode() {
        String email = edtEmail.getText().toString().trim();

        // Kiểm tra email hợp lệ
        if (email.isEmpty()) {
            tvMessage.setText("Vui lòng nhập email");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvMessage.setText("Email không hợp lệ");
            return;
        }

        // Kiểm tra email có tồn tại trong hệ thống
        progressBar.setVisibility(View.VISIBLE);
        btnSendCode.setEnabled(false);

        // Sử dụng DatabaseHelper để kiểm tra email
        boolean emailExists = dbHelper.checkEmailExists(email);

        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            btnSendCode.setEnabled(true);

            if (emailExists) {
                userEmail = email;
                generatedCode = generateRandomCode();
                viewFlipper.setDisplayedChild(1); // Chuyển sang bước 2

                // HIỂN THỊ MÃ XÁC NHẬN TRONG THÔNG BÁO
                tvMessage.setText("Mã xác nhận đã được gửi đến " + email + "\nMã của bạn: " + generatedCode);

                // Bắt đầu đếm ngược gửi lại mã
                startResendCountdown();
            } else {
                tvMessage.setText("Email chưa được đăng ký trong hệ thống");
            }
        }, 1500);
    }


    private void verifyCode() {
        String code = edtVerificationCode.getText().toString().trim();

        if (code.isEmpty()) {
            tvMessage.setText("Vui lòng nhập mã xác nhận");
            return;
        }

        if (code.length() != 6) {
            tvMessage.setText("Mã xác nhận phải có 6 chữ số");
            return;
        }

        if (code.equals(generatedCode)) {
            viewFlipper.setDisplayedChild(2); // Chuyển sang bước 3
            tvMessage.setText("Vui lòng đặt mật khẩu mới");
        } else {
            tvMessage.setText("Mã xác nhận không chính xác");
        }
    }

    private void resetPassword() {
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty()) {
            tvMessage.setText("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (newPassword.length() <= 8) {
            tvMessage.setText("Mật khẩu phải lớn hơn 8 ký tự");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            tvMessage.setText("Mật khẩu xác nhận không khớp");
            return;
        }

        // Cập nhật mật khẩu mới trong database
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            boolean updateSuccess = dbHelper.updatePassword(userEmail, newPassword);
            progressBar.setVisibility(View.GONE);

            if (updateSuccess) {
                tvMessage.setText("Đặt lại mật khẩu thành công!");

                // Quay lại màn hình đăng nhập sau 2 giây
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }, 2000);
            } else {
                tvMessage.setText("Đặt lại mật khẩu thất bại! Vui lòng thử lại");
            }
        }, 1500);
    }

    private void togglePasswordVisibility(EditText editText, ImageView icon, int type) {
        if (type == 1) {
            isPasswordVisible1 = !isPasswordVisible1;
            if (isPasswordVisible1) {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                icon.setImageResource(R.drawable.visibility);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                icon.setImageResource(R.drawable.visibility_off);
            }
        } else {
            isPasswordVisible2 = !isPasswordVisible2;
            if (isPasswordVisible2) {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                icon.setImageResource(R.drawable.visibility);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                icon.setImageResource(R.drawable.visibility_off);
            }
        }
        editText.setSelection(editText.getText().length());
    }

    private void resendCode() {
        if (resendCountdown > 0) return;

        generatedCode = generateRandomCode();
        tvMessage.setText("Mã xác nhận mới đã được gửi đến " + userEmail);
        startResendCountdown();
    }

    private void startResendCountdown() {
        resendCountdown = 60;
        tvResendCode.setEnabled(false);
        tvResendCode.setTextColor(getResources().getColor(R.color.gray));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (resendCountdown > 0) {
                    tvResendCode.setText("Gửi lại (" + resendCountdown + "s)");
                    resendCountdown--;
                    handler.postDelayed(this, 1000);
                } else {
                    tvResendCode.setText("Gửi lại");
                    tvResendCode.setEnabled(true);
                    tvResendCode.setTextColor(getResources().getColor(R.color.primary_blue));
                }
            }
        }, 1000);
    }

    private String generateRandomCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}