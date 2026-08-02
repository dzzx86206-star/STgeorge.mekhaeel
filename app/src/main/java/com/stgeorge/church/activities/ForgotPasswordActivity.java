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

/** استعادة كلمة المرور — sends a Firebase password-reset email for the given username. */
public class ForgotPasswordActivity extends BaseActivity {

    private TextInputEditText etUsername;
    private MaterialButton btnSendReset;
    private CircularProgressIndicator progressReset;
    private TextView tvMessage;

    private AuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authHelper = new AuthHelper();

        ImageView btnBack = findViewById(R.id.btnBack);
        etUsername = findViewById(R.id.etUsername);
        btnSendReset = findViewById(R.id.btnSendReset);
        progressReset = findViewById(R.id.progressReset);
        tvMessage = findViewById(R.id.tvMessage);

        btnBack.setOnClickListener(v -> finish());
        btnSendReset.setOnClickListener(v -> attemptReset());
    }

    private void attemptReset() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        if (username.isEmpty()) {
            showMessage(getString(R.string.reset_error_empty), true);
            return;
        }

        setLoading(true);
        authHelper.sendPasswordReset(username, new AuthHelper.ResetCallback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                showMessage(getString(R.string.reset_success), false);
            }

            @Override
            public void onFailure(String message) {
                setLoading(false);
                showMessage(getString(R.string.reset_error_generic), true);
            }
        });
    }

    private void showMessage(String message, boolean isError) {
        tvMessage.setText(message);
        tvMessage.setTextColor(isError
                ? getColor(R.color.md_error)
                : getColor(R.color.md_secondary));
        tvMessage.setVisibility(View.VISIBLE);
    }

    private void setLoading(boolean loading) {
        progressReset.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSendReset.setEnabled(!loading);
    }
}
