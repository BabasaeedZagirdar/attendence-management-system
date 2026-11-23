package com.college.attendance.service;

import com.college.attendance.dto.Dtos.*;
import com.college.attendance.entity.*;
import com.college.attendance.exception.ApiException;
import com.college.attendance.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

public class ProfileService {
    private final UserRepository users;
    private final StudentProfileRepository students;
    private final FacultyProfileRepository faculty;

    public ProfileService(UserRepository users, StudentProfileRepository students, FacultyProfileRepository faculty) {
        this.users = users;
        this.students = students;
        this.faculty = faculty;
    }

    public StudentProfile studentOf(User u) {
        return students.findByUserId(u.getId()).orElseThrow(() -> ApiException.notFound("Student profile"));
    }

    public FacultyProfile facultyOf(User u) {
        return faculty.findByUserId(u.getId()).orElseThrow(() -> ApiException.notFound("Faculty profile"));
    }

    public ProfileResponse get(User u) {
        String identifier = null, department = null;
        if (u.getRole() == Role.STUDENT) {
            StudentProfile s = studentOf(u);
            identifier = s.getRollNo();
            department = s.getDepartment();
        } else if (u.getRole() == Role.FACULTY) {
            FacultyProfile f = facultyOf(u);
            identifier = f.getEmployeeId();
            department = f.getDepartment();
        }
        return new ProfileResponse(u.getName(), u.getEmail(), u.getPhone(), u.getRole(), identifier, department);
    }

    @Transactional
    public ProfileResponse update(User u, ProfileUpdateRequest r) {
        u.setName(r.name().trim());
        u.setPhone(r.phone());
        users.save(u);
        return get(u);
    }
}
