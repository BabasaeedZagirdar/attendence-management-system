package com.college.attendance.repository;

import com.college.attendance.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Long> {
    Optional<StudentAttendance> findByStudentIdAndCourseIdAndClassDate(Long studentId, Long courseId, LocalDate date);
    List<StudentAttendance> findByCourseIdAndClassDate(Long courseId, LocalDate date);
    long countByClassDateAndStatus(LocalDate date, AttendanceStatus status);

    @Query("select a from StudentAttendance a where (:courseId is null or a.course.id = :courseId) " +
           "and (:studentId is null or a.student.id = :studentId) " +
           "and (:from is null or a.classDate >= :from) and (:to is null or a.classDate <= :to) " +
           "order by a.classDate desc, a.id desc")
    List<StudentAttendance> search(@Param("courseId") Long courseId, @Param("studentId") Long studentId,
                                   @Param("from") LocalDate from, @Param("to") LocalDate to);
}
