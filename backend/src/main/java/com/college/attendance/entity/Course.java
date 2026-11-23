package com.college.attendance.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity @Table(name = "courses")

public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String courseName;
    @Column(nullable = false, unique = true) private String courseCode;
    private int credits;
     private boolean active = true;
    @ManyToOne @JoinColumn(name = "session_id") private AcademicSession session;

    public Course() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public AcademicSession getSession() { return session; }
    public void setSession(AcademicSession session) { this.session = session; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String courseName;
        private String courseCode;
        private int credits;
        private boolean active = true;
        private AcademicSession session;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder courseName(String courseName) { this.courseName = courseName; return this; }
        public Builder courseCode(String courseCode) { this.courseCode = courseCode; return this; }
        public Builder credits(int credits) { this.credits = credits; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder session(AcademicSession session) { this.session = session; return this; }
        public Course build() {
            Course o = new Course();
            o.setId(id);
            o.setCourseName(courseName);
            o.setCourseCode(courseCode);
            o.setCredits(credits);
            o.setActive(active);
            o.setSession(session);
            return o;
        }
    }
}
