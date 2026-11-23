package com.college.attendance.repository;

import com.college.attendance.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface FacultyAttendanceRepository extends JpaRepository<FacultyAttendance, Long> {
    Optional<FacultyAttendance> findByFacultyIdAndAttendanceDate(Long facultyId, LocalDate date);
    long countByAttendanceDateAndStatus(LocalDate date, AttendanceStatus status);

    @Query("select a from FacultyAttendance a where (:facultyId is null or a.faculty.id = :facultyId) " +
           "and (:from is null or a.attendanceDate >= :from) and (:to is null or a.attendanceDate <= :to) " +
           "order by a.attendanceDate desc, a.id desc")
    List<FacultyAttendance> search(@Param("facultyId") Long facultyId,
                                   @Param("from") LocalDate from, @Param("to") LocalDate to);
}
