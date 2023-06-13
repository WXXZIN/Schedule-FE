package com.android.schedule.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.schedule.R;
import com.android.schedule.data.model.response.ScheduleItem;

import java.util.HashMap;
import java.util.List;

public class ScheduleAdapter extends BaseAdapter {
    private final Context context;
    private final List<HashMap<String, List<ScheduleItem>>> scheduleeMapList;

    public ScheduleAdapter(Context context, List<HashMap<String, List<ScheduleItem>>> scheduleeMapList) {
        this.context = context;
        this.scheduleeMapList = scheduleeMapList;
    }

    @Override
    public int getCount() {
        return scheduleeMapList.size();
    }

    @Override
    public Object getItem(int position) {
        return scheduleeMapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false);
        }

        TextView scheduleTitle = convertView.findViewById(R.id.textScheduleTitle);
        scheduleTitle.setText("시간표 " + (position + 1));

        return convertView;
    }
}
