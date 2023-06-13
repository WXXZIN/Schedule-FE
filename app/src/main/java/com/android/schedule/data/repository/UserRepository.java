package com.android.schedule.data.repository;

import androidx.annotation.NonNull;

import com.android.schedule.data.api.ApiService;
import com.android.schedule.data.api.RetrofitClient;
import com.android.schedule.data.model.response.CommonResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final ApiService apiService;

    public UserRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void login(String studentNumber, String password, final RepositoryCallback<CommonResponse> callback) {
        apiService.login(studentNumber, password).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommonResponse> call, @NonNull Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("로그인 실패: " + (response.body() != null ? response.body().getMessage() : "알 수 없는 오류"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResponse> call, @NonNull Throwable t) {
                callback.onError("네트워크 오류: " + t.getMessage());
            }
        });
    }

    public void join(
            String studentNumber,
            String password,
            String deptName,
            String grade,
            String name,
            final RepositoryCallback<CommonResponse> callback
    ) {
        apiService.join(studentNumber, password, deptName, grade, name).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommonResponse> call, @NonNull Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("회원가입 실패: " + (response.body() != null ? response.body().getMessage() : "알 수 없는 오류"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResponse> call, @NonNull Throwable t) {
                callback.onError("네트워크 오류: " + t.getMessage());
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }
}
