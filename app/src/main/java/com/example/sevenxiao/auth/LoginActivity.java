package com.example.sevenxiao.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.remote.AuthRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private AuthRepository authRepository;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginBtn;
    private TextView registerLink;
    private TextView errorText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        final int contentPadding = (int) (24 * getResources().getDisplayMetrics().density);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root), (v, insets) -> {
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

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginBtn = findViewById(R.id.login_btn);
        registerLink = findViewById(R.id.register_link);
        errorText = findViewById(R.id.error_text);

        loginBtn.setOnClickListener(v -> login());
        registerLink.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("请填写邮箱和密码");
            return;
        }

        loginBtn.setEnabled(false);
        loginBtn.setText("登录中...");
        errorText.setVisibility(View.GONE);

        String error = authRepository.login(email, password);
        loginBtn.setEnabled(true);
        loginBtn.setText("登录");

        if (error == null) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            showError(error);
        }
    }

    @Override
    public void onBackPressed() {
        // 未登录时按返回键直接退出 App
        finishAffinity();
    }

    private void showError(String msg) {
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(msg);
    }
}
