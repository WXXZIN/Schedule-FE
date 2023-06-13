package com.android.schedule.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.schedule.R;
import com.android.schedule.data.model.response.ScheduleItem;
import com.android.schedule.data.model.response.ScheduleResponse;
import com.android.schedule.ui.viewModel.StudentViewModel;
import com.android.schedule.ui.viewModel.ScheduleViewModel;
import com.android.schedule.util.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainFragment extends Fragment {

    private StudentViewModel studentViewModel;
    private ScheduleViewModel scheduleViewModel;
    private FrameLayout loadingLayout;
    private TextView textDeptName, textMajor, textStudentNumber, textName, textDate;
    private LinearLayout todayScheduleLayout;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentViewModel = new ViewModelProvider(requireActivity()).get(StudentViewModel.class);
        scheduleViewModel = new ViewModelProvider(requireActivity()).get(ScheduleViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        loadingLayout = rootView.findViewById(R.id.loadingLayout);
        textDeptName = rootView.findViewById(R.id.textDeptName);
        textMajor = rootView.findViewById(R.id.textMajor);
        textStudentNumber = rootView.findViewById(R.id.textStudentNumber);
        textName = rootView.findViewById(R.id.textName);
        textDate = rootView.findViewById(R.id.textDate);
        todayScheduleLayout = rootView.findViewById(R.id.todayScheduleLayout);

        observeStudentInfoData();
        observeScheduleData();

        return rootView;
    }

    private void observeStudentInfoData() {
        loadingLayout.setVisibility(View.VISIBLE);

        studentViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> studentViewModel.setErrorMessage(null), 2000);
            }
        });

        studentViewModel.getStudentData().observe(getViewLifecycleOwner(), studentResponse -> {
            loadingLayout.setVisibility(View.GONE);

            String deptName = studentResponse.getStudent().getDeptName();
            String major = studentResponse.getStudent().getMajor();
            String studentNumber = studentResponse.getStudent().getStudentNumber();
            String name = studentResponse.getStudent().getName();

            if (deptName.equals(major)) {
                studentResponse.getStudent().setMajor("전공 없음");
                major = "전공 없음";
            }

            textDeptName.setText(String.format("학과 : %s", deptName));
            textMajor.setText(String.format("전공 : %s", major));
            textStudentNumber.setText(String.format("학번 : %s", studentNumber));
            textName.setText(String.format("이름 : %s", name));
            textDate.setText(DateUtil.getFormattedDate());
        });
    }

    private void observeScheduleData() {
        scheduleViewModel.getPersonalScheduleData().observe(getViewLifecycleOwner(), scheduleResponse -> {
            if (scheduleResponse != null) {
                displaySchedule(scheduleResponse);
            } else {
                todayScheduleLayout.removeAllViews();
                TextView noDataText = new TextView(getContext());
                noDataText.setText("시간표를 불러올 수 없습니다.");
                todayScheduleLayout.addView(noDataText);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void displaySchedule(ScheduleResponse scheduleResponse) {
        todayScheduleLayout.removeAllViews();

        String todayDayOfWeek = DateUtil.getDayOfWeek();

        boolean hasScheduleForToday = false;

        List<ScheduleItem> scheduleItemList = scheduleResponse.getScheduleItemList();
        List<ScheduleItem> todayScheduleItemList = new ArrayList<>();

        for (ScheduleItem scheduleItem : scheduleItemList) {
            if (scheduleItem.getDayOfWeek().equals(todayDayOfWeek)) {
                todayScheduleItemList.add(scheduleItem);
            }
        }

        if (!todayScheduleItemList.isEmpty()) {
            hasScheduleForToday = true;

            Collections.sort(todayScheduleItemList, new Comparator<ScheduleItem>() {
                @Override
                public int compare(ScheduleItem o1, ScheduleItem o2) {
                    return Integer.compare(o1.getStartTime(), o2.getStartTime());
                }
            });

            for (ScheduleItem item : todayScheduleItemList) {
                TextView textSchedule = new TextView(getContext());
                textSchedule.setTextSize(18);
                textSchedule.setTypeface(null, Typeface.BOLD);
                textSchedule.setText(String.format("%d:%s - %d:%s %s",
                        item.getStartTime() + 8, "30", item.getEndTime() + 8, "30", item.getCourse().getCourseName()));
                todayScheduleLayout.addView(textSchedule);
            }
        }

        if (!hasScheduleForToday) {
            TextView textNoSchedule = new TextView(getContext());
            textNoSchedule.setTextSize(18);
            textNoSchedule.setTypeface(null, Typeface.BOLD);
            textNoSchedule.setText("오늘의 시간표가 없습니다.");
            todayScheduleLayout.addView(textNoSchedule);
        }
    }
}
