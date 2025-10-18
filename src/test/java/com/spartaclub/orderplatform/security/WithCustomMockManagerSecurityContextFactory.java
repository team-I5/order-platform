package com.spartaclub.orderplatform.security;

import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomMockManagerSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCustomManager> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomManager annotation) {
        User user = User.createManager("sdcvsdf", "wervsdv@testy.com", "sdfvcdcd", "asdvasd",
            "01099999999");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "password",
            List.of(new SimpleGrantedAuthority("ROLE_MANAGER")));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);

        return context;
    }
}
