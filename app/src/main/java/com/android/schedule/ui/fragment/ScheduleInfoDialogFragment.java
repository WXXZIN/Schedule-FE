package com.android.schedule.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.schedule.R;
import com.android.schedule.util.DateUtil;
import com.github.tlaabs.timetableview.Schedule;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScheduleInfoDialogFragment extends BottomSheetDialogFragment {

    private final int idx;
    private final ArrayList<Schedule> scheduleList;

    public ScheduleInfoDialogFragment(int idx, ArrayList<Schedule> scheduleList) {
        this.idx = idx;
        this.scheduleList = scheduleList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_info_dialog, container, false);

        TextView textClassName = view.findViewById(R.id.textClassName);
        TextView textClassDate = view.findViewById(R.id.textClassDate);
        TextView textClassLocation = view.findViewById(R.id.textClassLocation);

        Button btnDelete = view.findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(v -> {
            dismiss();

            FragmentManager fragmentManager = getParentFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();

            for (Fragment fragment : fragments) {
                if (fragment instanceof EditScheduleFragment) {
                    ((EditScheduleFragment) fragment).onBtnDeleteClicked(idx, scheduleList);
                }
            }
        });

        StringBuilder classNames = new StringBuilder();
        StringBuilder classTimes = new StringBuilder();
        StringBuilder classLocations = getStringBuilder(classNames, classTimes);

        textClassName.setText(classNames);
        textClassDate.setText(classTimes);
        textClassLocation.setText(classLocations);

        return view;
    }

    private @NonNull StringBuilder getStringBuilder(StringBuilder classNames, StringBuilder classTimes) {
        StringBuilder classLocations = new StringBuilder();

        Set<String> uniqueClassNames = new HashSet<>();

        for (Schedule schedule : scheduleList) {
            if (!uniqueClassNames.contains(schedule.getClassTitle())) {
                if (classNames.length() > 0) {
                    classNames.append(", ");
                }
                classNames.append(schedule.getClassTitle());
                uniqueClassNames.add(schedule.getClassTitle());
            }

            if (classTimes.length() > 0) {
                classTimes.append(", ");
                classLocations.append(", ");
            }

            classTimes.append(DateUtil.getDay(schedule.getDay()))
                    .append(" ")
                    .append(schedule.getStartTime().getHour())
                    .append(":")
                    .append(schedule.getStartTime().getMinute())
                    .append(" - ")
                    .append(schedule.getEndTime().getHour())
                    .append(":")
                    .append(schedule.getEndTime().getMinute());

            classLocations.append(schedule.getClassPlace());
        }

        return classLocations;
    }
}
