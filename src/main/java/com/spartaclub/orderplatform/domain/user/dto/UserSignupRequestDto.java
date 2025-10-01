package com.spartaclub.orderplatform.domain.user.dto; // User DTO 패키지 선언

import com.spartaclub.orderplatform.domain.user.entity.UserRole; // UserRole 열거형 임포트
import jakarta.validation.constraints.*; // 유효성 검증 어노테이션 임포트
import lombok.Getter; // Lombok Getter 어노테이션
import lombok.NoArgsConstructor; // Lombok NoArgsConstructor 어노테이션
import lombok.Setter; // Lombok Setter 어노테이션

/**
 * 회원가입 요청 DTO 클래스
 * 클라이언트로부터 받은 회원가입 데이터를 검증하고 전달
 * 
 * @author 전우선
 * @date 2025-10-01(수)
 */
@Getter // Lombok - 모든 필드에 대한 getter 메서드 자동 생성
@Setter // Lombok - 모든 필드에 대한 setter 메서드 자동 생성
@NoArgsConstructor // Lombok - 기본 생성자 자동 생성
public class UserSignupRequestDto {

    @NotBlank(message = "사용자명은 필수입니다.") // 공백 불허 검증
    @Size(min = 4, max = 10, message = "사용자명은 4-10자 이내여야 합니다.") // 길이 검증
    @Pattern(regexp = "^[a-z0-9]+$", message = "사용자명은 영소문자와 숫자만 사용 가능합니다.") // 형식 검증
    private String userName; // 사용자명 (4-10자, 영소문자+숫자만)

    @NotBlank(message = "이메일은 필수입니다.") // 공백 불허 검증
    @Email(message = "올바른 이메일 형식이 아닙니다.") // 이메일 형식 검증
    @Size(max = 50, message = "이메일은 50자 이내여야 합니다.") // 길이 검증
    private String email; // 이메일 주소 (로그인 ID)

    @NotBlank(message = "비밀번호는 필수입니다.") // 공백 불허 검증
    @Size(min = 8, max = 15, message = "비밀번호는 8-15자 이내여야 합니다.") // 길이 검증
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "비밀번호는 영대소문자, 숫자, 특수문자를 모두 포함해야 합니다.") // 복잡도 검증
    private String password; // 비밀번호 (8-15자, 영대소문자+숫자+특수문자 포함)

    @NotBlank(message = "닉네임은 필수입니다.") // 공백 불허 검증
    @Size(min = 2, max = 10, message = "닉네임은 2-10자 이내여야 합니다.") // 길이 검증 (명세와 맞춤)
    private String nickname; // 닉네임 (2-10자)

    @NotBlank(message = "연락처는 필수입니다.") // 공백 불허 검증
    @Pattern(regexp = "^\\d{10,11}$", message = "연락처는 10-11자리 숫자만 입력 가능합니다.") // 형식 검증
    private String phoneNumber; // 연락처 (10-11자리 숫자, 하이픈 제외)

    @NotBlank(message = "주소는 필수입니다.") // 공백 불허 검증
    @Size(min = 10, max = 200, message = "주소는 10-200자 이내여야 합니다.") // 길이 검증
    private String address; // 주소 (10-200자)

    @NotNull(message = "권한은 필수입니다.") // null 불허 검증
    private UserRole role; // 권한 (CUSTOMER, OWNER만 허용)

    @Pattern(regexp = "^\\d{10}$", message = "사업자번호는 10자리 숫자만 입력 가능합니다.") // 형식 검증 (선택사항)
    private String businessNumber; // 사업자번호 (OWNER 선택 시 필수, 10자리 숫자)

    /**
     * 권한별 사업자번호 유효성 검증
     * OWNER 권한 선택 시 사업자번호 필수
     * CUSTOMER 권한 선택 시 사업자번호 입력 불가
     * 
     * @return 유효성 검증 결과
     */
    @AssertTrue(message = "OWNER 권한 선택 시 사업자번호는 필수입니다.") // 커스텀 검증
    public boolean isValidBusinessNumber() {
        if (role == UserRole.OWNER) { // OWNER 권한인 경우
            return businessNumber != null && !businessNumber.trim().isEmpty(); // 사업자번호 필수
        } else if (role == UserRole.CUSTOMER) { // CUSTOMER 권한인 경우
            return businessNumber == null || businessNumber.trim().isEmpty(); // 사업자번호 입력 불가
        }
        return true; // 기타 경우 통과
    }

    /**
     * 관리자 권한 선택 방지 검증
     * MANAGER, MASTER 권한은 일반 회원가입에서 선택 불가
     * 
     * @return 유효성 검증 결과
     */
    @AssertTrue(message = "MANAGER, MASTER 권한은 선택할 수 없습니다.") // 커스텀 검증
    public boolean isValidRole() {
        return role == UserRole.CUSTOMER || role == UserRole.OWNER; // 일반 회원 권한만 허용
    }
}