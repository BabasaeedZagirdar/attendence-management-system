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
@RequestMapping("/api/faculty")

public class FacultyController {
    private final ProfileService profiles;
    private final CourseService courses;
    private final AttendanceService attendance;

    public FacultyController(ProfileService profiles, CourseService courses, AttendanceService attendance) {
        this.profiles = profiles;
        this.courses = courses;
        this.attendance = attendance;
    }

    @GetMapping("/dashboard")
    public FacultyDashboard dashboard(@AuthenticationPrincipal User user) {
        FacultyProfile f = profiles.facultyOf(user);
        List<CourseResponse> mine = courses.coursesOfFaculty(f);
        long students = mine.stream().mapToLong(c -> courses.studentsOfCourse(c.id(), f).size()).sum();
        String today = attendance.facultyToday(f.getId()).map(a -> a.getStatus().name()).orElse(
                AttendanceService.isHoliday(LocalDate.now()) ? "HOLIDAY" : "NOT_MARKED");
        DayOfWeek dow = LocalDate.now().getDayOfWeek();
        List<TimetableResponse> todays = courses.timetableForFaculty(f).stream().filter(t -> t.dayOfWeek() == dow).toList();
        return new FacultyDashboard(mine.size(), students, today, todays);
    }

    @GetMapping("/courses")
    public List<CourseResponse> myCourses(@AuthenticationPrincipal User user) { return courses.coursesOfFaculty(profiles.facultyOf(user)); }

    @GetMapping("/courses/{courseId}/students")
    public List<PersonResponse> students(@AuthenticationPrincipal User user, @PathVariable Long courseId) {
        return courses.studentsOfCourse(courseId, profiles.facultyOf(user));
    }

    // ----- mark / view students' attendance -----
    @GetMapping("/attendance")
    public List<AttendanceRow> attendanceOn(@AuthenticationPrincipal User user, @RequestParam Long courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendance.forCourseOnDate(profiles.facultyOf(user), courseId, date);
    }

    @PostMapping("/attendance")
    public Map<String, Object> mark(@AuthenticationPrincipal User user, @Valid @RequestBody MarkAttendanceRequest r) {
        int n = attendance.markStudents(profiles.facultyOf(user), r);
        return Map.of("message", "Attendance saved for " + n + " student(s)", "count", n);
    }

    @GetMapping("/attendance/history")
    public List<AttendanceRow> history(@AuthenticationPrincipal User user, @RequestParam Long courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return attendance.searchForFaculty(profiles.facultyOf(user), courseId, from, to);
    }

    @GetMapping("/reports")
    public List<Summary> report(@AuthenticationPrincipal User user, @RequestParam Long courseId) {
        return attendance.summaryForCourse(profiles.facultyOf(user), courseId);
    }

    // ----- own daily attendance -----
    @PostMapping("/my-attendance")
    public FacultyAttendanceRow markSelf(@AuthenticationPrincipal User user, @Valid @RequestBody FacultyAttendanceRequest r) {
        return attendance.markFaculty(user, r);
    }

    @GetMapping("/my-attendance")
    public List<FacultyAttendanceRow> mine(@AuthenticationPrincipal User user) {
        return attendance.facultySearch(profiles.facultyOf(user).getId(), null, null);
    }

    @GetMapping("/timetable")
    public List<TimetableResponse> timetable(@AuthenticationPrincipal User user) { return courses.timetableForFaculty(profiles.facultyOf(user)); }
}
