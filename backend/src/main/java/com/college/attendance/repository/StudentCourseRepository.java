package com.college.attendance.repository;

import com.college.attendance.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    List<StudentCourse> findByStudentIdAndStatus(Long studentId, EnrolmentStatus status);
    List<StudentCourse> findByCourseIdAndStatus(Long courseId, EnrolmentStatus status);
    Optional<StudentCourse> findByStudentIdAndCourseId(Long studentId, Long courseId);
    boolean existsByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, EnrolmentStatus status);
    long countByStatus(EnrolmentStatus status);
}
