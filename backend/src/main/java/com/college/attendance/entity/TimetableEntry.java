package com.college.attendance.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity @Table(name = "timetable")

public class TimetableEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "course_id") private Course course;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private DayOfWeek dayOfWeek;
    @Column(nullable = false) private LocalTime startTime;
    @Column(nullable = false) private LocalTime endTime;
    private String room;

    public TimetableEntry() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Course course;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private String room;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder course(Course course) { this.course = course; return this; }
        public Builder dayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; return this; }
        public Builder startTime(LocalTime startTime) { this.startTime = startTime; return this; }
        public Builder endTime(LocalTime endTime) { this.endTime = endTime; return this; }
        public Builder room(String room) { this.room = room; return this; }
        public TimetableEntry build() {
            TimetableEntry o = new TimetableEntry();
            o.setId(id);
            o.setCourse(course);
            o.setDayOfWeek(dayOfWeek);
            o.setStartTime(startTime);
            o.setEndTime(endTime);
            o.setRoom(room);
            return o;
        }
    }
}
