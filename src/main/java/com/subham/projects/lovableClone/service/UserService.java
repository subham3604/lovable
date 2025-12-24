package com.subham.projects.lovableClone.service;

import com.subham.projects.lovableClone.dto.auth.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(Long userId);
}
