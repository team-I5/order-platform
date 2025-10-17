package com.spartaclub.orderplatform.security;

import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomMockOwnerSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCustomOwner> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomOwner annotation) {
        // OWNER 역할의 테스트 유저 생성
        User user = User.createBusinessUser(
            "ownerTest",              // username
            "owner@test.com",         // email
            "password123!",           // password
            "점주테스터",               // nickname
            "01012345678",            // phoneNumber
            UserRole.OWNER,
            "1234567890"              // businessNumber
        );

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(
            userDetails,
            "password",
            List.of(new SimpleGrantedAuthority("ROLE_OWNER"))
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
