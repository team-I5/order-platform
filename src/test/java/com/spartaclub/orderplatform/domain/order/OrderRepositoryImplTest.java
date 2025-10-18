package com.spartaclub.orderplatform.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.RETURNS_DEEP_STUBS;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import com.spartaclub.orderplatform.domain.order.application.query.OrderSpecQuery;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.infrastructure.repository.OrderJpaRepository;
import com.spartaclub.orderplatform.domain.order.infrastructure.repository.OrderRepositoryImpl;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

    @Mock
    OrderJpaRepository orderJpaRepository;

    @InjectMocks
    OrderRepositoryImpl orderRepository; // SUT

    @Test
    @DisplayName("save: JPA 레포지토리에 위임된다")
    void save_delegatesToJpaRepository() {
        Order order = mock(Order.class);
        given(orderJpaRepository.save(order)).willReturn(order);

        Order saved = orderRepository.save(order);

        assertThat(saved).isSameAs(order);
        then(orderJpaRepository).should(times(1)).save(order);
    }

    @Test
    @DisplayName("findById: JPA 레포지토리에 위임된다")
    void findById_delegatesToJpaRepository() {
        UUID id = UUID.randomUUID();
        Order order = mock(Order.class);
        given(orderJpaRepository.findById(id)).willReturn(Optional.of(order));

        Optional<Order> found = orderRepository.findById(id);

        assertThat(found).containsSame(order);
        then(orderJpaRepository).should(times(1)).findById(id);
    }

    @Test
    @DisplayName("findAll: Specification을 조립한 뒤 JPA 레포지토리에 위임된다")
    void findAll_buildsSpecAndDelegates() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));
        Page<Order> dummyPage = new PageImpl<>(List.of(mock(Order.class)));

        // OrderSpecQuery는 프로젝트 타입을 사용하세요.
        // 여기서는 null 허용 스펙(visibleFor/statusIn이 null일 때 no-op)임을 가정합니다.
        OrderSpecQuery specQuery = mock(OrderSpecQuery.class, RETURNS_DEEP_STUBS);
        given(specQuery.viewer()).willReturn(null); // 필요하면 실제 viewer 더미를 주입
        given(specQuery.status()).willReturn(null);

        ArgumentCaptor<Specification<Order>> specCaptor = ArgumentCaptor.forClass(
            Specification.class);

        given(orderJpaRepository.findAll(any(Specification.class), eq(pageable)))
            .willReturn(dummyPage);

        // when
        Page<Order> result = orderRepository.findAll(specQuery, pageable);

        // then
        assertThat(result).isSameAs(dummyPage);
        then(orderJpaRepository).should(times(1)).findAll(specCaptor.capture(), eq(pageable));

        Specification<Order> built = specCaptor.getValue();
        assertThat(built).as("조립된 Specification이 null이 아니어야 함").isNotNull();
    }

    @Test
    @DisplayName("findAll: Pageable 그대로 전달된다")
    void findAll_passesPageable() {
        Pageable pageable = PageRequest.of(2, 5, Sort.by("createdAt").descending());
        OrderSpecQuery specQuery = mock(OrderSpecQuery.class, RETURNS_DEEP_STUBS);
        given(orderJpaRepository.findAll(any(Specification.class), eq(pageable)))
            .willReturn(Page.empty(pageable));

        Page<Order> page = orderRepository.findAll(specQuery, pageable);

        assertThat(page.getSize()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(2);
        then(orderJpaRepository).should().findAll(any(Specification.class), eq(pageable));
    }
}
