package com.android.schedule.ui.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.android.schedule.R;
import com.android.schedule.ui.adapter.PageAdapter;
import com.android.schedule.ui.viewModel.StudentViewModel;
import com.android.schedule.ui.viewModel.ScheduleViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private StudentViewModel studentViewModel;
    private ScheduleViewModel scheduleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        setupLayout();

        fetchStudentInfo();
    }

    @Override
    public void onBackPressed() {
        showExitConfirmDialog();
    }

    public void setupLayout() {
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        PageAdapter pageAdapter = new PageAdapter(this);
        viewPager.setAdapter(pageAdapter);
        viewPager.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {}
        ).attach();

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_baseline_home_24_black);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_baseline_calendar_month_24_black);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_baseline_search_24_black);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getIcon() != null)
                    tab.getIcon().setColorFilter(Color.parseColor("#3A69EA"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.ic_baseline_home_24_black);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_baseline_calendar_month_24_black);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.ic_baseline_search_24_black);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void fetchStudentInfo() {
        String studentNumber = getIntent().getStringExtra("studentNumber");

        studentViewModel.getStudentInfo(studentNumber);
        scheduleViewModel.getPersonalSchedule(studentNumber);
        scheduleViewModel.getScheduleList();
    }

    private void showExitConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit_dialog_title)
                .setMessage(R.string.exit_dialog_message)
                .setPositiveButton(
                        R.string.exit_dialog_yes,
                        (dialog, which) -> finish()
                )
                .setNegativeButton(
                        R.string.exit_dialog_no,
                        (dialog, which) -> dialog.dismiss()
                )
                .show();
    }
}
