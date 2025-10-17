package com.spartaclub.orderplatform.domain.user.application.mapper;

import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.domain.user.presentation.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

/**
 * User 엔티티와 DTO 간의 매핑을 처리하는 MapStruct 매퍼 인터페이스
 * 컴파일 타임에 구현체가 자동 생성됨
 *
 * @author 전우선
 * @date 2025-10-09(목)
 */
@Mapper(componentModel = "spring", imports = {UserRole.class})
public interface UserMapper {

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

    /**
     * User 엔티티를 관리자 생성 응답의 UserInfo로 변환
     */
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    ManagerCreateResponseDto.UserInfo toManagerUserInfo(User user);

    /**
     * 페이지 정보를 PageableInfo DTO로 변환
     */
    default UserListPageResponseDto.PageableInfo toPageableInfo(Page<User> userPage) {
        return UserListPageResponseDto.PageableInfo.builder()
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .build();
    }

    /**
     * 통계 정보를 SummaryInfo DTO로 변환
     */
    default UserListPageResponseDto.SummaryInfo toSummaryInfo(
            long totalUsers, long activeUsers, long deletedUsers,
            java.util.Map<String, Long> roleDistribution) {
        return UserListPageResponseDto.SummaryInfo.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .deletedUsers(deletedUsers)
                .roleDistribution(roleDistribution)
                .build();
    }

    /**
     * 전체 페이지 응답 DTO를 생성
     */
    default UserListPageResponseDto toPageResponse(
            java.util.List<UserListResponseDto> userList,
            Page<User> userPage,
            UserListPageResponseDto.SummaryInfo summaryInfo) {
        return UserListPageResponseDto.builder()
                .content(userList)
                .pageable(toPageableInfo(userPage))
                .summary(summaryInfo)
                .build();
    }
}