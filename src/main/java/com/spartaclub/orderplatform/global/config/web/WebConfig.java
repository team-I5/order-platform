package com.spartaclub.orderplatform.global.config.web;

import com.spartaclub.orderplatform.global.exception.advice.PageableHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * ✅ WebMvcConfigurer
 *
 * Spring MVC 전역 설정을 확장하는 클래스.
 * 여기서 우리가 만든 CustomPageableResolver를 등록하여,
 * 모든 Controller에서 Pageable 요청 파라미터 정책을 강제 적용합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final PageableHandler pageableHandler;

    public WebConfig(PageableHandler pageableHandler) {
        this.pageableHandler = pageableHandler;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(pageableHandler);
    }
}