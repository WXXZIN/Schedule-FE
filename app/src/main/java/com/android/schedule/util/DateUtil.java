package com.android.schedule.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static int getToday() {
        Calendar cal = Calendar.getInstance();

        int day = cal.get(Calendar.DAY_OF_WEEK) - 1;

        if(day > 0 && day < 6) return day;

        return -1;
    }

    public static String getDay(int index) {
        String day;

        switch (index) {
            case 0:
                day = "월";
                break;
            case 1:
                day = "화";
                break;
            case 2:
                day = "수";
                break;
            case 3:
                day = "목";
                break;
            case 4:
                day = "금";
                break;
            case 5:
                day = "토";
                break;
            case 6:
                day = "일";
                break;
            default:
                day = "알 수 없음";
                break;
        }

        return day;
    }

    public static String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일 EEE요일", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String getDayOfWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());
        return sdf.format(new Date()) + "요일";
    }
}
