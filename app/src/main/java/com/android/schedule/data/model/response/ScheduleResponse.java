package com.android.schedule.data.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScheduleResponse extends CommonResponse {

    @SerializedName("scheduleList")
    private List<ScheduleItem> scheduleItemList;

    public List<ScheduleItem> getScheduleItemList() {
        return scheduleItemList;
    }
}
