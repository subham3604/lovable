package com.subham.projects.lovableClone.controller;

import com.subham.projects.lovableClone.dto.auth.UserProfileResponse;
import com.subham.projects.lovableClone.security.AuthUtil;
import com.subham.projects.lovableClone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthUtil authUtil;

    @GetMapping("/api/me")
    public ResponseEntity<UserProfileResponse> getProfile() {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(userService.getProfile(userId));
    }
}
