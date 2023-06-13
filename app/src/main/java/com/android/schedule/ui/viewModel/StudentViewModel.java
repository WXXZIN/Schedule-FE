package com.android.schedule.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.schedule.data.model.response.StudentResponse;
import com.android.schedule.data.repository.StudentRepository;

public class StudentViewModel extends ViewModel {

    private final StudentRepository studentRepository;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<StudentResponse> student = new MutableLiveData<>();

    public StudentViewModel() {
        studentRepository = new StudentRepository();
    }

    public LiveData<StudentResponse> getStudentData() {
        return student;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String newErrorMessage) {
        errorMessage.postValue(newErrorMessage);
    }

    public void getStudentInfo(String studentNumber) {
        studentRepository.getStudentInfo(studentNumber, new StudentRepository.RepositoryCallback<StudentResponse>() {
            @Override
            public void onSuccess(StudentResponse data) {
                if ("success".equals(data.getStatus())) {
                    student.postValue(data);
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
