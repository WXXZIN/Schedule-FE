package com.android.schedule.data.repository;

import androidx.annotation.NonNull;

import com.android.schedule.data.api.ApiService;
import com.android.schedule.data.api.RetrofitClient;
import com.android.schedule.data.model.response.CommonResponse;
import com.android.schedule.data.model.response.ScheduleResponse;
import com.android.schedule.data.model.response.ScheduleResponseWithMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleRepository {

    private final ApiService apiService;

    public ScheduleRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void saveSchedule(
            String studentNumber,
            Set<String> uniqueCourseList,
            final UserRepository.RepositoryCallback<CommonResponse> callback
    ) {
        List<String> courseCodeList = new ArrayList<>();
        List<String> sectionNumberList = new ArrayList<>();

        for (String uniqueKey : uniqueCourseList) {
            String[] parts = uniqueKey.split("-");
            if (parts.length == 2) {
                courseCodeList.add(parts[0]);
                sectionNumberList.add(parts[1]);
            }
        }

        apiService.saveSchedule(studentNumber, courseCodeList, sectionNumberList).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommonResponse> call, @NonNull Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("저장 실패:" + (response.body() != null ? response.body().getMessage() : "알 수 없는 오류"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResponse> call, @NonNull Throwable t) {
                callback.onError("네트워크 오류: " + t.getMessage());
            }
        });
    }

    public void getScheduleList(final RepositoryCallback<ScheduleResponse> callback) {
        apiService.getScheduleList().enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScheduleResponse> call, @NonNull Response<ScheduleResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("강의 정보 조회 실패: " + (response.body() != null ? response.body().getMessage() : "알 수 없는 오류"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScheduleResponse> call, @NonNull Throwable t) {
                callback.onError("네트워크 오류: " + t.getMessage());
            }
        });
    }

    public void getPersonalSchedule(String studentNumber, final RepositoryCallback<ScheduleResponse> callback) {
        apiService.getPersonalSchedule(studentNumber).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScheduleResponse> call, @NonNull Response<ScheduleResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("개인 시간표 조희 실패: " + (response.body() != null ? response.body().getMessage() : "알 수 없는 오류"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScheduleResponse> call, @NonNull Throwable t) {
                callback.onError("네트워크 오류: " + t.getMessage());
            }
        });
    }

    public void getGeneratedSchedule(
            String deptName,
            String targetYear,
            final RepositoryCallback<ScheduleResponseWithMap> callback
    ) {
        apiService.getGeneratedSchedule(deptName, targetYear).enqueue(new Callback<ScheduleResponseWithMap>() {
            @Override
            public void onResponse(@NonNull Call<ScheduleResponseWithMap> call, @NonNull Response<ScheduleResponseWithMap> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("시간표 생성 실패: " + (response.body() != null ? response.body().getMessage() : "알 수 없는 오류"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScheduleResponseWithMap> call, @NonNull Throwable t) {
                callback.onError("네트워크 오류: " + t.getMessage());
            }
        });
    }

    public void validateSchedule(Set<String> uniqueCourseList, final RepositoryCallback<CommonResponse> callback) {
        List<String> courseCodeList = new ArrayList<>();
        List<String> sectionNumberList = new ArrayList<>();

        for (String uniqueKey : uniqueCourseList) {
            String[] parts = uniqueKey.split("-");
            if (parts.length == 2) {
                courseCodeList.add(parts[0]);
                sectionNumberList.add(parts[1]);
            }
        }

        apiService.validateSchedule(courseCodeList, sectionNumberList).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommonResponse> call, @NonNull Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("강의 검증 실패");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResponse> call, @NonNull Throwable t) {
                callback.onError("네트워크 오류");
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }
}
