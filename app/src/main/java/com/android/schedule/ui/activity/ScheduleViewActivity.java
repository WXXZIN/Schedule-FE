package com.android.schedule.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.schedule.R;
import com.android.schedule.data.model.response.ScheduleItem;
import com.android.schedule.util.ScheduleUtil;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScheduleViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_view);

        Button btnClose = findViewById(R.id.btnClose);
        TimetableView timetableView = findViewById(R.id.timetableView);

        btnClose.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, R.anim.fragment_slide_out_up);
        });

        Intent intent = getIntent();
        HashMap<String, List<ScheduleItem>> selectedSchedule =
                (HashMap<String, List<ScheduleItem>>) intent.getSerializableExtra("selectedSchedule");

        if (selectedSchedule != null) {
            Map<String, ArrayList<Schedule>> courseScheduleListMap = new HashMap<>();

            for (List<ScheduleItem> scheduleItems : selectedSchedule.values()) {
                for (ScheduleItem scheduleItem : scheduleItems) {
                    String courseKey = scheduleItem.getCourse().getCourseCode() + " - " + scheduleItem.getCourse().getCourseName();
                    Schedule schedule = ScheduleUtil.convertToSchedule(scheduleItem);

                    if (!courseScheduleListMap.containsKey(courseKey)) {
                        courseScheduleListMap.put(courseKey, new ArrayList<>());
                    }
                    Objects.requireNonNull(courseScheduleListMap.get(courseKey)).add(schedule);
                }
            }

            for (ArrayList<Schedule> scheduleList : courseScheduleListMap.values()) {
                timetableView.add(scheduleList);
            }
        }
    }

    @Override
    public void onBackPressed() {}
}
