package com.college.attendance.repository;

import com.college.attendance.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCourseCode(String code);
    List<Course> findByActiveTrue();
}
