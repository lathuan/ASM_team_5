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
            tvMessage.setText("Please enter email");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvMessage.setText("Invalid email");
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
                tvMessage.setText("Verification code has been sent to " + email + "\nYour OTP: " + generatedCode);

                // Bắt đầu đếm ngược gửi lại mã
                startResendCountdown();
            } else {
                tvMessage.setText("Email is not registered in the system");
            }
        }, 1500);
    }


    private void verifyCode() {
        String code = edtVerificationCode.getText().toString().trim();

        if (code.isEmpty()) {
            tvMessage.setText("Please enter the confirmation code");
            return;
        }

        if (code.length() != 6) {
            tvMessage.setText("The confirmation code must be 6 digits long.");
            return;
        }

        if (code.equals(generatedCode)) {
            viewFlipper.setDisplayedChild(2); // Chuyển sang bước 3
            tvMessage.setText("Please set a new password");
        } else {
            tvMessage.setText("Incorrect confirmation code");
        }
    }

    private void resetPassword() {
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty()) {
            tvMessage.setText("Please enter new password");
            return;
        }

        if (newPassword.length() <= 8) {
            tvMessage.setText("Password must be greater than 8 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            tvMessage.setText("Confirmation password does not match");
            return;
        }

        // Cập nhật mật khẩu mới trong database
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            boolean updateSuccess = dbHelper.updatePassword(userEmail, newPassword);
            progressBar.setVisibility(View.GONE);

            if (updateSuccess) {
                tvMessage.setText("Password reset successful!");

                // Quay lại màn hình đăng nhập sau 2 giây
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }, 2000);
            } else {
                tvMessage.setText("Password reset failed! Please try again");
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
        tvMessage.setText("A new confirmation code has been sent " + userEmail);
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
                    tvResendCode.setText("Resend (" + resendCountdown + "second)");
                    resendCountdown--;
                    handler.postDelayed(this, 1000);
                } else {
                    tvResendCode.setText("Resend");
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