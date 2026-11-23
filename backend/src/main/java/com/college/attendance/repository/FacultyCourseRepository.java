package com.college.attendance.repository;

import com.college.attendance.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface FacultyCourseRepository extends JpaRepository<FacultyCourse, Long> {
    List<FacultyCourse> findByFacultyId(Long facultyId);
    List<FacultyCourse> findByCourseId(Long courseId);
    boolean existsByFacultyIdAndCourseId(Long facultyId, Long courseId);
    void deleteByCourseId(Long courseId);
}
