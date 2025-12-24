package com.subham.projects.lovableClone.dto.auth;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {

}
