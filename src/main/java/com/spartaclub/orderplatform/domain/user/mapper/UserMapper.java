package com.spartaclub.orderplatform.domain.user.mapper; // User 매퍼 패키지 선언

import com.spartaclub.orderplatform.domain.user.dto.UserSignupRequestDto; // 회원가입 요청 DTO 임포트
import com.spartaclub.orderplatform.domain.user.entity.User; // User 엔티티 임포트
import org.mapstruct.Mapper; // MapStruct Mapper 어노테이션
import org.mapstruct.Mapping; // MapStruct Mapping 어노테이션

/**
 * User 엔티티와 DTO 간의 매핑을 처리하는 MapStruct 매퍼 인터페이스
 * 컴파일 타임에 구현체가 자동 생성됨
 * 
 * @author 전우선
 * @date 2025-10-01(수)
 */
@Mapper(componentModel = "spring") // Spring Bean으로 등록되는 MapStruct 매퍼
public interface UserMapper {

    /**
     * 회원가입 요청 DTO를 User 엔티티로 변환
     * userName 필드를 username 필드로 매핑
     * password는 암호화 전 상태이므로 무시하고 별도 처리
     * 
     * @param requestDto 회원가입 요청 DTO
     * @return User 엔티티 (password 제외)
     */
    @Mapping(source = "userName", target = "username") // userName -> username 필드명 매핑
    @Mapping(target = "password", ignore = true) // password는 별도 암호화 처리 필요
    @Mapping(target = "userId", ignore = true) // userId는 자동 생성
    @Mapping(target = "createdAt", ignore = true) // BaseEntity 필드는 자동 관리
    @Mapping(target = "modifiedAt", ignore = true) // BaseEntity 필드는 자동 관리
    @Mapping(target = "deletedAt", ignore = true) // BaseEntity 필드는 자동 관리
    User toEntity(UserSignupRequestDto requestDto);
}