package com.android.schedule.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.schedule.R;
import com.android.schedule.ui.viewModel.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private EditText inputStudentNumber, inputPassword;
    private String studentNumber, password;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                inputStudentNumber.setText("");
                inputPassword.setText("");
                recreate();
            }

        });

        inputStudentNumber = findViewById(R.id.inputStudentNumber);
        inputPassword = findViewById(R.id.inputPassword);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            studentNumber = inputStudentNumber.getText().toString();
            password = inputPassword.getText().toString();

            if (studentNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, R.string.error_empty_on_login, Toast.LENGTH_SHORT).show();
            } else {
                userViewModel.login(studentNumber, password);
            }
        });

        findViewById(R.id.textBtnJoin).setOnClickListener(v -> {
            Intent mIntent = new Intent(getApplicationContext(), JoinActivity.class);
            activityResultLauncher.launch(mIntent);
        });

        userViewModel.getSuccessMessage().observe(this, successMessage -> {
            if (successMessage != null) {
                Toast.makeText(LoginActivity.this, successMessage, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> userViewModel.setSuccessMessage(null), 2000);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("studentNumber", studentNumber);
                startActivity(intent);
                finish();
            }
        });

        userViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> userViewModel.setErrorMessage(null), 2000);
            }
        });
    }

    @Override
    public void onBackPressed() {
        showExitConfirmDialog();
    }

    private void showExitConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit_dialog_title)
                .setMessage(R.string.exit_dialog_message)
                .setPositiveButton(R.string.exit_dialog_yes, (dialog, which) -> finish())
                .setNegativeButton(R.string.exit_dialog_no, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
