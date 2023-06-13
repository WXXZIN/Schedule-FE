package com.android.schedule.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.schedule.R;
import com.android.schedule.data.model.response.ScheduleItem;
import com.android.schedule.ui.adapter.ClassroomScheduleAdapter;
import com.android.schedule.ui.viewModel.ScheduleViewModel;
import com.android.schedule.util.ScheduleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ClassroomScheduleFragment extends Fragment {

    private ScheduleViewModel scheduleViewModel;
    private ExpandableListView expandableListView;
    private TextView resultNone;
    private ClassroomScheduleAdapter adapter;
    private List<String> filteredBuildingNameList = new ArrayList<>();
    private HashMap<String, List<ScheduleItem>> scheduleListMap = new HashMap<>();
    private HashMap<String, List<ScheduleItem>> filteredScheduleListMap = new HashMap<>();

    public ClassroomScheduleFragment() {
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

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_classroom_schedule, container, false);

        EditText searchRoom = rootView.findViewById(R.id.searchRoom);
        expandableListView = rootView.findViewById(R.id.listView);
        resultNone = rootView.findViewById(R.id.resultNone);

        searchRoom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String query = editable.toString().toLowerCase();

                if (scheduleListMap.isEmpty()) {
                    searchRoom.setEnabled(false);
                    resultNone.setText("데이터를 가져오지 못했습니다.");
                    resultNone.setVisibility(View.VISIBLE);
                    expandableListView.setVisibility(View.GONE);
                } else {
                    searchRoom.setEnabled(true);
                    filterSchedule(query);
                }
            }
        });

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> true);

        observeScheduleListData();

        return rootView;
    }

    private void observeScheduleListData() {
        scheduleViewModel.getScheduleListData().observe(getViewLifecycleOwner(), scheduleResponse -> {
            if (scheduleResponse != null) {
                scheduleListMap = ScheduleUtil.convertToHashMapUsingClassroom(scheduleResponse);
                filteredScheduleListMap = new HashMap<>(scheduleListMap);

                List<String> buildingNameList = new ArrayList<>(scheduleListMap.keySet());
                filteredBuildingNameList = new ArrayList<>(buildingNameList);
                Collections.sort(filteredBuildingNameList);

                if (adapter == null) {
                    adapter = new ClassroomScheduleAdapter(getContext(), filteredBuildingNameList, filteredScheduleListMap);
                    expandableListView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

                expandableListView.setVisibility(View.VISIBLE);
                resultNone.setVisibility(View.GONE);
            } else {
                resultNone.setVisibility(View.VISIBLE);
                expandableListView.setVisibility(View.GONE);
            }
        });
    }

    private void filterSchedule(String query) {
        filteredBuildingNameList.clear();
        filteredScheduleListMap.clear();

        for (String buildingRoomKey : scheduleListMap.keySet()) {
            if (buildingRoomKey.toLowerCase().contains(query)) {
                filteredBuildingNameList.add(buildingRoomKey);
                filteredScheduleListMap.put(buildingRoomKey, scheduleListMap.get(buildingRoomKey));
            }
        }

        Collections.sort(filteredBuildingNameList);

        if (filteredScheduleListMap.isEmpty()) {
            resultNone.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
        } else {
            resultNone.setVisibility(View.GONE);
            expandableListView.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }
}
