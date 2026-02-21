package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.auth.AuthResponse;
import com.subham.projects.lovableClone.dto.auth.LoginRequest;
import com.subham.projects.lovableClone.dto.auth.SignupRequest;
import com.subham.projects.lovableClone.entity.User;
import com.subham.projects.lovableClone.error.BadRequestException;
import com.subham.projects.lovableClone.mapper.UserMapper;
import com.subham.projects.lovableClone.repository.UserRepository;
import com.subham.projects.lovableClone.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    @Override
    public AuthResponse signup(SignupRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(user -> {
            throw new BadRequestException("User already exists with username: " + request.username());
        });

        User user = userMapper.toUserEntityFromSignupRequest(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return new AuthResponse("dummy", userMapper.toUserProfileResponseFromUser(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}
