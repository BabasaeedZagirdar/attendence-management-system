package com.college.attendance.controller;

import com.college.attendance.dto.Dtos.*;
import com.college.attendance.entity.*;
import com.college.attendance.service.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;

/** View and update the logged-in user's own profile (all roles). */
@RestController
@RequestMapping("/api/profile")

public class ProfileController {
    private final ProfileService profiles;

    public ProfileController(ProfileService profiles) {
        this.profiles = profiles;
    }

    @GetMapping
    public ProfileResponse get(@AuthenticationPrincipal User user) { return profiles.get(user); }

    @PutMapping
    public ProfileResponse update(@AuthenticationPrincipal User user, @Valid @RequestBody ProfileUpdateRequest r) {
        return profiles.update(user, r);
    }
}
