package com.college.attendance.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity @Table(name = "student_profile")

public class StudentProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false) @JoinColumn(name = "user_id", unique = true) private User user;
    @Column(unique = true) private String rollNo;
    private String department;

    public StudentProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User user;
        private String rollNo;
        private String department;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder rollNo(String rollNo) { this.rollNo = rollNo; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public StudentProfile build() {
            StudentProfile o = new StudentProfile();
            o.setId(id);
            o.setUser(user);
            o.setRollNo(rollNo);
            o.setDepartment(department);
            return o;
        }
    }
}
