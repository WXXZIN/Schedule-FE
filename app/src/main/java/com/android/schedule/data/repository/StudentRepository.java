package com.android.schedule.data.repository;

import androidx.annotation.NonNull;

import com.android.schedule.data.api.ApiService;
import com.android.schedule.data.api.RetrofitClient;
import com.android.schedule.data.model.response.StudentResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentRepository {

    private final ApiService apiService;

    public StudentRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void getStudentInfo(String studentNumber, final RepositoryCallback<StudentResponse> callback) {
        apiService.getStudentInfo(studentNumber).enqueue(new Callback<StudentResponse>() {
            @Override
            public void onResponse(@NonNull Call<StudentResponse> call, @NonNull Response<StudentResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("학생 정보 조희 실패: " + (response.body() != null ? response.body().getMessage() : "알 수 없는 오류"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentResponse> call, @NonNull Throwable t) {
                callback.onError("네트워크 오류: " + t.getMessage());
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }
}
