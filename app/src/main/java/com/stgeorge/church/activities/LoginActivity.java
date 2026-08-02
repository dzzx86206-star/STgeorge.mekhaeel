package com.stgeorge.church.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.stgeorge.church.R;
import com.stgeorge.church.firebase.AuthHelper;
import com.stgeorge.church.helpers.SessionManager;
import com.stgeorge.church.models.User;

import android.widget.TextView;

/**
 * Login screen. Validates input, authenticates against Firebase Auth
 * (via AuthHelper), stores the resulting session, and routes to MainActivity.
 *
 * Screen UI credit: واجهة الشاشة صممها ميخائيل ياسر.
 */
public class LoginActivity extends BaseActivity {

    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private CircularProgressIndicator progressLogin;
    private TextView tvError, tvForgotPassword, tvCreateAccount;

    private AuthHelper authHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authHelper = new AuthHelper();
        sessionManager = new SessionManager(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressLogin = findViewById(R.id.progressLogin);
        tvError = findViewById(R.id.tvError);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
        tvCreateAccount.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String username = etUsername.getText() != null ? etUsername.getText().toString() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (username.trim().isEmpty() || password.isEmpty()) {
            showError(getString(R.string.login_error_empty));
            return;
        }

        setLoading(true);

        authHelper.login(username, password, new AuthHelper.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                setLoading(false);
                sessionManager.saveSession(user);
                goToMain();
            }

            @Override
            public void onFailure(String message) {
                setLoading(false);
                if ("ACCOUNT_DISABLED".equals(message)) {
                    showError(getString(R.string.login_error_disabled));
                } else {
                    showError(getString(R.string.login_error_invalid));
                }
            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void setLoading(boolean loading) {
        progressLogin.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        if (loading) {
            tvError.setVisibility(View.GONE);
        }
    }
}
