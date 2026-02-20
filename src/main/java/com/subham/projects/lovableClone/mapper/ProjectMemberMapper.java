package com.subham.projects.lovableClone.mapper;

import com.subham.projects.lovableClone.dto.member.MemberResponse;
import com.subham.projects.lovableClone.entity.ProjectMember;
import com.subham.projects.lovableClone.entity.User;
import com.subham.projects.lovableClone.enums.ProjectRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {
    @Mapping(source = "id", target = "userId")
    @Mapping(target = "projectRole", constant = "OWNER")
    MemberResponse toMemberResponseFromUser(User owner);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "name", source = "user.name")
    MemberResponse toMemberResponseFromMember(ProjectMember projectMember);
}
