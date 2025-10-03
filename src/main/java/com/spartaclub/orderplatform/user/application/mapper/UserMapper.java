package com.spartaclub.orderplatform.user.application.mapper;

import com.spartaclub.orderplatform.user.presentation.dto.UserSignupRequestDto;
import com.spartaclub.orderplatform.user.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * User 엔티티와 DTO 간의 매핑을 처리하는 MapStruct 매퍼 인터페이스
 * 컴파일 타임에 구현체가 자동 생성됨
 *
 * @author 전우선
 * @date 2025-10-01(수)
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
}