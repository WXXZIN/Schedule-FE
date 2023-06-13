package com.android.schedule.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.android.schedule.R;
import com.android.schedule.data.model.response.ScheduleItem;
import com.android.schedule.data.model.response.ScheduleResponseWithMap;
import com.android.schedule.ui.adapter.SearchScheduleAdapter;
import com.android.schedule.ui.viewModel.StudentViewModel;
import com.android.schedule.ui.viewModel.ScheduleViewModel;
import com.android.schedule.util.ScheduleUtil;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EditScheduleFragment extends Fragment {

    private StudentViewModel studentViewModel;
    private ScheduleViewModel scheduleViewModel;
    private TimetableView editTimetableView;
    private ExpandableListView expandableListView;
    private SearchScheduleAdapter adapter;
    private List<String> filteredCourseNameList = new ArrayList<>();
    private HashMap<String, List<ScheduleItem>> scheduleListMap = new HashMap<>();
    private LinkedHashMap<String, List<ScheduleItem>> filteredScheduleListMap = new LinkedHashMap<>();
    private final LinkedHashSet<String> uniqueCourseList = new LinkedHashSet<>();

    private boolean isScheduleModified = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentViewModel = new ViewModelProvider(requireActivity()).get(StudentViewModel.class);
        scheduleViewModel = new ViewModelProvider(requireActivity()).get(ScheduleViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_edit_schedule, container, false);

        Button btnClose = rootView.findViewById(R.id.btnClose);
        Button btnSave = rootView.findViewById(R.id.btnSave);

        editTimetableView = rootView.findViewById(R.id.editTimetableView);
        expandableListView = rootView.findViewById(R.id.listView);

        btnClose.setOnClickListener(v -> {
            if (isScheduleModified) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("변경 사항")
                        .setMessage("변경된 내용이 있습니다. 저장하시겠습니까?")
                        .setPositiveButton("확인", (dialog, which) -> saveSchedule())
                        .setNegativeButton("취소", (dialog, which) -> {
                            requireFragmentManager().popBackStack();
                            resetFragmentVisibility();
                        })
                        .show();
            } else {
                requireFragmentManager().popBackStack();
                resetFragmentVisibility();
            }
        });

        btnSave.setOnClickListener(v -> saveSchedule());

        editTimetableView.setOnStickerSelectEventListener(this::showScheduleInfoDialog);

        expandableListView.setGroupIndicator(null);
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> true);

        return rootView;
    }

    private void resetFragmentVisibility() {
        scheduleViewModel.setTempScheduleData(null);

        FragmentManager fragmentManager = getParentFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();

        for (Fragment fragment : fragments) {
            if (fragment instanceof ScheduleFragment) {
                fragment.requireView().findViewById(R.id.btnEdit).setVisibility(View.VISIBLE);
            }
        }
    }

    private void showAutomateScheduleDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("시간표 자동화")
                .setMessage("개설된 강의를 기반으로 자동으로 시간표를 가져옵니다. 계속 진행하시겠습니까?")
                .setPositiveButton("확인", (dialog, which) -> getGeneratedSchedule())
                .setNegativeButton("취소", null)
                .show();
    }

    private void getGeneratedSchedule() {
        studentViewModel.getStudentData().observe(getViewLifecycleOwner(), studentResponse -> {
            String deptName = studentResponse.getStudent().getDeptName();
            String major = studentResponse.getStudent().getMajor();
            int grade = studentResponse.getStudent().getGrade();

            if (major.equals("전공 없음")) {
                scheduleViewModel.getGeneratedSchedule(deptName, String.valueOf(grade));
            } else {
                scheduleViewModel.getGeneratedSchedule(major, String.valueOf(grade));
            }

            scheduleViewModel.getGeneratedScheduleData().observe(getViewLifecycleOwner(), scheduleResponseWithMap -> {
                if (scheduleResponseWithMap != null) {
                    showSchedulePreviewDialog(scheduleResponseWithMap);
                }
            });
        });
    }

    private void showSchedulePreviewDialog(ScheduleResponseWithMap scheduleResponseWithMap) {
        FragmentManager fragmentManager = getParentFragmentManager();

        SchedulePreviewDialogFragment existingDialog = (SchedulePreviewDialogFragment) fragmentManager.findFragmentByTag("schedulePreviewDialog");

        if (existingDialog == null) {
            SchedulePreviewDialogFragment dialogFragment = new SchedulePreviewDialogFragment(ScheduleUtil.convertToListHashMap(scheduleResponseWithMap));
            dialogFragment.show(fragmentManager, "schedulePreviewDialog");
        } else {
            Log.d("SchedulePreviewFragment", "Dialog is already showing.");
        }
    }

    private void showScheduleInfoDialog(int idx, ArrayList<Schedule> scheduleList) {
        FragmentManager fragmentManager = getParentFragmentManager();

        ScheduleInfoDialogFragment existingDialog = (ScheduleInfoDialogFragment) fragmentManager.findFragmentByTag("scheduleInfoDialog");

        if (existingDialog == null) {
            ScheduleInfoDialogFragment dialogFragment = new ScheduleInfoDialogFragment(idx, scheduleList);
            dialogFragment.show(fragmentManager, "scheduleInfoDialog");
        } else {
            Log.d("ScheduleInfoDialog", "Dialog is already showing.");
        }
    }

    public void onBtnDeleteClicked(int idx, List<Schedule> scheduleList) {
        new AlertDialog.Builder(requireContext())
                .setMessage("수업을 삭제하시겠습니까?")
                .setPositiveButton("확인", (dialog, which) -> {
                    scheduleViewModel.deleteSchedule(scheduleList);
                    isScheduleModified = true;
                    editTimetableView.remove(idx);
                })
                .setNegativeButton("취소", null)
                .show();
    }

    public void observePersonalSchedule() {
        scheduleViewModel.getPersonalScheduleData().observe(getViewLifecycleOwner(), scheduleResponse -> {
            if (scheduleResponse == null || scheduleResponse.getScheduleItemList().isEmpty()) {
                showAutomateScheduleDialog();
            } else {
                Map<String, ArrayList<Schedule>> courseScheduleListMap = new HashMap<>();
                HashMap<String, List<ScheduleItem>> tempSchedule = new HashMap<>();

                tempSchedule.put("1", scheduleResponse.getScheduleItemList());

                scheduleViewModel.setTempScheduleData(tempSchedule);

                for (ScheduleItem scheduleItem : scheduleResponse.getScheduleItemList()) {
                    String courseKey = scheduleItem.getCourse().getCourseCode() + " - " + scheduleItem.getCourse().getCourseName();
                    Schedule schedule = ScheduleUtil.convertToSchedule(scheduleItem);

                    if (!courseScheduleListMap.containsKey(courseKey)) {
                        courseScheduleListMap.put(courseKey, new ArrayList<>());
                    }
                    Objects.requireNonNull(courseScheduleListMap.get(courseKey)).add(schedule);
                }

                for (ArrayList<Schedule> scheduleList : courseScheduleListMap.values()) {
                    editTimetableView.add(scheduleList);
                }
            }
        });
    }

    public void observeScheduleListData() {
        scheduleViewModel.getScheduleListData().observe(getViewLifecycleOwner(), scheduleResponse -> {
            if (scheduleResponse != null) {
                scheduleListMap = ScheduleUtil.convertToHashMapUsingSearch(scheduleResponse);
                filteredScheduleListMap = new LinkedHashMap<>(scheduleListMap);

                List<String> courseNameList = new ArrayList<>(scheduleListMap.keySet());
                filteredCourseNameList = new ArrayList<>(courseNameList);

                if (adapter == null) {
                    adapter = new SearchScheduleAdapter(getContext(), this, filteredCourseNameList, filteredScheduleListMap);
                    expandableListView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

                expandableListView.setVisibility(View.VISIBLE);
            } else {
                expandableListView.setVisibility(View.GONE);
            }
        });
    }

    public void onSchedulePreviewDialogDismissed(HashMap<String, List<ScheduleItem>> selectedSchedule) {
        editTimetableView.removeAll();

        if (selectedSchedule != null) {
            Map<String, ArrayList<Schedule>> courseScheduleListMap = new HashMap<>();

            for (Map.Entry<String, List<ScheduleItem>> entry : selectedSchedule.entrySet()) {
                for (ScheduleItem scheduleItem : entry.getValue()) {
                    String courseKey = scheduleItem.getCourse().getCourseCode() + " - " + scheduleItem.getCourse().getCourseName();

                    if (courseScheduleListMap.containsKey(courseKey)) {
                        Schedule schedule = ScheduleUtil.convertToSchedule(scheduleItem);
                        Objects.requireNonNull(courseScheduleListMap.get(courseKey)).add(schedule);
                    } else {
                        ArrayList<Schedule> scheduleList = new ArrayList<>();
                        Schedule schedule = ScheduleUtil.convertToSchedule(scheduleItem);
                        scheduleList.add(schedule);
                        courseScheduleListMap.put(courseKey, scheduleList);
                    }
                }
            }

            for (ArrayList<Schedule> scheduleList : courseScheduleListMap.values()) {
                editTimetableView.add(scheduleList);
            }

            isScheduleModified = true;
        }
    }

    private void saveSchedule() {
        if (isScheduleModified) {
            studentViewModel.getStudentData().observe(getViewLifecycleOwner(), studentResponse -> {
                String studentNumber = studentResponse.getStudent().getStudentNumber();

                scheduleViewModel.getTempScheduleData().observe(getViewLifecycleOwner(), stringListHashMap -> {
                    if (stringListHashMap == null) {
                        stringListHashMap = new HashMap<>();
                    }

                    for (Map.Entry<String, List<ScheduleItem>> entry : stringListHashMap.entrySet()) {
                        List<ScheduleItem> scheduleItems = entry.getValue();
                        for (ScheduleItem item : scheduleItems) {
                            String uniqueKey = item.getCourse().getCourseCode() + "-" + item.getSectionNumber();
                            uniqueCourseList.add(uniqueKey);
                        }
                    }

                    if (studentNumber != null) {
                        scheduleViewModel.saveSchedule(studentNumber, uniqueCourseList);
                    }
                });
            });

            scheduleViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
                if (successMessage != null) {
                    isScheduleModified = false;
                    scheduleViewModel.setTempScheduleData(null);
                    requireFragmentManager().popBackStack();
                    resetFragmentVisibility();

                    scheduleViewModel.setSuccessMessage(null);
                }
            });

            scheduleViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> scheduleViewModel.setErrorMessage(null), 2000);
                }
            });

        } else {
            requireFragmentManager().popBackStack();
            resetFragmentVisibility();
        }
    }

    public void onAddScheduleClicked(int groupPosition) {
        String courseName = filteredCourseNameList.get(groupPosition);
        List<ScheduleItem> scheduleItemList = filteredScheduleListMap.get(courseName);

        addSchedule(Objects.requireNonNull(scheduleItemList));
    }

    private void addSchedule(List<ScheduleItem> scheduleItemList) {
        String courseCode = scheduleItemList.get(0).getCourse().getCourseCode();
        String sectionNumber = scheduleItemList.get(0).getSectionNumber();
        String newKey = courseCode + "-" + sectionNumber;

        final boolean[] isExecuted = {false};

        scheduleViewModel.getTempScheduleData().observe(getViewLifecycleOwner(), stringListHashMap -> {
            if (isExecuted[0]) return;
            isExecuted[0] = true;

            if (stringListHashMap == null || stringListHashMap.isEmpty()) {
                uniqueCourseList.add(newKey);
                scheduleViewModel.validateSchedule(uniqueCourseList);
            } else {
                boolean courseCodeExists = false;
                boolean sectionExists = false;

                for (Map.Entry<String, List<ScheduleItem>> entry : stringListHashMap.entrySet()) {
                    List<ScheduleItem> scheduleItems = entry.getValue();
                    for (ScheduleItem item : scheduleItems) {
                        String existingCourseCode = item.getCourse().getCourseCode();
                        String existingSectionNumber = item.getSectionNumber();
                        String existingKey = existingCourseCode + "-" + existingSectionNumber;

                        uniqueCourseList.add(existingKey);

                        if (existingCourseCode.equals(courseCode)) {
                            courseCodeExists = true;
                        }

                        if (existingKey.equals(newKey)) {
                            sectionExists = true;
                        }
                    }

                    if (courseCodeExists || sectionExists) break;
                }

                if (sectionExists) {
                    Toast.makeText(getContext(), "이미 시간표에 존재하는 강의입니다.", Toast.LENGTH_SHORT).show();
                } else if (courseCodeExists) {
                    Toast.makeText(getContext(), "이미 수강 중인 과목입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    uniqueCourseList.add(newKey);
                    scheduleViewModel.validateSchedule(uniqueCourseList);
                }
            }
        });

        scheduleViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null) {
                HashMap<String, List<ScheduleItem>> selectedSchedule = new HashMap<>();

                selectedSchedule.put(newKey, scheduleItemList);

                scheduleViewModel.updateTempScheduleData(selectedSchedule);

                Map<String, ArrayList<Schedule>> courseScheduleListMap = new HashMap<>();

                for (ScheduleItem scheduleItem : scheduleItemList) {
                    String courseKey = scheduleItem.getCourse().getCourseName() + " - " + scheduleItem.getSectionNumber();

                    Schedule schedule = ScheduleUtil.convertToSchedule(scheduleItem);

                    if (!courseScheduleListMap.containsKey(courseKey)) {
                        courseScheduleListMap.put(courseKey, new ArrayList<>());
                    }
                    Objects.requireNonNull(courseScheduleListMap.get(courseKey)).add(schedule);
                }

                for (ArrayList<Schedule> scheduleList : courseScheduleListMap.values()) {
                    editTimetableView.add(scheduleList);
                }

                isScheduleModified = true;
                scheduleViewModel.setSuccessMessage(null);
            }
        });

        scheduleViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Iterator<String> iterator = uniqueCourseList.iterator();
                String last = null;

                while (iterator.hasNext()) {
                    last = iterator.next();
                }

                if (last != null) {
                    uniqueCourseList.remove(last);
                }

                scheduleItemList.clear();

                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> scheduleViewModel.setErrorMessage(null), 2000);
            }
        });
    }
}
