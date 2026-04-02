package com.subham.projects.lovableClone.mapper;

import com.subham.projects.lovableClone.dto.auth.SignupRequest;
import com.subham.projects.lovableClone.dto.auth.UserProfileResponse;
import com.subham.projects.lovableClone.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUserEntityFromSignupRequest(SignupRequest request);

    UserProfileResponse toUserProfileResponseFromUser(User user);
}