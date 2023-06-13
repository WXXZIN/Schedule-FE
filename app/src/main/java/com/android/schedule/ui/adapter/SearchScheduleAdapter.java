package com.android.schedule.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.schedule.R;
import com.android.schedule.data.model.response.ScheduleItem;
import com.android.schedule.ui.fragment.EditScheduleFragment;

import java.util.HashMap;
import java.util.List;

public class SearchScheduleAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final EditScheduleFragment fragment;
    private final List<String> courseNameList;
    private final HashMap<String, List<ScheduleItem>> scheduleMap;

    public SearchScheduleAdapter(
            Context context,
            EditScheduleFragment fragment,
            List<String> courseNameList,
            HashMap<String, List<ScheduleItem>> scheduleMap
    ) {
        this.context = context;
        this.fragment = fragment;
        this.courseNameList = courseNameList;
        this.scheduleMap = scheduleMap;
    }


    @Override
    public int getGroupCount() {
        return courseNameList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return courseNameList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String groupKey = courseNameList.get(groupPosition);
        List<ScheduleItem> scheduleItemList = scheduleMap.get(groupKey);

        return scheduleItemList != null ? scheduleItemList.get(childPosition) : null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_item_layout, null);
        }

        String courseName = courseNameList.get(groupPosition);
        List<ScheduleItem> scheduleItemList = scheduleMap.get(courseName);

        if (scheduleItemList != null && !scheduleItemList.isEmpty()) {
            StringBuilder classTimes = new StringBuilder();
            StringBuilder creditsBuilder = getStringBuilder(scheduleItemList, classTimes);

            TextView textClassName = convertView.findViewById(R.id.textClassName);
            TextView textClassDate = convertView.findViewById(R.id.textClassDate);
            TextView textClassTargetAndCredit = convertView.findViewById(R.id.textClassTargetAndCredit);

            textClassName.setText(courseName);
            textClassDate.setText(classTimes);
            textClassTargetAndCredit.setText(creditsBuilder);
        }

        return convertView;
    }

    private static @NonNull StringBuilder getStringBuilder(List<ScheduleItem> scheduleItemList, StringBuilder classTimes) {
        StringBuilder creditsBuilder = new StringBuilder();
        boolean isFirstCredit = true;

        for (ScheduleItem scheduleItem : scheduleItemList) {
            String dayOfWeek = scheduleItem.getDayOfWeek();
            String credits = scheduleItem.getCourse().getCredits() + "학점";

            if (classTimes.length() > 0) {
                classTimes.append(", ");
            }

            classTimes.append(dayOfWeek)
                    .append(" ")
                    .append(scheduleItem.getStartTime() + 8)
                    .append(":")
                    .append(30)
                    .append(" - ")
                    .append(scheduleItem.getEndTime() + 8)
                    .append(":")
                    .append(30);

            if (isFirstCredit) {
                creditsBuilder.append(credits);
                isFirstCredit = false;
            }
        }

        return creditsBuilder;
    }


    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_item_layout, null);
        }

        Button btnAdd = convertView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            if (fragment != null) {
                fragment.onAddScheduleClicked(groupPosition);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
