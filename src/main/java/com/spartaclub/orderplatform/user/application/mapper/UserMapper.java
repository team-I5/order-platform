package com.spartaclub.orderplatform.user.application.mapper;

import com.spartaclub.orderplatform.user.domain.entity.User;
import com.spartaclub.orderplatform.user.presentation.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * User 엔티티와 DTO 간의 매핑을 처리하는 MapStruct 매퍼 인터페이스
 * 컴파일 타임에 구현체가 자동 생성됨
 *
 * @author 전우선
 * @date 2025-10-08(수)
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * 회원가입 요청 DTO를 User 엔티티로 변환
     * password는 별도 암호화 처리 필요
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    User toEntity(UserSignupRequestDto requestDto);

    /**
     * User 엔티티를 공통 사용자 정보 DTO로 변환
     */
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserInfoDto toUserInfo(User user);

    /**
     * User 엔티티를 프로필 응답 DTO로 변환
     */
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserProfileResponseDto toProfileResponse(User user);

    /**
     * User 엔티티를 업데이트 응답 DTO로 변환
     */
    @Mapping(target = "message", constant = "회원정보가 성공적으로 수정되었습니다.")
    @Mapping(target = "userInfo", expression = "java(toUserInfo(user))")
    UserUpdateResponseDto toUpdateResponse(User user);

    /**
     * User 엔티티를 회원 목록 응답 DTO로 변환
     */
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "isActive", expression = "java(!user.isDeleted())")
    UserListResponseDto toListResponse(User user);
}