package com.android.schedule.data.model.response;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ScheduleItem implements Serializable {

    private int id;
    private Course course;
    private String sectionNumber;
    private String dayOfWeek;
    private int startTime;
    private int endTime;
    private String buildingName;
    private String roomNumber;

    public ScheduleItem(ScheduleItem other) {
        this.id = other.id;
        this.course = new Course(other.course);
        this.sectionNumber = other.sectionNumber;
        this.dayOfWeek = other.dayOfWeek;
        this.startTime = other.startTime;
        this.endTime = other.endTime;
        this.buildingName = other.buildingName;
        this.roomNumber = other.roomNumber;
    }

    public Course getCourse() {
        return course;
    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public String getDayOfWeek() {
        switch (dayOfWeek) {
            case "Mon":
                return "월요일";
            case "Tue":
                return "화요일";
            case "Wed":
                return "수요일";
            case "Thu":
                return "목요일";
            case "Fri":
                return "금요일";
            default:
                return "알 수 없음";
        }
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        String courseInfo = (course != null) ? course.toString() : "Course Info Not Available";

        return String.format("Course: %s, Section: %s, Day: %s, Time: %d-%d, Building: %s, Room: %s",
                courseInfo,
                sectionNumber,
                getDayOfWeek(),
                startTime,
                endTime,
                buildingName,
                roomNumber);
    }
}
