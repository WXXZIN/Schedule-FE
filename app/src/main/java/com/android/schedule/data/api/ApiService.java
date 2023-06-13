package com.android.schedule.data.api;

import com.android.schedule.data.model.response.CommonResponse;
import com.android.schedule.data.model.response.StudentResponse;
import com.android.schedule.data.model.response.ScheduleResponse;
import com.android.schedule.data.model.response.ScheduleResponseWithMap;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("login")
    Call<CommonResponse> login(@Field("studentNumber") String studentNumber, @Field("password") String password);

    @FormUrlEncoded
    @POST("join")
    Call<CommonResponse> join(
            @Field("studentNumber") String studentNumber,
            @Field("password") String password,
            @Field("deptName") String deptName,
            @Field("grade") String grade,
            @Field("name") String name
    );

    @GET("student/info")
    Call<StudentResponse> getStudentInfo(@Query("studentNumber") String studentNumber);

    @FormUrlEncoded
    @POST("schedule/save")
    Call<CommonResponse> saveSchedule(
            @Field("studentNumber") String studentNumber,
            @Field("courseCode") List<String> courseCodeList,
            @Field("sectionNumber") List<String> sectionNumberList
    );

    @GET("schedule")
    Call<ScheduleResponse> getScheduleList();

    @GET("schedule/personal")
    Call<ScheduleResponse> getPersonalSchedule(@Query("studentNumber") String studentNumber);

    @GET("schedule/generate")
    Call<ScheduleResponseWithMap> getGeneratedSchedule(
            @Query("deptName") String deptName,
            @Query("targetYear") String targetYear
    );

    @GET("schedule/validate")
    Call<CommonResponse> validateSchedule(
            @Query("courseCode") List<String> courseCodeList,
            @Query("sectionNumber") List<String> sectionNumberList
    );
}
