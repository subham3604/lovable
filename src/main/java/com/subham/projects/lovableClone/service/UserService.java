package com.subham.projects.lovableClone.service;

import com.codingshuttle.projects.lovable_clone.dto.auth.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(Long userId);
}
