package com.android.schedule.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.android.schedule.data.model.response.ScheduleItem;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;

public class ClassroomScheduleAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> buildingNameList;
    private final HashMap<String, List<ScheduleItem>> scheduleMap;

    public ClassroomScheduleAdapter(
            Context context,
            List<String> buildingNameList,
            HashMap<String, List<ScheduleItem>> scheduleMap
    ) {
        this.context = context;
        this.buildingNameList = buildingNameList;
        this.scheduleMap = scheduleMap;
    }

    @Override
    public int getGroupCount() {
        return buildingNameList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String groupKey = buildingNameList.get(groupPosition);
        List<ScheduleItem> scheduleItemList = scheduleMap.get(groupKey);

        return scheduleItemList != null ? scheduleItemList.size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return buildingNameList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String groupKey = buildingNameList.get(groupPosition);
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
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
        }

        String groupKey = buildingNameList.get(groupPosition);
        TextView groupTextView = convertView.findViewById(android.R.id.text1);
        groupTextView.setText(groupKey);

        int desiredHeight = convertDpToPixels(64);
        convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, desiredHeight));

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
        }

        ScheduleItem scheduleItem = (ScheduleItem) getChild(groupPosition, childPosition);

        if (scheduleItem != null) {
            TextView childTextView = convertView.findViewById(android.R.id.text1);
            childTextView.setTextSize(convertDpToPixels(5.7));
            childTextView.setText(
                    MessageFormat.format("{0} | {1} {2}교시 - {3}교시",
                            scheduleItem.getDayOfWeek(),
                            scheduleItem.getCourse().getCourseName(),
                            scheduleItem.getStartTime(),
                            scheduleItem.getEndTime()
                    )
            );
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private int convertDpToPixels(double dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
