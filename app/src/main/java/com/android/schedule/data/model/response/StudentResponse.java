package com.android.schedule.data.model.response;

public class StudentResponse extends CommonResponse {

    private Student student;

    public Student getStudent() {
        return student;
    }

    public static class Student {
        private String deptName;
        private String major;
        private String studentNumber;
        private String name;
        private int grade;

        public String getDeptName() {
            return deptName;
        }

        public String getMajor() {
            return major;
        }

        public void setMajor(String major) {
            this.major = major;
        }

        public String getStudentNumber() {
            return studentNumber;
        }

        public String getName() {
            return name;
        }

        public int getGrade() {
            return grade;
        }
    }
}
