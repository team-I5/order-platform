package com.spartaclub.orderplatform.global.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 간단한 API 응답 클래스 포스트맨 테스트용으로 성공/실패와 메시지만 포함
 *
 * @author 전우선
 * @date 2025-09-30(화)
 */
@Getter // Lombok - 모든 필드에 대한 getter 메서드 자동 생성 (isSuccess(), getMessage(), getData())
@AllArgsConstructor // Lombok - 모든 필드를 매개변수로 받는 생성자 자동 생성
public class ApiResponse<T> { // 제네릭 타입 T를 사용하여 다양한 데이터 타입을 담을 수 있는 응답 클래스

    private boolean success; // API 호출 성공/실패 여부를 나타내는 불린 값
    private String message; // 클라이언트에게 전달할 응답 메시지 (성공/실패 메시지)
    private T data; // 실제 응답 데이터 (제네릭 타입으로 다양한 데이터 타입 지원, 없으면 null)

    /**
     * 성공 응답 생성 메서드 (데이터 포함) 데이터와 함께 성공 응답을 반환할 때 사용
     */
    public static <T> ApiResponse<T> success(T data) { // 데이터를 받아 성공 응답 생성하는 정적 메서드
        return new ApiResponse<>(true, "성공", data); // success=true, 기본 성공 메시지, 전달받은 데이터로 객체 생성
    }

    /**
     * 성공 응답 생성 메서드 (메시지만) 커스텀 성공 메시지만 전달하고 데이터는 없을 때 사용
     */
    public static <T> ApiResponse<T> success(String message) { // 메시지만 받아 성공 응답 생성하는 정적 메서드
        return new ApiResponse<>(true, message, null); // success=true, 전달받은 메시지, data=null로 객체 생성
    }

    /**
     * 성공 응답 생성 메서드 (데이터 미포함) 데이터 없이 성공 응답을 반환할 때 사용
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null, null);
    }

    /**
     * 실패 응답 생성 메서드 에러 메시지와 함께 실패 응답을 반환할 때 사용
     */
    public static <T> ApiResponse<T> error(String message) { // 에러 메시지를 받아 실패 응답 생성하는 정적 메서드
        return new ApiResponse<>(false, message,
            null); // success=false, 전달받은 에러 메시지, data=null로 객체 생성
    }
}