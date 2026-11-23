package com.college.attendance.config;

import com.college.attendance.entity.*;
import com.college.attendance.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/** Seeds the default admin account and one academic session on first start. */

@Component

public class DataInitializer implements CommandLineRunner {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository users;
    private final AcademicSessionRepository sessions;
    private final PasswordEncoder encoder;

    public DataInitializer(UserRepository users, AcademicSessionRepository sessions, PasswordEncoder encoder) {
        this.users = users;
        this.sessions = sessions;
        this.encoder = encoder;
    }

    @Value("${app.admin.name}") private String adminName;
    @Value("${app.admin.email}") private String adminEmail;
    @Value("${app.admin.password}") private String adminPassword;

    @Override
    public void run(String... args) {
        if (!users.existsByEmail(adminEmail.toLowerCase())) {
            users.save(User.builder().name(adminName).email(adminEmail.toLowerCase())
                    .password(encoder.encode(adminPassword)).role(Role.ADMIN).build());
            log.info("Default admin created: {}", adminEmail);
        }
        if (sessions.count() == 0) {
            int y = LocalDate.now().getYear();
            sessions.save(AcademicSession.builder().name(y + "-" + (y + 1) + " Semester 1")
                    .startDate(LocalDate.of(y, 7, 1)).endDate(LocalDate.of(y, 12, 31)).current(true).build());
        }
    }
}
