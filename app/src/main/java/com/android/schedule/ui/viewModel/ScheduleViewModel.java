package com.android.schedule.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.schedule.data.model.response.CommonResponse;
import com.android.schedule.data.model.response.ScheduleItem;
import com.android.schedule.data.model.response.ScheduleResponse;
import com.android.schedule.data.model.response.ScheduleResponseWithMap;
import com.android.schedule.data.repository.ScheduleRepository;
import com.android.schedule.data.repository.UserRepository;
import com.github.tlaabs.timetableview.Schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ScheduleViewModel extends ViewModel {

    private final ScheduleRepository scheduleRepository;
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<ScheduleResponse> scheduleList = new MutableLiveData<>();
    private final MutableLiveData<ScheduleResponse> personalSchedule = new MutableLiveData<>();
    private final MutableLiveData<ScheduleResponseWithMap> generatedSchedule = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String, List<ScheduleItem>>> tempSchedule = new MutableLiveData<>(new HashMap<>());

    public ScheduleViewModel() {
        scheduleRepository = new ScheduleRepository();
    }

    public MutableLiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String newSuccessMessage) {
        successMessage.postValue(newSuccessMessage);
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String newErrorMessage) {
        errorMessage.postValue(newErrorMessage);
    }

    public LiveData<ScheduleResponse> getScheduleListData() {
        return scheduleList;
    }

    public LiveData<ScheduleResponse> getPersonalScheduleData() {
        return personalSchedule;
    }

    public LiveData<ScheduleResponseWithMap> getGeneratedScheduleData() {
        return generatedSchedule;
    }

    public LiveData<HashMap<String, List<ScheduleItem>>> getTempScheduleData() {
        return tempSchedule;
    }

    public void setTempScheduleData(HashMap<String, List<ScheduleItem>> selectedSchedule) {
        tempSchedule.postValue(selectedSchedule);
    }

    public void updateTempScheduleData(HashMap<String, List<ScheduleItem>> selectedSchedule) {
        HashMap<String, List<ScheduleItem>> currentSchedule = tempSchedule.getValue();

        if (currentSchedule == null) {
            currentSchedule = new HashMap<>();
        }

        for (Map.Entry<String, List<ScheduleItem>> entry : selectedSchedule.entrySet()) {
            List<ScheduleItem> newScheduleItems = entry.getValue();

            if (currentSchedule.get("1") == null) {
                currentSchedule.put("1", new ArrayList<>());
            }

            Objects.requireNonNull(currentSchedule.get("1")).addAll(newScheduleItems);
        }

        tempSchedule.postValue(currentSchedule);
    }

    public void deleteSchedule(List<Schedule> scheduleList) {
        Schedule schedule = scheduleList.get(0);
        String courseName = schedule.getClassTitle();

        if (tempSchedule.getValue() != null) {
            HashMap<String, List<ScheduleItem>> tempData = tempSchedule.getValue();

            HashMap<String, List<ScheduleItem>> tempDataCopy = new HashMap<>();

            for (Map.Entry<String, List<ScheduleItem>> entry : tempData.entrySet()) {
                List<ScheduleItem> copiedList = new ArrayList<>();
                for (ScheduleItem item : entry.getValue()) {
                    copiedList.add(new ScheduleItem(item));
                }
                tempDataCopy.put(entry.getKey(), copiedList);
            }

            for (Map.Entry<String, List<ScheduleItem>> entry : tempDataCopy.entrySet()) {
                List<ScheduleItem> scheduleItemList = entry.getValue();

                Iterator<ScheduleItem> itemIterator = scheduleItemList.iterator();
                while (itemIterator.hasNext()) {
                    ScheduleItem scheduleItem = itemIterator.next();

                    if (scheduleItem.getCourse().getCourseName().equals(courseName)) {
                        itemIterator.remove();
                    }
                }

                tempDataCopy.put(entry.getKey(), scheduleItemList);
            }

            tempSchedule.postValue(tempDataCopy);
        }
    }

    public void saveSchedule(String studentNumber, Set<String> uniqueCourseList) {
        scheduleRepository.saveSchedule(studentNumber, uniqueCourseList, new UserRepository.RepositoryCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse data) {
                if ("success".equals(data.getStatus())) {
                    successMessage.postValue("");
                    getPersonalSchedule(studentNumber);
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

    public void validateSchedule(Set<String> uniqueCourseList) {
        scheduleRepository.validateSchedule(uniqueCourseList, new ScheduleRepository.RepositoryCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse data) {
                if ("success".equals(data.getStatus())) {
                    successMessage.postValue("");
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

    public void getScheduleList() {
        scheduleRepository.getScheduleList(new ScheduleRepository.RepositoryCallback<ScheduleResponse>() {
            @Override
            public void onSuccess(ScheduleResponse data) {
                if ("success".equals(data.getStatus())) {
                    scheduleList.postValue(data);
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

    public void getPersonalSchedule(String studentNumber) {
        scheduleRepository.getPersonalSchedule(studentNumber, new ScheduleRepository.RepositoryCallback<ScheduleResponse>() {
            @Override
            public void onSuccess(ScheduleResponse data) {
                if ("success".equals(data.getStatus())) {
                    personalSchedule.postValue(data);
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

    public void getGeneratedSchedule(String deptName, String targetYear) {
        scheduleRepository.getGeneratedSchedule(deptName, targetYear, new ScheduleRepository.RepositoryCallback<ScheduleResponseWithMap>() {
            @Override
            public void onSuccess(ScheduleResponseWithMap data) {
                if ("success".equals(data.getStatus())) {
                    generatedSchedule.postValue(data);
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
