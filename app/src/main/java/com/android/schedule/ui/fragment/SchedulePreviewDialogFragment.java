package com.android.schedule.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.android.schedule.R;
import com.android.schedule.data.model.response.ScheduleItem;
import com.android.schedule.ui.activity.ScheduleViewActivity;
import com.android.schedule.ui.adapter.ScheduleAdapter;
import com.android.schedule.ui.viewModel.ScheduleViewModel;

import java.util.HashMap;
import java.util.List;

public class SchedulePreviewDialogFragment extends DialogFragment {

    private ScheduleViewModel scheduleViewModel;
    private final List<HashMap<String, List<ScheduleItem>>> generatedScheduleMapList;
    private HashMap<String, List<ScheduleItem>> selectedSchedule;

    public SchedulePreviewDialogFragment(List<HashMap<String, List<ScheduleItem>>> generatedScheduleMapList) {
        this.generatedScheduleMapList = generatedScheduleMapList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleViewModel = new ViewModelProvider(requireActivity()).get(ScheduleViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_preview_dialog, container, false);

        setCancelable(false);

        GridView gridView = view.findViewById(R.id.gridView);
        Button btnView = view.findViewById(R.id.btnView);
        Button btnSelect = view.findViewById(R.id.btnSelect);

        ScheduleAdapter adapter = new ScheduleAdapter(getContext(), generatedScheduleMapList);
        gridView.setAdapter(adapter);

        gridView.setNumColumns(2);

        gridView.setOnItemClickListener((parent, v, position, id) -> {
            selectedSchedule = generatedScheduleMapList.get(position);

            for (int i = 0; i < gridView.getChildCount(); i++) {
                View itemView = gridView.getChildAt(i);
                itemView.setBackgroundColor(Color.TRANSPARENT);

                TextView textView = itemView.findViewById(R.id.textScheduleTitle);
                textView.setTextColor(Color.BLACK);
            }

            v.setBackgroundColor(Color.parseColor("#74a4f3"));

            TextView textView = v.findViewById(R.id.textScheduleTitle);
            textView.setTextColor(Color.WHITE);
        });

        btnView.setOnClickListener(v -> {
            if (selectedSchedule != null) {
                showSchedule(selectedSchedule);
            } else {
                Toast.makeText(getContext(), "Please select a schedule", Toast.LENGTH_SHORT).show();
            }
        });

        btnSelect.setOnClickListener(v -> {
            if (selectedSchedule != null) {
                selectSchedule(selectedSchedule);
                dismiss();

                FragmentManager fragmentManager = getParentFragmentManager();
                List<Fragment> fragments = fragmentManager.getFragments();

                for (Fragment fragment : fragments) {
                    if (fragment instanceof EditScheduleFragment) {
                        ((EditScheduleFragment) fragment).onSchedulePreviewDialogDismissed(selectedSchedule);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Please select a schedule", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showSchedule(HashMap<String, List<ScheduleItem>> selectedSchedule) {
        Intent intent = new Intent(getActivity(), ScheduleViewActivity.class);
        intent.putExtra("selectedSchedule", selectedSchedule);
        startActivity(intent);

        requireActivity().overridePendingTransition(R.anim.fragment_slide_in_up, 0);
    }

    private void selectSchedule(HashMap<String, List<ScheduleItem>> selectedSchedule) {
        scheduleViewModel.setTempScheduleData(selectedSchedule);
    }
}
