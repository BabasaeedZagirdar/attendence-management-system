package com.college.attendance.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity @Table(name = "faculty_profile")

public class FacultyProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false) @JoinColumn(name = "user_id", unique = true) private User user;
    @Column(unique = true) private String employeeId;
    private String department;

    public FacultyProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User user;
        private String employeeId;
        private String department;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public FacultyProfile build() {
            FacultyProfile o = new FacultyProfile();
            o.setId(id);
            o.setUser(user);
            o.setEmployeeId(employeeId);
            o.setDepartment(department);
            return o;
        }
    }
}
