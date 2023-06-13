package com.android.schedule.data.model.response;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Course implements Serializable {

    private String courseCode;
    private String courseName;
    private int credits;

    public Course(Course other) {
        this.courseCode = other.courseCode;
        this.courseName = other.courseName;
        this.credits = other.credits;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCredits() {
        return credits;
    }

    @NonNull
    @Override
    public String toString() {
        return "Course Code: " + courseCode + ", " +
                "Course Name: " + courseName + ", " +
                "Credits: " + credits;
    }
}
