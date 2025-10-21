package com.spartaclub.orderplatform.global.application.aop;

import com.spartaclub.orderplatform.domain.ai.application.service.AiService;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductCreateRequestDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductResponseDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
@RequiredArgsConstructor
public class AiLogAfterCommitAspect {
    private final AiService aiService;

    // createProduct 또는 updateProduct에만 적용
    @AfterReturning(
            pointcut = "execution(* com.spartaclub.orderplatform.domain.product.application.service.ProductService.createProduct(..)) || " +
                    "execution(* com.spartaclub.orderplatform.domain.product.application.service.ProductService.updateProduct(..))",
            returning = "result"
    )
    public void afterProductCommit(JoinPoint joinPoint, Object result) {
        ProductResponseDto response = (ProductResponseDto) result;
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();

        Long userId;
        String description;
        boolean isUpdate = methodName.equals("updateProduct");

        if (isUpdate) {
            // updateProduct(Long userId, UUID productId, ProductUpdateRequestDto dto)
            userId = (Long) args[0];
            ProductUpdateRequestDto dto = (ProductUpdateRequestDto) args[2];
            description = dto.getProductDescription();
        } else {
            // createProduct(ProductCreateRequestDto dto, Long userId)
            ProductCreateRequestDto dto = (ProductCreateRequestDto) args[0];
            userId = (Long) args[1];
            description = dto.getProductDescription();
        }
        System.out.println("[DEBUG] afterProductCommit 실행됨, method=" + joinPoint.getSignature().getName());

        // 커밋 이후 실행
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                aiService.saveOrUpdateAiLogs(
                        userId,
                        response.getProductId(),
                        description,
                        isUpdate
                );
            }
        });
        boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
        System.out.println("[DEBUG] transaction active=" + isActive);

    }
}
