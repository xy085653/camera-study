package com.example.sevenxiao.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sevenxiao.MainActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.remote.AuthRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private AuthRepository authRepository;
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton registerBtn;
    private TextView loginLink;
    private TextView errorText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        final int contentPadding = (int) (24 * getResources().getDisplayMetrics().density);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    contentPadding + systemBars.left,
                    contentPadding + systemBars.top,
                    contentPadding + systemBars.right,
                    contentPadding + systemBars.bottom
            );
            return insets;
        });

        authRepository = new AuthRepository(this);

        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        registerBtn = findViewById(R.id.register_btn);
        loginLink = findViewById(R.id.login_link);
        errorText = findViewById(R.id.error_text);

        registerBtn.setOnClickListener(v -> register());
        loginLink.setOnClickListener(v -> finish());
    }

    private void register() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("请填写所有字段");
            return;
        }
        if (password.length() < 6) {
            showError("密码至少6位");
            return;
        }

        registerBtn.setEnabled(false);
        registerBtn.setText("注册中...");
        errorText.setVisibility(View.GONE);

        String error = authRepository.register(email, password, name);
        registerBtn.setEnabled(true);
        registerBtn.setText("注册");

        if (error == null) {
            // 注册成功后自动登录并跳转主页
            authRepository.login(email, password);
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            showError(error);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void showError(String msg) {
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(msg);
    }
}
