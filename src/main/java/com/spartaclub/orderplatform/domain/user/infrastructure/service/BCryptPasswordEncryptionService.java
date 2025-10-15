package com.spartaclub.orderplatform.domain.user.infrastructure.service;

import com.spartaclub.orderplatform.domain.user.domain.service.PasswordEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * BCrypt 기반 비밀번호 암호화 서비스 구현체
 * Infrastructure 계층에서 구체적인 암호화 방식을 구현
 */
@Service
@RequiredArgsConstructor
public class BCryptPasswordEncryptionService implements PasswordEncryptionService {

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encryptedPassword) {
        return passwordEncoder.matches(rawPassword, encryptedPassword);
    }
}