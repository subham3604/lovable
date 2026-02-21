package com.subham.projects.lovableClone.controller;

import com.subham.projects.lovableClone.dto.auth.AuthResponse;
import com.subham.projects.lovableClone.dto.auth.LoginRequest;
import com.subham.projects.lovableClone.dto.auth.SignupRequest;
import com.subham.projects.lovableClone.dto.auth.UserProfileResponse;
import com.subham.projects.lovableClone.service.AuthService;
import com.subham.projects.lovableClone.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile() {
        Long userId = 1L;
        return ResponseEntity.ok(userService.getProfile(userId));
    }

}
