package com.college.attendance.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity @Table(name = "student_assigned_courses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))

public class StudentCourse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "student_id") private StudentProfile student;
    @ManyToOne(optional = false) @JoinColumn(name = "course_id") private Course course;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private EnrolmentStatus status = EnrolmentStatus.ENROLLED;

    public StudentCourse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public EnrolmentStatus getStatus() { return status; }
    public void setStatus(EnrolmentStatus status) { this.status = status; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private StudentProfile student;
        private Course course;
        private EnrolmentStatus status = EnrolmentStatus.ENROLLED;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder student(StudentProfile student) { this.student = student; return this; }
        public Builder course(Course course) { this.course = course; return this; }
        public Builder status(EnrolmentStatus status) { this.status = status; return this; }
        public StudentCourse build() {
            StudentCourse o = new StudentCourse();
            o.setId(id);
            o.setStudent(student);
            o.setCourse(course);
            o.setStatus(status);
            return o;
        }
    }
}
