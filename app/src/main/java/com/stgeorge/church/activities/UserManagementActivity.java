package com.stgeorge.church.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.stgeorge.church.R;
import com.stgeorge.church.adapters.ServantAdapter;
import com.stgeorge.church.firebase.AdminUserRepository;
import com.stgeorge.church.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * إدارة الخدام — manager only, add and delete only (by design — see
 * AdminUserRepository's javadoc for the full security model and why
 * there's no in-place password edit or Auth-account edit here).
 */
public class UserManagementActivity extends BaseActivity {

    private final AdminUserRepository adminUserRepository = new AdminUserRepository(this);
    private final List<User> servants = new ArrayList<>();
    private ServantAdapter adapter;

    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmpty;
    private ProgressBar progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rvServants = findViewById(R.id.rvServants);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressLoading = findViewById(R.id.progressLoading);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        rvServants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ServantAdapter(servants, this::confirmDeleteServant);
        rvServants.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadServants);
        fabAdd.setOnClickListener(v -> showAddServantDialog());

        loadServants();
    }

    private void loadServants() {
        swipeRefresh.setRefreshing(true);
        progressLoading.setVisibility(servants.isEmpty() ? View.VISIBLE : View.GONE);

        adminUserRepository.getAllServants(new AdminUserRepository.ListCallback() {
            @Override
            public void onSuccess(List<User> result) {
                swipeRefresh.setRefreshing(false);
                progressLoading.setVisibility(View.GONE);
                servants.clear();
                servants.addAll(result);
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(servants.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(String message) {
                swipeRefresh.setRefreshing(false);
                progressLoading.setVisibility(View.GONE);
                Toast.makeText(UserManagementActivity.this, R.string.generic_load_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddServantDialog() {
        View formView = getLayoutInflater().inflate(R.layout.dialog_servant_form, null);
        TextInputEditText etFullName = formView.findViewById(R.id.etFullName);
        TextInputEditText etUsername = formView.findViewById(R.id.etUsername);
        TextInputEditText etPassword = formView.findViewById(R.id.etPassword);
        TextInputEditText etPhone = formView.findViewById(R.id.etPhone);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.servant_add_title)
                .setView(formView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String fullName = textOf(etFullName);
                    String username = textOf(etUsername);
                    String password = textOf(etPassword);
                    String phone = textOf(etPhone);

                    if (fullName.isEmpty() || username.isEmpty() || password.length() < 6) {
                        Toast.makeText(this, R.string.servant_error_invalid, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    adminUserRepository.createServant(username, password, fullName, phone,
                            new AdminUserRepository.WriteCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(UserManagementActivity.this,
                                            R.string.servant_created, Toast.LENGTH_SHORT).show();
                                    loadServants();
                                }

                                @Override
                                public void onFailure(String message) {
                                    int errorRes = "USERNAME_TAKEN".equals(message)
                                            ? R.string.servant_error_username_taken
                                            : R.string.servant_error_create;
                                    Toast.makeText(UserManagementActivity.this, errorRes, Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private String textOf(TextInputEditText field) {
        return field.getText() != null ? field.getText().toString().trim() : "";
    }

    private void confirmDeleteServant(User servant) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.servant_delete_confirm_title)
                .setMessage(getString(R.string.servant_delete_confirm_message, servant.getFullName()))
                .setPositiveButton(R.string.delete, (dialog, which) ->
                        adminUserRepository.deleteServant(servant.getUserId(), new AdminUserRepository.WriteCallback() {
                            @Override
                            public void onSuccess() {
                                loadServants();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(UserManagementActivity.this,
                                        R.string.generic_delete_error, Toast.LENGTH_SHORT).show();
                            }
                        }))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
