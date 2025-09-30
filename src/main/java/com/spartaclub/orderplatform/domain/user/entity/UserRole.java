package com.spartaclub.orderplatform.domain.user.entity; // UserRole enum 패키지 선언

/**
 * 사용자 권한 enum 클래스
 * 로그인 명세에 따른 사용자의 역할과 권한을 정의
 * 
 * @author 전우선
 * @date 2025-09-30(화)
 */
public enum UserRole { // 사용자 권한을 정의하는 열거형 클래스
    
    CUSTOMER("고객"), // 일반 고객 권한 - 자신의 주문 내역만 조회 가능
    OWNER("가게 주인"), // 가게 주인 권한 - 자신의 가게 주문 내역, 가게 정보, 주문 처리 및 메뉴 수정 가능
    MANAGER("서비스 담당자"), // 서비스 담당자 권한 - 모든 가게 및 주문에 대한 전체 권한 보유
    MASTER("최종관리자"); // 최종관리자 권한 - 모든 권한 + MANAGER 생성/조회/수정/삭제 가능

    private final String description; // 권한에 대한 한글 설명

    /**
     * UserRole 생성자
     * @param description 권한에 대한 한글 설명
     */
    UserRole(String description) { // enum 생성자 (private)
        this.description = description; // 권한 설명 필드 초기화
    }

    /**
     * 권한 설명을 반환하는 메서드
     * @return 권한에 대한 한글 설명
     */
    public String getDescription() { // 권한 설명을 반환하는 getter 메서드
        return description; // 권한 설명 반환
    }
}