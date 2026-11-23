package com.college.attendance.controller;

import com.college.attendance.dto.Dtos.*;
import com.college.attendance.entity.*;
import com.college.attendance.service.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/student")

public class StudentController {
    private final ProfileService profiles;
    private final CourseService courses;
    private final AttendanceService attendance;

    public StudentController(ProfileService profiles, CourseService courses, AttendanceService attendance) {
        this.profiles = profiles;
        this.courses = courses;
        this.attendance = attendance;
    }

    @GetMapping("/dashboard")
    public StudentDashboard dashboard(@AuthenticationPrincipal User user) {
        StudentProfile s = profiles.studentOf(user);
        List<Summary> sums = attendance.summaryForStudent(s);
        long total = sums.stream().mapToLong(Summary::total).sum();
        long present = sums.stream().mapToLong(Summary::present).sum();
        double pct = total == 0 ? 0 : Math.round(present * 1000.0 / total) / 10.0;
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<TimetableResponse> todays = courses.timetableForStudent(s).stream().filter(t -> t.dayOfWeek() == today).toList();
        return new StudentDashboard(courses.enrolledFor(s).size(), pct, present, todays);
    }

    @GetMapping("/courses")
    public List<CourseResponse> enrolled(@AuthenticationPrincipal User user) { return courses.enrolledFor(profiles.studentOf(user)); }

    @GetMapping("/courses/available")
    public List<CourseResponse> available(@AuthenticationPrincipal User user) { return courses.availableFor(profiles.studentOf(user)); }

    @PostMapping("/courses/{courseId}/enrol")
    public ResponseEntity<Void> enrol(@AuthenticationPrincipal User user, @PathVariable Long courseId) {
        courses.enrol(profiles.studentOf(user).getId(), courseId);
        return ResponseEntity.noContent().build();
    }

    /** A student only ever sees their own records. */
    @GetMapping("/attendance")
    public List<AttendanceRow> attendance(@AuthenticationPrincipal User user,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return attendance.search(courseId, profiles.studentOf(user).getId(), from, to);
    }

    @GetMapping("/attendance/summary")
    public List<Summary> summary(@AuthenticationPrincipal User user) { return attendance.summaryForStudent(profiles.studentOf(user)); }

    @GetMapping("/timetable")
    public List<TimetableResponse> timetable(@AuthenticationPrincipal User user) { return courses.timetableForStudent(profiles.studentOf(user)); }
}
