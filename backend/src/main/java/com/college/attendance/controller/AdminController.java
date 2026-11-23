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
@RequestMapping("/api/admin")

public class AdminController {
    private final UserAdminService people;
    private final CourseService courses;
    private final AttendanceService attendance;

    public AdminController(UserAdminService people, CourseService courses, AttendanceService attendance) {
        this.people = people;
        this.courses = courses;
        this.attendance = attendance;
    }

    // ----- dashboard & reports -----
    @GetMapping("/dashboard")
    public AdminStats dashboard() { return attendance.stats(); }

    @GetMapping("/reports/courses")
    public List<Summary> courseReport() { return attendance.courseSummaries(); }

    // ----- students -----
    @GetMapping("/students") public List<PersonResponse> students() { return people.students(); }
    @PostMapping("/students") public PersonResponse createStudent(@Valid @RequestBody StudentRequest r) { return people.createStudent(r); }
    @PutMapping("/students/{id}") public PersonResponse updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest r) { return people.updateStudent(id, r); }
    @PatchMapping("/students/{id}/status")
    public ResponseEntity<Void> studentStatus(@PathVariable Long id, @RequestParam boolean active) {
        people.setStudentActive(id, active);
        return ResponseEntity.noContent().build();
    }

    // ----- faculty -----
    @GetMapping("/faculty") public List<PersonResponse> faculty() { return people.faculty(); }
    @PostMapping("/faculty") public PersonResponse createFaculty(@Valid @RequestBody FacultyRequest r) { return people.createFaculty(r); }
    @PutMapping("/faculty/{id}") public PersonResponse updateFaculty(@PathVariable Long id, @Valid @RequestBody FacultyRequest r) { return people.updateFaculty(id, r); }
    @PatchMapping("/faculty/{id}/status")
    public ResponseEntity<Void> facultyStatus(@PathVariable Long id, @RequestParam boolean active) {
        people.setFacultyActive(id, active);
        return ResponseEntity.noContent().build();
    }

    // ----- courses -----
    @GetMapping("/courses") public List<CourseResponse> courses() { return courses.allCourses(); }
    @PostMapping("/courses") public CourseResponse createCourse(@Valid @RequestBody CourseRequest r) { return courses.create(r); }
    @PutMapping("/courses/{id}") public CourseResponse updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequest r) { return courses.update(id, r); }
    @PatchMapping("/courses/{id}/status")
    public ResponseEntity<Void> courseStatus(@PathVariable Long id, @RequestParam boolean active) {
        courses.setActive(id, active);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/courses/{courseId}/faculty/{facultyId}")
    public CourseResponse assign(@PathVariable Long courseId, @PathVariable Long facultyId) { return courses.assignFaculty(courseId, facultyId); }
    @DeleteMapping("/courses/{courseId}/faculty")
    public ResponseEntity<Void> unassign(@PathVariable Long courseId) {
        courses.unassignFaculty(courseId);
        return ResponseEntity.noContent().build();
    }

    // ----- enrolments -----
    @GetMapping("/enrolments") public List<EnrolmentResponse> enrolments() { return courses.enrolments(); }
    @PostMapping("/enrolments")
    public ResponseEntity<Void> enrol(@Valid @RequestBody EnrolRequest r) {
        courses.enrol(r.studentId(), r.courseId());
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/enrolments")
    public ResponseEntity<Void> drop(@RequestParam Long studentId, @RequestParam Long courseId) {
        courses.drop(studentId, courseId);
        return ResponseEntity.noContent().build();
    }

    // ----- timetable & sessions -----
    @GetMapping("/timetable") public List<TimetableResponse> timetable() { return courses.allTimetable(); }
    @PostMapping("/timetable") public TimetableResponse addTimetable(@Valid @RequestBody TimetableRequest r) { return courses.addTimetable(r); }
    @DeleteMapping("/timetable/{id}")
    public ResponseEntity<Void> deleteTimetable(@PathVariable Long id) {
        courses.deleteTimetable(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/sessions") public List<SessionResponse> sessions() { return courses.sessions(); }
    @PostMapping("/sessions") public SessionResponse addSession(@Valid @RequestBody SessionRequest r) { return courses.addSession(r); }

    // ----- attendance (admin sees everything) -----
    @GetMapping("/attendance/students")
    public List<AttendanceRow> studentAttendance(@RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return attendance.search(courseId, studentId, from, to);
    }

    @GetMapping("/attendance/faculty")
    public List<FacultyAttendanceRow> facultyAttendance(@RequestParam(required = false) Long facultyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return attendance.facultySearch(facultyId, from, to);
    }

    @PostMapping("/attendance/faculty")
    public FacultyAttendanceRow markFaculty(@AuthenticationPrincipal User admin, @Valid @RequestBody FacultyAttendanceRequest r) {
        return attendance.markFaculty(admin, r);
    }
}
