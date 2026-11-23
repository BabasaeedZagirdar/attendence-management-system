package com.college.attendance.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity @Table(name = "faculty_assigned_courses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"faculty_id", "course_id"}))

public class FacultyCourse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "faculty_id") private FacultyProfile faculty;
    @ManyToOne(optional = false) @JoinColumn(name = "course_id") private Course course;

    public FacultyCourse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FacultyProfile getFaculty() { return faculty; }
    public void setFaculty(FacultyProfile faculty) { this.faculty = faculty; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private FacultyProfile faculty;
        private Course course;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder faculty(FacultyProfile faculty) { this.faculty = faculty; return this; }
        public Builder course(Course course) { this.course = course; return this; }
        public FacultyCourse build() {
            FacultyCourse o = new FacultyCourse();
            o.setId(id);
            o.setFaculty(faculty);
            o.setCourse(course);
            return o;
        }
    }
}
