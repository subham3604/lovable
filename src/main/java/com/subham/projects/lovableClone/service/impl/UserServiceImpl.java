package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.auth.UserProfileResponse;
import com.subham.projects.lovableClone.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserProfileResponse getProfile(Long userId) {
        return null;
    }
}
