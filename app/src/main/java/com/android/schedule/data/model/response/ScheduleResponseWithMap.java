package com.android.schedule.data.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class ScheduleResponseWithMap extends CommonResponse {

    @SerializedName("scheduleMap")
    private HashMap<String, List<ScheduleItem>> scheduleItemListWithMap;

    public HashMap<String, List<ScheduleItem>> getScheduleItemListWithMap() {
        return scheduleItemListWithMap;
    }
}
