package com.subham.projects.lovableClone.service;

import com.subham.projects.lovableClone.dto.auth.AuthResponse;
import com.subham.projects.lovableClone.dto.auth.LoginRequest;
import com.subham.projects.lovableClone.dto.auth.SignupRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
