package com.college.attendance.entity;

import jakarta.persistence.*;
import java.time.*;

/** Rule 5: one attendance record per student, course and class date. */
@Entity @Table(name = "student_attendance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id", "class_date"}))

public class StudentAttendance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "student_id") private StudentProfile student;
    @ManyToOne(optional = false) @JoinColumn(name = "course_id") private Course course;
    @Column(name = "class_date", nullable = false) private LocalDate classDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private AttendanceStatus status;
    @ManyToOne @JoinColumn(name = "marked_by") private User markedBy;

    public StudentAttendance() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public LocalDate getClassDate() { return classDate; }
    public void setClassDate(LocalDate classDate) { this.classDate = classDate; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    public User getMarkedBy() { return markedBy; }
    public void setMarkedBy(User markedBy) { this.markedBy = markedBy; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private StudentProfile student;
        private Course course;
        private LocalDate classDate;
        private AttendanceStatus status;
        private User markedBy;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder student(StudentProfile student) { this.student = student; return this; }
        public Builder course(Course course) { this.course = course; return this; }
        public Builder classDate(LocalDate classDate) { this.classDate = classDate; return this; }
        public Builder status(AttendanceStatus status) { this.status = status; return this; }
        public Builder markedBy(User markedBy) { this.markedBy = markedBy; return this; }
        public StudentAttendance build() {
            StudentAttendance o = new StudentAttendance();
            o.setId(id);
            o.setStudent(student);
            o.setCourse(course);
            o.setClassDate(classDate);
            o.setStatus(status);
            o.setMarkedBy(markedBy);
            return o;
        }
    }
}
