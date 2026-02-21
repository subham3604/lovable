package com.subham.projects.lovableClone.mapper;

import com.subham.projects.lovableClone.dto.member.MemberResponse;
import com.subham.projects.lovableClone.entity.ProjectMember;
import com.subham.projects.lovableClone.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {
    @Mapping(source = "id", target = "userId")
    @Mapping(target = "projectRole", constant = "OWNER")
    MemberResponse toMemberResponseFromUser(User owner);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "name", source = "user.name")
    MemberResponse toMemberResponseFromMember(ProjectMember projectMember);
}
