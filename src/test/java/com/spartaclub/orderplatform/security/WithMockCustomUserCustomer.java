package com.spartaclub.orderplatform.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockCustomerSecurityContextFactory.class)
public @interface WithMockCustomUserCustomer {

}
