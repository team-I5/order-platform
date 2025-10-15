package com.spartaclub.orderplatform.domain.user.domain.service;

/**
 * 비밀번호 암호화 서비스 인터페이스
 * 도메인 계층의 암호화 정책을 정의
 * 구체적인 암호화 방식(BCrypt, Argon2 등)은 Infrastructure 계층에서 구현
 */
public interface PasswordEncryptionService {

    /**
     * 평문 비밀번호를 암호화
     * 
     * @param rawPassword 평문 비밀번호
     * @return 암호화된 비밀번호
     */
    String encryptPassword(String rawPassword);

    /**
     * 평문 비밀번호와 암호화된 비밀번호가 일치하는지 검증
     * 
     * @param rawPassword 평문 비밀번호
     * @param encryptedPassword 암호화된 비밀번호
     * @return 일치 여부
     */
    boolean matches(String rawPassword, String encryptedPassword);
}