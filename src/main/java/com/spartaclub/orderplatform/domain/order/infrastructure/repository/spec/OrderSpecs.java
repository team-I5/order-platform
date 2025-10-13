package com.spartaclub.orderplatform.domain.order.infrastructure.repository.spec;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import com.spartaclub.orderplatform.user.domain.entity.User;
import org.springframework.data.jpa.domain.Specification;

public final class OrderSpecs {

    private OrderSpecs() {
    }

    // 역할별 가시성
    public static Specification<Order> visibleFor(User viewer) {
        return (root, q, cb) -> switch (viewer.getRole()) {
            case CUSTOMER -> cb.equal(root.get("user").get("userId"), viewer.getUserId());
            case OWNER -> cb.equal(root.get("store").get("user").get("userId"), viewer.getUserId());
            case MASTER, MANAGER -> cb.conjunction(); // 모두 허용
            default -> cb.disjunction(); // 서비스에서 AccessDeniedException
        };
    }

    // 상태 필터
    public static Specification<Order> statusIn(java.util.List<OrderStatus> statuses) {
        return (root, q, cb) -> {
            if (statuses == null || statuses.isEmpty()) {
                return cb.conjunction();
            }
            var in = cb.in(root.get("status"));
            statuses.forEach(in::value);
            return in;
        };
    }
}
