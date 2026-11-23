package com.college.attendance.dto;

import com.college.attendance.entity.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.*;
import java.util.List;

/** All request/response objects (DTO layer: transfers data between frontend and backend). */
public final class Dtos {
    private Dtos() {}

    // ---------- Auth ----------
    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record RegisterRequest(@NotBlank String name, @Email @NotBlank String email,
                                  @NotBlank @Size(min = 6, message = "must be at least 6 characters") String password,
                                  @NotBlank String rollNo, String department, String phone) {}
    public record GoogleLoginRequest(@NotBlank String idToken) {}
    public record ForgotPasswordRequest(@Email @NotBlank String email) {}
    public record ResetPasswordRequest(@NotBlank String token,
                                       @NotBlank @Size(min = 6, message = "must be at least 6 characters") String newPassword) {}
    public record UserInfo(Long id, String name, String email, Role role) {}
    public record AuthResponse(String token, UserInfo user) {}

    // ---------- Admin: people ----------
    public record StudentRequest(@NotBlank String name, @Email @NotBlank String email, String password,
                                 String phone, @NotBlank String rollNo, String department) {}
    public record FacultyRequest(@NotBlank String name, @Email @NotBlank String email, String password,
                                 String phone, @NotBlank String employeeId, String department) {}
    /** id = profile id (student_profile.id / faculty_profile.id). identifier = rollNo or employeeId. */
    public record PersonResponse(Long id, Long userId, String name, String email, String phone,
                                 String identifier, String department, UserStatus status) {}

    // ---------- Profile ----------
    public record ProfileResponse(String name, String email, String phone, Role role, String identifier, String department) {}
    public record ProfileUpdateRequest(@NotBlank String name, String phone) {}

    // ---------- Courses ----------
    public record CourseRequest(@NotBlank String courseName, @NotBlank String courseCode,
                                @Min(1) @Max(10) int credits, Long sessionId) {}
    public record CourseResponse(Long id, String courseName, String courseCode, int credits, String sessionName,
                                 boolean active, Long facultyId, String facultyName, Boolean enrolled) {}
    public record EnrolRequest(@NotNull Long studentId, @NotNull Long courseId) {}
    public record EnrolmentResponse(Long id, Long studentId, String studentName, String rollNo,
                                    Long courseId, String courseName, String courseCode, EnrolmentStatus status) {}
    public record SessionRequest(@NotBlank String name, LocalDate startDate, LocalDate endDate, boolean current) {}
    public record SessionResponse(Long id, String name, LocalDate startDate, LocalDate endDate, boolean current) {}

    // ---------- Timetable ----------
    public record TimetableRequest(@NotNull Long courseId, @NotNull DayOfWeek dayOfWeek,
                                   @NotNull LocalTime startTime, @NotNull LocalTime endTime, String room) {}
    public record TimetableResponse(Long id, Long courseId, String courseName, String courseCode,
                                    String facultyName, DayOfWeek dayOfWeek, LocalTime startTime,
                                    LocalTime endTime, String room) {}

    // ---------- Attendance ----------
    public record AttendanceEntry(@NotNull Long studentId, @NotNull AttendanceStatus status) {}
    public record MarkAttendanceRequest(@NotNull Long courseId, @NotNull LocalDate date,
                                        @NotEmpty List<@Valid AttendanceEntry> records) {}
    public record AttendanceRow(Long id, Long studentId, String studentName, String rollNo,
                                Long courseId, String courseName, LocalDate date, AttendanceStatus status) {}
    /** facultyId is required only when an admin marks; faculty always mark their own. */
    public record FacultyAttendanceRequest(LocalDate date, @NotNull AttendanceStatus status, Long facultyId) {}
    public record FacultyAttendanceRow(Long id, Long facultyId, String facultyName, String employeeId,
                                       LocalDate date, AttendanceStatus status) {}
    /** Generic summary: id/name/code = course (id,name,code) or student (id,name,rollNo). */
    public record Summary(Long id, String name, String code, long total, long present, long absent,
                          long leave, double percentage) {}

    // ---------- Dashboards ----------
    public record AdminStats(long students, long faculty, long courses, long activeEnrolments,
                             long studentsPresentToday, long facultyPresentToday) {}
    public record StudentDashboard(long enrolledCourses, double overallAttendance, long classesAttended,
                                   List<TimetableResponse> todayClasses) {}
    public record FacultyDashboard(long assignedCourses, long totalStudents, String todayStatus,
                                   List<TimetableResponse> todayClasses) {}
}
