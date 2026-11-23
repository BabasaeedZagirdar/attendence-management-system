package com.college.attendance.service;

import com.college.attendance.dto.Dtos.*;
import com.college.attendance.entity.*;
import com.college.attendance.exception.ApiException;
import com.college.attendance.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/**
 * Attendance business rules
 *  1. Saturday/Sunday are automatically holidays.
 *  2. Attendance cannot be marked on holidays (or in the future).
 *  3. A faculty member can mark attendance only for their assigned courses.
 *  4. A student can have attendance only for an enrolled course.
 *  5. One record per student + course + class date (re-marking updates the record).
 *  6. Admin can view all attendance reports.
 */
@Service

public class AttendanceService {
    private final StudentAttendanceRepository studentAtt;
    private final FacultyAttendanceRepository facultyAtt;
    private final CourseRepository courses;
    private final StudentProfileRepository students;
    private final FacultyProfileRepository faculty;
    private final StudentCourseRepository enrolments;
    private final FacultyCourseRepository assignments;

    public AttendanceService(StudentAttendanceRepository studentAtt, FacultyAttendanceRepository facultyAtt, CourseRepository courses, StudentProfileRepository students, FacultyProfileRepository faculty, StudentCourseRepository enrolments, FacultyCourseRepository assignments) {
        this.studentAtt = studentAtt;
        this.facultyAtt = facultyAtt;
        this.courses = courses;
        this.students = students;
        this.faculty = faculty;
        this.enrolments = enrolments;
        this.assignments = assignments;
    }

    public static boolean isHoliday(LocalDate d) {
        DayOfWeek w = d.getDayOfWeek();
        return w == DayOfWeek.SATURDAY || w == DayOfWeek.SUNDAY;
    }

    private void checkDate(LocalDate d) {
        if (d == null) throw ApiException.bad("Date is required");
        if (isHoliday(d)) throw ApiException.bad("Attendance cannot be marked on holidays (Saturday/Sunday)");
        if (d.isAfter(LocalDate.now())) throw ApiException.bad("Attendance cannot be marked for a future date");
    }

    private void checkStatus(AttendanceStatus s) {
        if (s == null || s == AttendanceStatus.HOLIDAY)
            throw ApiException.bad("Status must be PRESENT, ABSENT or LEAVE (holidays are automatic)");
    }

    // ---------- students' attendance ----------
    @Transactional
    public int markStudents(FacultyProfile f, MarkAttendanceRequest r) {
        checkDate(r.date());
        Course course = courses.findById(r.courseId()).orElseThrow(() -> ApiException.notFound("Course"));
        if (!assignments.existsByFacultyIdAndCourseId(f.getId(), course.getId()))
            throw ApiException.forbidden("You can mark attendance only for your assigned courses");
        User actor = f.getUser();
        int n = 0;
        for (AttendanceEntry rec : r.records()) {
            checkStatus(rec.status());
            StudentProfile sp = students.findById(rec.studentId()).orElseThrow(() -> ApiException.notFound("Student"));
            if (!enrolments.existsByStudentIdAndCourseIdAndStatus(sp.getId(), course.getId(), EnrolmentStatus.ENROLLED))
                throw ApiException.bad(sp.getUser().getName() + " is not enrolled in " + course.getCourseCode());
            StudentAttendance a = studentAtt
                    .findByStudentIdAndCourseIdAndClassDate(sp.getId(), course.getId(), r.date())
                    .orElseGet(() -> StudentAttendance.builder().student(sp).course(course).classDate(r.date()).build());
            a.setStatus(rec.status());
            a.setMarkedBy(actor);
            studentAtt.save(a);
            n++;
        }
        return n;
    }

    /** Existing records of one course on one date (to pre-fill the faculty marking sheet). */
    public List<AttendanceRow> forCourseOnDate(FacultyProfile f, Long courseId, LocalDate date) {
        if (!assignments.existsByFacultyIdAndCourseId(f.getId(), courseId))
            throw ApiException.forbidden("You are not assigned to this course");
        return studentAtt.findByCourseIdAndClassDate(courseId, date).stream().map(this::row).toList();
    }

    public List<AttendanceRow> search(Long courseId, Long studentId, LocalDate from, LocalDate to) {
        return studentAtt.search(courseId, studentId, from, to).stream().map(this::row).toList();
    }

    public List<AttendanceRow> searchForFaculty(FacultyProfile f, Long courseId, LocalDate from, LocalDate to) {
        if (!assignments.existsByFacultyIdAndCourseId(f.getId(), courseId))
            throw ApiException.forbidden("You are not assigned to this course");
        return search(courseId, null, from, to);
    }

    private AttendanceRow row(StudentAttendance a) {
        return new AttendanceRow(a.getId(), a.getStudent().getId(), a.getStudent().getUser().getName(),
                a.getStudent().getRollNo(), a.getCourse().getId(), a.getCourse().getCourseName(),
                a.getClassDate(), a.getStatus());
    }

    // ---------- faculty's own attendance ----------
    @Transactional
    public FacultyAttendanceRow markFaculty(User actor, FacultyAttendanceRequest r) {
        checkStatus(r.status());
        FacultyProfile f;
        LocalDate date;
        if (actor.getRole() == Role.ADMIN) {
            if (r.facultyId() == null) throw ApiException.bad("Faculty is required");
            f = faculty.findById(r.facultyId()).orElseThrow(() -> ApiException.notFound("Faculty"));
            date = r.date();
        } else {
            f = faculty.findByUserId(actor.getId()).orElseThrow(() -> ApiException.notFound("Faculty profile"));
            date = r.date() == null ? LocalDate.now() : r.date();
            if (!date.equals(LocalDate.now())) throw ApiException.forbidden("Faculty can mark only today's attendance");
        }
        checkDate(date);
        FacultyAttendance a = facultyAtt.findByFacultyIdAndAttendanceDate(f.getId(), date)
                .orElseGet(() -> FacultyAttendance.builder().faculty(f).attendanceDate(date).build());
        a.setStatus(r.status());
        a.setMarkedBy(actor);
        return facultyRow(facultyAtt.save(a));
    }

    public List<FacultyAttendanceRow> facultySearch(Long facultyId, LocalDate from, LocalDate to) {
        return facultyAtt.search(facultyId, from, to).stream().map(this::facultyRow).toList();
    }

    public Optional<FacultyAttendance> facultyToday(Long facultyId) {
        return facultyAtt.findByFacultyIdAndAttendanceDate(facultyId, LocalDate.now());
    }

    private FacultyAttendanceRow facultyRow(FacultyAttendance a) {
        return new FacultyAttendanceRow(a.getId(), a.getFaculty().getId(), a.getFaculty().getUser().getName(),
                a.getFaculty().getEmployeeId(), a.getAttendanceDate(), a.getStatus());
    }

    // ---------- reports ----------
    private static Summary summarise(Long id, String name, String code, List<StudentAttendance> list) {
        long p = list.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long ab = list.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        long l = list.stream().filter(a -> a.getStatus() == AttendanceStatus.LEAVE).count();
        long total = p + ab + l;
        double pct = total == 0 ? 0 : Math.round(p * 1000.0 / total) / 10.0;
        return new Summary(id, name, code, total, p, ab, l, pct);
    }

    /** Per-course summary for one student. */
    public List<Summary> summaryForStudent(StudentProfile s) {
        return enrolments.findByStudentIdAndStatus(s.getId(), EnrolmentStatus.ENROLLED).stream().map(e -> {
            Course c = e.getCourse();
            return summarise(c.getId(), c.getCourseName(), c.getCourseCode(),
                    studentAtt.search(c.getId(), s.getId(), null, null));
        }).toList();
    }

    /** Per-student summary for one course (faculty report). */
    public List<Summary> summaryForCourse(FacultyProfile f, Long courseId) {
        if (!assignments.existsByFacultyIdAndCourseId(f.getId(), courseId))
            throw ApiException.forbidden("You are not assigned to this course");
        List<StudentAttendance> all = studentAtt.search(courseId, null, null, null);
        return enrolments.findByCourseIdAndStatus(courseId, EnrolmentStatus.ENROLLED).stream().map(e -> {
            StudentProfile s = e.getStudent();
            return summarise(s.getId(), s.getUser().getName(), s.getRollNo(),
                    all.stream().filter(a -> a.getStudent().getId().equals(s.getId())).toList());
        }).toList();
    }

    /** Per-course summary across all students (admin report). */
    public List<Summary> courseSummaries() {
        return courses.findAll().stream().map(c ->
                summarise(c.getId(), c.getCourseName(), c.getCourseCode(), studentAtt.search(c.getId(), null, null, null))).toList();
    }

    public AdminStats stats() {
        LocalDate t = LocalDate.now();
        return new AdminStats(students.count(), faculty.count(), courses.count(),
                enrolments.countByStatus(EnrolmentStatus.ENROLLED),
                studentAtt.countByClassDateAndStatus(t, AttendanceStatus.PRESENT),
                facultyAtt.countByAttendanceDateAndStatus(t, AttendanceStatus.PRESENT));
    }
}
