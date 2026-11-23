package com.college.attendance.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity @Table(name = "faculty_attendance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"faculty_id", "attendance_date"}))

public class FacultyAttendance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "faculty_id") private FacultyProfile faculty;
    @Column(name = "attendance_date", nullable = false) private LocalDate attendanceDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private AttendanceStatus status;
    @ManyToOne @JoinColumn(name = "marked_by") private User markedBy;

    public FacultyAttendance() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FacultyProfile getFaculty() { return faculty; }
    public void setFaculty(FacultyProfile faculty) { this.faculty = faculty; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    public User getMarkedBy() { return markedBy; }
    public void setMarkedBy(User markedBy) { this.markedBy = markedBy; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private FacultyProfile faculty;
        private LocalDate attendanceDate;
        private AttendanceStatus status;
        private User markedBy;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder faculty(FacultyProfile faculty) { this.faculty = faculty; return this; }
        public Builder attendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; return this; }
        public Builder status(AttendanceStatus status) { this.status = status; return this; }
        public Builder markedBy(User markedBy) { this.markedBy = markedBy; return this; }
        public FacultyAttendance build() {
            FacultyAttendance o = new FacultyAttendance();
            o.setId(id);
            o.setFaculty(faculty);
            o.setAttendanceDate(attendanceDate);
            o.setStatus(status);
            o.setMarkedBy(markedBy);
            return o;
        }
    }
}
