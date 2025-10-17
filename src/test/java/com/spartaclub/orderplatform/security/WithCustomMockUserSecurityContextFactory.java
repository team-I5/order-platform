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

public class WithCustomMockUserSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        User user = User.createManager("sosofkf", "emaa@testy.com", "sfwofjowf", "fnicj",
            "01052111111");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "password",
            List.of(new SimpleGrantedAuthority("ROLE_MASTER")));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);

        return context;
    }
}
