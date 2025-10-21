package com.spartaclub.orderplatform.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockManagerSecurityContextFactory.class)
public @interface WithMockCustomManager {

}
