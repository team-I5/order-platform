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

public class WithCustomMockCustomerSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCustomUserCustomer> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUserCustomer annotation) {
        // OWNER 역할의 테스트 유저 생성
        User user = User.createUser(
            "customerTest",              // username
            "customer@test.com",         // email
            "password123!",           // password
            "고객테스터",               // nickname
            "01012345678",            // phoneNumber
            UserRole.CUSTOMER
        );

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(
            userDetails,
            "password",
            List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
