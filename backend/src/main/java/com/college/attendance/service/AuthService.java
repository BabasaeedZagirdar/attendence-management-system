package com.college.attendance.service;

import com.college.attendance.dto.Dtos.*;
import com.college.attendance.entity.*;
import com.college.attendance.exception.ApiException;
import com.college.attendance.repository.*;
import com.college.attendance.security.GoogleTokenVerifier;
import com.college.attendance.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service

public class AuthService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthService.class);
    private final UserRepository users;
    private final StudentProfileRepository students;
    private final PasswordResetTokenRepository resetTokens;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final GoogleTokenVerifier google;

    public AuthService(UserRepository users, StudentProfileRepository students, PasswordResetTokenRepository resetTokens, PasswordEncoder encoder, JwtService jwt, GoogleTokenVerifier google) {
        this.users = users;
        this.students = students;
        this.resetTokens = resetTokens;
        this.encoder = encoder;
        this.jwt = jwt;
        this.google = google;
    }

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private AuthResponse respond(User u) {
        return new AuthResponse(jwt.generate(u), new UserInfo(u.getId(), u.getName(), u.getEmail(), u.getRole()));
    }

    public AuthResponse login(LoginRequest r) {
        User u = users.findByEmail(r.email().trim().toLowerCase())
                .filter(x -> encoder.matches(r.password(), x.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (u.getStatus() != UserStatus.ACTIVE)
            throw new ApiException(HttpStatus.FORBIDDEN, "This account is deactivated. Contact the administrator.");
        return respond(u);
    }

    /** Self-registration is for students only. Faculty and admin accounts are created by an admin. */
    @Transactional
    public AuthResponse register(RegisterRequest r) {
        String email = r.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw ApiException.bad("Email already registered");
        if (students.existsByRollNo(r.rollNo().trim())) throw ApiException.bad("Roll number already registered");
        User u = users.save(User.builder().name(r.name().trim()).email(email)
                .password(encoder.encode(r.password())).phone(r.phone()).role(Role.STUDENT).build());
        students.save(StudentProfile.builder().user(u).rollNo(r.rollNo().trim()).department(r.department()).build());
        return respond(u);
    }

    /** Existing users sign in; unknown Google accounts are registered as students. */
    @Transactional
    public AuthResponse googleLogin(String idToken) {
        var g = google.verify(idToken);
        User u = users.findByEmail(g.email()).orElseGet(() -> {
            User created = users.save(User.builder().name(g.name()).email(g.email())
                    .password(encoder.encode(UUID.randomUUID().toString())).role(Role.STUDENT).build());
            students.save(StudentProfile.builder().user(created).build());
            return created;
        });
        if (u.getStatus() != UserStatus.ACTIVE)
            throw new ApiException(HttpStatus.FORBIDDEN, "This account is deactivated. Contact the administrator.");
        return respond(u);
    }

    /** No mail server is configured: the reset link is written to the server log. Plug JavaMailSender in here. */
    @Transactional
    public void forgotPassword(String email) {
        users.findByEmail(email.trim().toLowerCase()).ifPresent(u -> {
            String token = UUID.randomUUID().toString();
            resetTokens.save(PasswordResetToken.builder().token(token).user(u)
                    .expiresAt(LocalDateTime.now().plusMinutes(30)).build());
            log.info("PASSWORD RESET LINK for {}: {}/reset-password?token={}", u.getEmail(), frontendUrl, token);
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest r) {
        PasswordResetToken t = resetTokens.findByToken(r.token())
                .filter(x -> !x.isUsed() && x.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> ApiException.bad("Reset link is invalid or has expired"));
        t.getUser().setPassword(encoder.encode(r.newPassword()));
        users.save(t.getUser());
        t.setUsed(true);
        resetTokens.save(t);
    }
}
