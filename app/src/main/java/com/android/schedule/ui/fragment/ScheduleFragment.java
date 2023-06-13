package com.android.schedule.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.android.schedule.R;
import com.android.schedule.data.model.response.ScheduleItem;
import com.android.schedule.ui.viewModel.ScheduleViewModel;
import com.android.schedule.util.DateUtil;
import com.android.schedule.util.ScheduleUtil;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScheduleFragment extends Fragment {

    private ScheduleViewModel scheduleViewModel;
    private Button btnEdit;
    private TimetableView timetableView;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleViewModel = new ViewModelProvider(requireActivity()).get(ScheduleViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        btnEdit = rootView.findViewById(R.id.btnEdit);
        timetableView = rootView.findViewById(R.id.timetableView);

        btnEdit.setOnClickListener(this::onClick);
        timetableView.setHeaderHighlight(DateUtil.getToday());

        observePersonalSchedule();

        return rootView;
    }

    private void observePersonalSchedule() {
        scheduleViewModel.getPersonalScheduleData().observe(getViewLifecycleOwner(), scheduleResponse -> {
            timetableView.removeAll();

            if (scheduleResponse != null) {
                Map<String, ArrayList<Schedule>> courseScheduleListMap = new HashMap<>();

                for (ScheduleItem scheduleItem : scheduleResponse.getScheduleItemList()) {
                    String courseKey = scheduleItem.getCourse().getCourseCode() + " - " + scheduleItem.getCourse().getCourseName();
                    Schedule schedule = ScheduleUtil.convertToSchedule(scheduleItem);

                    if (!courseScheduleListMap.containsKey(courseKey)) {
                        courseScheduleListMap.put(courseKey, new ArrayList<>());
                    }
                    Objects.requireNonNull(courseScheduleListMap.get(courseKey)).add(schedule);
                }

                for (ArrayList<Schedule> scheduleList : courseScheduleListMap.values()) {
                    timetableView.add(scheduleList);
                }
            }
        });
    }

    private void onClick(View v) {
       showEditScheduleLayout();
    }

    private void showEditScheduleLayout() {
        btnEdit.setVisibility(View.GONE);

        FragmentManager fragmentManager = getParentFragmentManager();

        EditScheduleFragment existingDialog = (EditScheduleFragment) fragmentManager.findFragmentByTag("editSchedule");

        if (existingDialog == null) {
            EditScheduleFragment editScheduleFragment = new EditScheduleFragment();

            FragmentTransaction transaction = requireFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.fragment_slide_in_up,
                    R.anim.fragment_slide_out_down,
                    R.anim.fragment_slide_in_down,
                    R.anim.fragment_slide_out_up
            );

            transaction.replace(R.id.relativeLayout, editScheduleFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            new Handler().postDelayed(() -> {
                editScheduleFragment.observeScheduleListData();
                editScheduleFragment.observePersonalSchedule();
            }, 250);
        } else {
            Log.d("EditScheduleFragment", "Dialog is already showing.");
        }
    }
}
