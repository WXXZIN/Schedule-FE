package com.android.schedule.util;

import com.android.schedule.data.model.response.ScheduleItem;
import com.android.schedule.data.model.response.ScheduleResponse;
import com.android.schedule.data.model.response.ScheduleResponseWithMap;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScheduleUtil {

    public static LinkedHashMap<String, List<ScheduleItem>> convertToHashMapUsingSearch(ScheduleResponse scheduleResponse) {
        LinkedHashMap<String, List<ScheduleItem>> scheduleMap = new LinkedHashMap<>();

        List<ScheduleItem> scheduleItemList = scheduleResponse.getScheduleItemList();

        for (ScheduleItem item : scheduleItemList) {
            String courseName = item.getCourse().getCourseName();
            String sectionNumber = item.getSectionNumber();
            String key = courseName + "-" + sectionNumber;

            if (!scheduleMap.containsKey(key)) {
                scheduleMap.put(key, new ArrayList<>());
            }

            Objects.requireNonNull(scheduleMap.get(key)).add(item);
        }

        return scheduleMap;
    }

    public static HashMap<String, List<ScheduleItem>> convertToHashMapUsingClassroom(ScheduleResponse scheduleResponse) {
        HashMap<String, List<ScheduleItem>> scheduleMap = new HashMap<>();

        List<ScheduleItem> scheduleItemList = scheduleResponse.getScheduleItemList();

        for (ScheduleItem item : scheduleItemList) {
            String buildingName = item.getBuildingName();
            String roomNumber = item.getRoomNumber();
            String key = buildingName + " " + roomNumber;

            if (!scheduleMap.containsKey(key)) {
                scheduleMap.put(key, new ArrayList<>());
            }

            Objects.requireNonNull(scheduleMap.get(key)).add(item);
        }

        return scheduleMap;
    }

    public static List<HashMap<String, List<ScheduleItem>>> convertToListHashMap(ScheduleResponseWithMap scheduleResponseWithMap) {
        List<HashMap<String, List<ScheduleItem>>> scheduleMapList = new ArrayList<>();

        HashMap<String, List<ScheduleItem>> scheduleItemMap = scheduleResponseWithMap.getScheduleItemListWithMap();

        for (Map.Entry<String, List<ScheduleItem>> entry : scheduleItemMap.entrySet()) {
            HashMap<String, List<ScheduleItem>> newMap = new HashMap<>();
            newMap.put(entry.getKey(), entry.getValue());
            scheduleMapList.add(newMap);
        }

        return scheduleMapList;
    }

    public static Schedule convertToSchedule(ScheduleItem scheduleItem) {
        Schedule schedule = new Schedule();
        schedule.setDay(convertToIndex(scheduleItem.getDayOfWeek()));
        schedule.setClassTitle(scheduleItem.getCourse().getCourseName());
        schedule.setClassPlace(convertToClassPlace(scheduleItem.getBuildingName(), scheduleItem.getRoomNumber()));
        schedule.setStartTime(new Time(scheduleItem.getStartTime() + 8, 30));
        schedule.setEndTime(new Time(scheduleItem.getEndTime() + 9, 30));

        return schedule;
    }

    private static int convertToIndex(String dayOfWeek) {
        switch (dayOfWeek) {
            case "월요일":
                return 0;
            case "화요일":
                return 1;
            case "수요일":
                return 2;
            case "목요일":
                return 3;
            case "금요일":
                return 4;
            default:
                return -1;
        }
    }

    private static String convertToClassPlace(String buildingName, String roomNumber) {
        String shortBuildingName;

        switch (buildingName) {
            case "국제관":
                shortBuildingName = "국";
                break;
            case "종합관":
                shortBuildingName = "종";
                break;
            case "공학관":
                shortBuildingName = "공";
                break;
            default:
                shortBuildingName = "알 수 없음";
                break;
        }

        return shortBuildingName + roomNumber;
    }
}
