package com.android.schedule.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.schedule.data.model.response.CommonResponse;
import com.android.schedule.data.repository.UserRepository;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String newMessage) {
        successMessage.postValue(newMessage);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String newErrorMessage) {
        errorMessage.postValue(newErrorMessage);
    }

    public void login(String studentNumber, String password) {
        userRepository.login(studentNumber, password, new UserRepository.RepositoryCallback<CommonResponse>() {

            @Override
            public void onSuccess(CommonResponse data) {
                if ("success".equals(data.getStatus())) {
                    successMessage.postValue("로그인 성공");
                } else {
                    errorMessage.postValue(data.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void join(String studentNumber, String password, String deptName, String grade, String name) {
        userRepository.join(studentNumber, password, deptName, grade, name, new UserRepository.RepositoryCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse data) {
                if ("success".equals(data.getStatus())) {
                    successMessage.postValue("회원가입 성공");
                } else {
                    errorMessage.postValue(data.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }
}
