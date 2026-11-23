package com.college.attendance.service;

import com.college.attendance.dto.Dtos.*;
import com.college.attendance.entity.*;
import com.college.attendance.exception.ApiException;
import com.college.attendance.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Admin: create, update and (de)activate student and faculty accounts. */
@Service

public class UserAdminService {
    private final UserRepository users;
    private final StudentProfileRepository students;
    private final FacultyProfileRepository faculty;
    private final PasswordEncoder encoder;

    public UserAdminService(UserRepository users, StudentProfileRepository students, FacultyProfileRepository faculty, PasswordEncoder encoder) {
        this.users = users;
        this.students = students;
        this.faculty = faculty;
        this.encoder = encoder;
    }

    // ----- helpers -----
    private User newUser(String name, String email, String pw, String phone, Role role) {
        if (pw == null || pw.length() < 6) throw ApiException.bad("Password must be at least 6 characters");
        String e = email.trim().toLowerCase();
        if (users.existsByEmail(e)) throw ApiException.bad("Email already registered");
        return users.save(User.builder().name(name.trim()).email(e).password(encoder.encode(pw))
                .phone(phone).role(role).build());
    }

    private void updateUser(User u, String name, String email, String pw, String phone) {
        String e = email.trim().toLowerCase();
        if (!e.equals(u.getEmail()) && users.existsByEmail(e)) throw ApiException.bad("Email already registered");
        u.setName(name.trim());
        u.setEmail(e);
        u.setPhone(phone);
        if (pw != null && !pw.isBlank()) {
            if (pw.length() < 6) throw ApiException.bad("Password must be at least 6 characters");
            u.setPassword(encoder.encode(pw));
        }
        users.save(u);
    }

    private PersonResponse dto(StudentProfile s) {
        User u = s.getUser();
        return new PersonResponse(s.getId(), u.getId(), u.getName(), u.getEmail(), u.getPhone(),
                s.getRollNo(), s.getDepartment(), u.getStatus());
    }

    private PersonResponse dto(FacultyProfile f) {
        User u = f.getUser();
        return new PersonResponse(f.getId(), u.getId(), u.getName(), u.getEmail(), u.getPhone(),
                f.getEmployeeId(), f.getDepartment(), u.getStatus());
    }

    // ----- students -----
    public List<PersonResponse> students() { return students.findAll().stream().map(this::dto).toList(); }

    @Transactional
    public PersonResponse createStudent(StudentRequest r) {
        if (students.existsByRollNo(r.rollNo().trim())) throw ApiException.bad("Roll number already exists");
        User u = newUser(r.name(), r.email(), r.password(), r.phone(), Role.STUDENT);
        return dto(students.save(StudentProfile.builder().user(u).rollNo(r.rollNo().trim()).department(r.department()).build()));
    }

    @Transactional
    public PersonResponse updateStudent(Long id, StudentRequest r) {
        StudentProfile s = students.findById(id).orElseThrow(() -> ApiException.notFound("Student"));
        if (!r.rollNo().trim().equals(s.getRollNo()) && students.existsByRollNo(r.rollNo().trim()))
            throw ApiException.bad("Roll number already exists");
        updateUser(s.getUser(), r.name(), r.email(), r.password(), r.phone());
        s.setRollNo(r.rollNo().trim());
        s.setDepartment(r.department());
        return dto(students.save(s));
    }

    @Transactional
    public void setStudentActive(Long id, boolean active) {
        User u = students.findById(id).orElseThrow(() -> ApiException.notFound("Student")).getUser();
        u.setStatus(active ? UserStatus.ACTIVE : UserStatus.INACTIVE);
        users.save(u);
    }

    // ----- faculty -----
    public List<PersonResponse> faculty() { return faculty.findAll().stream().map(this::dto).toList(); }

    @Transactional
    public PersonResponse createFaculty(FacultyRequest r) {
        if (faculty.existsByEmployeeId(r.employeeId().trim())) throw ApiException.bad("Employee ID already exists");
        User u = newUser(r.name(), r.email(), r.password(), r.phone(), Role.FACULTY);
        return dto(faculty.save(FacultyProfile.builder().user(u).employeeId(r.employeeId().trim()).department(r.department()).build()));
    }

    @Transactional
    public PersonResponse updateFaculty(Long id, FacultyRequest r) {
        FacultyProfile f = faculty.findById(id).orElseThrow(() -> ApiException.notFound("Faculty"));
        if (!r.employeeId().trim().equals(f.getEmployeeId()) && faculty.existsByEmployeeId(r.employeeId().trim()))
            throw ApiException.bad("Employee ID already exists");
        updateUser(f.getUser(), r.name(), r.email(), r.password(), r.phone());
        f.setEmployeeId(r.employeeId().trim());
        f.setDepartment(r.department());
        return dto(faculty.save(f));
    }

    @Transactional
    public void setFacultyActive(Long id, boolean active) {
        User u = faculty.findById(id).orElseThrow(() -> ApiException.notFound("Faculty")).getUser();
        u.setStatus(active ? UserStatus.ACTIVE : UserStatus.INACTIVE);
        users.save(u);
    }
}
