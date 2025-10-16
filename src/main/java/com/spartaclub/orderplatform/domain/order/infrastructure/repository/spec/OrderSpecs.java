package com.spartaclub.orderplatform.domain.order.infrastructure.repository.spec;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import org.springframework.data.jpa.domain.Specification;

public final class OrderSpecs {

    private OrderSpecs() {
    }

    // 역할별 가시성
    public static Specification<Order> visibleFor(User viewer) {
        return (root, q, cb) -> switch (viewer.getRole()) {
            case CUSTOMER -> cb.equal(root.get("user").get("userId"), viewer.getUserId());
            case OWNER -> {
                // EXISTS (select 1 from Store s
                //         where s.storeId = order.storeId
                //           and s.user.userId = :viewerUserId)
                var sub = q.subquery(Integer.class);
                var s = sub.from(Store.class);
                sub.select(cb.literal(1));
                sub.where(
                    cb.equal(s.get("storeId"), root.get("storeId")),
                    cb.equal(s.get("user").get("userId"), viewer.getUserId())
                );
                yield cb.exists(sub);
            }
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
