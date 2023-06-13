package com.android.schedule.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.schedule.R;
import com.android.schedule.ui.viewModel.UserViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JoinActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private EditText inputStudentNumber, inputPassword, inputName;
    private Spinner deptNameSpinner, majorSpinner, gradeSpinner;
    private String studentNumber, password, deptName, major, grade, name;
    private ArrayAdapter<String> deptNameAdapter, majorAdapter;

    private JSONObject deptData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        inputStudentNumber = findViewById(R.id.inputStudentNumber);
        inputPassword = findViewById(R.id.inputPassword);
        inputName = findViewById(R.id.inputName);

        ArrayAdapter<?> gradeAdapter = ArrayAdapter.createFromResource(JoinActivity.this, R.array.grade, android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner = findViewById(R.id.gradeSpinner);
        gradeSpinner.setAdapter(gradeAdapter);

        deptNameSpinner = findViewById(R.id.deptNameSpinner);
        deptNameAdapter = new ArrayAdapter<>(JoinActivity.this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        deptNameSpinner.setAdapter(deptNameAdapter);

        majorSpinner = findViewById(R.id.majorSpinner);
        majorAdapter = new ArrayAdapter<>(JoinActivity.this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        majorSpinner.setAdapter(majorAdapter);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        deptNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDept2 = (String) parent.getItemAtPosition(position);
                updateMajorSpinner(selectedDept2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        findViewById(R.id.btnJoin).setOnClickListener(v -> {
            studentNumber = inputStudentNumber.getText().toString();
            password = inputPassword.getText().toString();
            deptName = deptNameSpinner.getSelectedItem().toString();
            major = majorSpinner.getSelectedItem().toString();

            grade = gradeSpinner.getSelectedItem().toString().replaceAll("\\D", "");
            name = inputName.getText().toString();

            if (studentNumber.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(JoinActivity.this, R.string.error_empty_on_join, Toast.LENGTH_SHORT).show();
            } else {
                if (major.equals("전공 없음")) {
                    userViewModel.join(studentNumber, password, deptName, grade, name);

                } else {
                    userViewModel.join(studentNumber, password, major, grade, name);
                }
            }
        });

        userViewModel.getSuccessMessage().observe(this, successMessage -> {
            if (successMessage != null) {

                Toast.makeText(JoinActivity.this, successMessage, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> userViewModel.setSuccessMessage(null), 2000);

                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                intent.putExtra("studentNumber", studentNumber);
                startActivity(intent);
                finish();
            }
        });

        userViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(JoinActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> userViewModel.setErrorMessage(null), 2000);
            }
        });


        loadDeptData();
    }

    private void loadDeptData() {
        try {
            InputStream inputStream = getAssets().open("dept_data.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            deptData = new JSONObject(stringBuilder.toString());

            JSONArray deptNameArray = deptData.getJSONObject("공과대학").getJSONArray("학과");
            List<String> deptNameList = new ArrayList<>();
            for (int i = 0; i < deptNameArray.length(); i++) {
                deptNameList.add(deptNameArray.getString(i));
            }

            deptNameAdapter.clear();
            deptNameAdapter.addAll(deptNameList);
            deptNameAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }

    private void updateMajorSpinner(String selectedDept2) {
        try {
            JSONArray majorArray = deptData.getJSONObject("공과대학").getJSONArray(selectedDept2);
            List<String> majorList = new ArrayList<>();
            for (int i = 0; i < majorArray.length(); i++) {
                majorList.add(majorArray.getString(i));
            }

            majorAdapter.clear();
            majorAdapter.addAll(majorList);
            majorAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }
}
