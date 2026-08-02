package com.stgeorge.church.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.stgeorge.church.R;
import com.stgeorge.church.firebase.AuthHelper;

/**
 * إنشاء حساب — lets a عام member self-register (role = member) instead of
 * needing a staff account created manually in the Firebase Console.
 */
public class RegisterActivity extends BaseActivity {

    private TextInputEditText etFullName, etPhone, etUsername, etPassword;
    private MaterialButton btnRegister;
    private CircularProgressIndicator progressRegister;
    private TextView tvError;

    private AuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authHelper = new AuthHelper();

        ImageView btnBack = findViewById(R.id.btnBack);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressRegister = findViewById(R.id.progressRegister);
        tvError = findViewById(R.id.tvError);

        btnBack.setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String fullName = text(etFullName);
        String phone = text(etPhone);
        String username = text(etUsername);
        String password = text(etPassword);

        if (fullName.isEmpty() || username.isEmpty() || password.length() < 6) {
            showError(getString(R.string.register_error_invalid));
            return;
        }

        setLoading(true);
        authHelper.register(username, password, fullName, phone, new AuthHelper.RegisterCallback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                android.widget.Toast.makeText(RegisterActivity.this,
                        R.string.register_success, android.widget.Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(String message) {
                setLoading(false);
                showError(getString(R.string.register_error_generic));
            }
        });
    }

    private String text(TextInputEditText field) {
        return field.getText() != null ? field.getText().toString().trim() : "";
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void setLoading(boolean loading) {
        progressRegister.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
        if (loading) {
            tvError.setVisibility(View.GONE);
        }
    }
}
