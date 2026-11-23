package com.college.attendance.service;

import com.college.attendance.dto.Dtos.*;
import com.college.attendance.entity.*;
import com.college.attendance.exception.ApiException;
import com.college.attendance.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;

/** Courses, faculty assignment, student enrolment, timetable and academic sessions. */
@Service

public class CourseService {
    private final CourseRepository courses;
    private final AcademicSessionRepository sessions;
    private final StudentProfileRepository students;
    private final FacultyProfileRepository faculty;
    private final StudentCourseRepository enrolments;
    private final FacultyCourseRepository assignments;
    private final TimetableRepository timetable;

    public CourseService(CourseRepository courses, AcademicSessionRepository sessions, StudentProfileRepository students, FacultyProfileRepository faculty, StudentCourseRepository enrolments, FacultyCourseRepository assignments, TimetableRepository timetable) {
        this.courses = courses;
        this.sessions = sessions;
        this.students = students;
        this.faculty = faculty;
        this.enrolments = enrolments;
        this.assignments = assignments;
        this.timetable = timetable;
    }

    // ---------- mapping ----------
    private CourseResponse dto(Course c, Boolean enrolled) {
        Optional<FacultyCourse> fc = assignments.findByCourseId(c.getId()).stream().findFirst();
        return new CourseResponse(c.getId(), c.getCourseName(), c.getCourseCode(), c.getCredits(),
                c.getSession() == null ? null : c.getSession().getName(), c.isActive(),
                fc.map(x -> x.getFaculty().getId()).orElse(null),
                fc.map(x -> x.getFaculty().getUser().getName()).orElse(null), enrolled);
    }

    private TimetableResponse dto(TimetableEntry t) {
        Course c = t.getCourse();
        String fac = assignments.findByCourseId(c.getId()).stream().findFirst()
                .map(x -> x.getFaculty().getUser().getName()).orElse(null);
        return new TimetableResponse(t.getId(), c.getId(), c.getCourseName(), c.getCourseCode(), fac,
                t.getDayOfWeek(), t.getStartTime(), t.getEndTime(), t.getRoom());
    }

    private EnrolmentResponse dto(StudentCourse e) {
        return new EnrolmentResponse(e.getId(), e.getStudent().getId(), e.getStudent().getUser().getName(),
                e.getStudent().getRollNo(), e.getCourse().getId(), e.getCourse().getCourseName(),
                e.getCourse().getCourseCode(), e.getStatus());
    }

    // ---------- courses (admin) ----------
    public List<CourseResponse> allCourses() { return courses.findAll().stream().map(c -> dto(c, null)).toList(); }

    @Transactional
    public CourseResponse create(CourseRequest r) {
        String code = r.courseCode().trim().toUpperCase();
        if (courses.existsByCourseCode(code)) throw ApiException.bad("Course code already exists");
        Course c = Course.builder().courseName(r.courseName().trim()).courseCode(code).credits(r.credits())
                .session(sessionOrNull(r.sessionId())).build();
        return dto(courses.save(c), null);
    }

    @Transactional
    public CourseResponse update(Long id, CourseRequest r) {
        Course c = course(id);
        String code = r.courseCode().trim().toUpperCase();
        if (!code.equals(c.getCourseCode()) && courses.existsByCourseCode(code))
            throw ApiException.bad("Course code already exists");
        c.setCourseName(r.courseName().trim());
        c.setCourseCode(code);
        c.setCredits(r.credits());
        c.setSession(sessionOrNull(r.sessionId()));
        return dto(courses.save(c), null);
    }

    @Transactional
    public void setActive(Long id, boolean active) {
        Course c = course(id);
        c.setActive(active);
        courses.save(c);
    }

    private Course course(Long id) { return courses.findById(id).orElseThrow(() -> ApiException.notFound("Course")); }

    private AcademicSession sessionOrNull(Long id) {
        return id == null ? null : sessions.findById(id).orElseThrow(() -> ApiException.notFound("Academic session"));
    }

    // ---------- faculty assignment (a course has one faculty; reassigning replaces it) ----------
    @Transactional
    public CourseResponse assignFaculty(Long courseId, Long facultyId) {
        Course c = course(courseId);
        FacultyProfile f = faculty.findById(facultyId).orElseThrow(() -> ApiException.notFound("Faculty"));
        if (f.getUser().getStatus() != UserStatus.ACTIVE) throw ApiException.bad("Faculty account is deactivated");
        assignments.deleteByCourseId(courseId);
        assignments.flush();
        assignments.save(FacultyCourse.builder().faculty(f).course(c).build());
        return dto(c, null);
    }

    @Transactional
    public void unassignFaculty(Long courseId) { assignments.deleteByCourseId(courseId); }

    public List<CourseResponse> coursesOfFaculty(FacultyProfile f) {
        return assignments.findByFacultyId(f.getId()).stream().map(x -> dto(x.getCourse(), null)).toList();
    }

    public List<PersonResponse> studentsOfCourse(Long courseId, FacultyProfile f) {
        if (!assignments.existsByFacultyIdAndCourseId(f.getId(), courseId))
            throw ApiException.forbidden("You are not assigned to this course");
        return enrolments.findByCourseIdAndStatus(courseId, EnrolmentStatus.ENROLLED).stream().map(e -> {
            StudentProfile s = e.getStudent();
            return new PersonResponse(s.getId(), s.getUser().getId(), s.getUser().getName(), s.getUser().getEmail(),
                    s.getUser().getPhone(), s.getRollNo(), s.getDepartment(), s.getUser().getStatus());
        }).toList();
    }

    // ---------- enrolment ----------
    @Transactional
    public void enrol(Long studentId, Long courseId) {
        StudentProfile s = students.findById(studentId).orElseThrow(() -> ApiException.notFound("Student"));
        Course c = course(courseId);
        if (!c.isActive()) throw ApiException.bad("This course is not open for enrolment");
        if (s.getUser().getStatus() != UserStatus.ACTIVE) throw ApiException.bad("Student account is deactivated");
        StudentCourse e = enrolments.findByStudentIdAndCourseId(studentId, courseId).orElse(null);
        if (e == null) {
            enrolments.save(StudentCourse.builder().student(s).course(c).build());
        } else if (e.getStatus() == EnrolmentStatus.ENROLLED) {
            throw ApiException.bad("Already enrolled in this course");
        } else {
            e.setStatus(EnrolmentStatus.ENROLLED);
            enrolments.save(e);
        }
    }

    @Transactional
    public void drop(Long studentId, Long courseId) {
        StudentCourse e = enrolments.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> ApiException.notFound("Enrolment"));
        e.setStatus(EnrolmentStatus.DROPPED);
        enrolments.save(e);
    }

    public List<EnrolmentResponse> enrolments() {
        return enrolments.findAll().stream().filter(e -> e.getStatus() == EnrolmentStatus.ENROLLED).map(this::dto).toList();
    }

    public List<CourseResponse> availableFor(StudentProfile s) {
        Set<Long> mine = new HashSet<>();
        enrolments.findByStudentIdAndStatus(s.getId(), EnrolmentStatus.ENROLLED).forEach(e -> mine.add(e.getCourse().getId()));
        return courses.findByActiveTrue().stream().map(c -> dto(c, mine.contains(c.getId()))).toList();
    }

    public List<CourseResponse> enrolledFor(StudentProfile s) {
        return enrolments.findByStudentIdAndStatus(s.getId(), EnrolmentStatus.ENROLLED).stream()
                .map(e -> dto(e.getCourse(), true)).toList();
    }

    // ---------- timetable ----------
    public List<TimetableResponse> allTimetable() {
        return sorted(timetable.findAll().stream().map(this::dto).toList());
    }

    public List<TimetableResponse> timetableForStudent(StudentProfile s) {
        List<Long> ids = enrolments.findByStudentIdAndStatus(s.getId(), EnrolmentStatus.ENROLLED).stream()
                .map(e -> e.getCourse().getId()).toList();
        return ids.isEmpty() ? List.of() : sorted(timetable.findByCourseIdIn(ids).stream().map(this::dto).toList());
    }

    public List<TimetableResponse> timetableForFaculty(FacultyProfile f) {
        List<Long> ids = assignments.findByFacultyId(f.getId()).stream().map(x -> x.getCourse().getId()).toList();
        return ids.isEmpty() ? List.of() : sorted(timetable.findByCourseIdIn(ids).stream().map(this::dto).toList());
    }

    private List<TimetableResponse> sorted(List<TimetableResponse> l) {
        return l.stream().sorted(Comparator.comparing(TimetableResponse::dayOfWeek)
                .thenComparing(TimetableResponse::startTime)).toList();
    }

    @Transactional
    public TimetableResponse addTimetable(TimetableRequest r) {
        if (r.dayOfWeek() == DayOfWeek.SATURDAY || r.dayOfWeek() == DayOfWeek.SUNDAY)
            throw ApiException.bad("Classes cannot be scheduled on weekends (holidays)");
        if (!r.endTime().isAfter(r.startTime())) throw ApiException.bad("End time must be after start time");
        Course c = course(r.courseId());
        return dto(timetable.save(TimetableEntry.builder().course(c).dayOfWeek(r.dayOfWeek())
                .startTime(r.startTime()).endTime(r.endTime()).room(r.room()).build()));
    }

    @Transactional
    public void deleteTimetable(Long id) { timetable.deleteById(id); }

    // ---------- academic sessions ----------
    public List<SessionResponse> sessions() {
        return sessions.findAll().stream().map(s -> new SessionResponse(s.getId(), s.getName(),
                s.getStartDate(), s.getEndDate(), s.isCurrent())).toList();
    }

    @Transactional
    public SessionResponse addSession(SessionRequest r) {
        if (r.current()) sessions.findAll().forEach(s -> { s.setCurrent(false); sessions.save(s); });
        AcademicSession s = sessions.save(AcademicSession.builder().name(r.name().trim())
                .startDate(r.startDate()).endDate(r.endDate()).current(r.current()).build());
        return new SessionResponse(s.getId(), s.getName(), s.getStartDate(), s.getEndDate(), s.isCurrent());
    }
}
