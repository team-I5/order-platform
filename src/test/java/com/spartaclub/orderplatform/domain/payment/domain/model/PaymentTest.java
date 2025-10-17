package com.spartaclub.orderplatform.domain.payment.domain.model;

import static com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus.AUTHORIZED;
import static com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus.CAPTURED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentTest {

    // --------- 헬퍼 ---------
    private Payment newAuthorizedPayment(long amount, String pgKey, String pgOrderId) {
        Order order = mock(Order.class);
        return Payment.ofStatus(order, AUTHORIZED, amount, pgKey, pgOrderId);
    }

    // ==========================
    // 생성/상태 변경
    // ==========================
    @Test
    @DisplayName("ofStatus(Authorized): 필드가 올바르게 세팅된다")
    void ofStatus_authorized_setsFields() {
        var p = newAuthorizedPayment(10_000L, "payKey-123", "ORD-1");
        assertThat(p.getStatus()).isEqualTo(AUTHORIZED);
        assertThat(p.getPaymentAmount()).isEqualTo(10_000L);
        assertThat(p.getPgPaymentKey()).isEqualTo("payKey-123");
        assertThat(p.getPgOrderId()).isEqualTo("ORD-1");
        assertThat(p.getOrder()).isNotNull();
    }

    @Test
    @DisplayName("changeStatus: 상태가 CAPTURED로 전이된다")
    void changeStatus_toCaptured() {
        var p = newAuthorizedPayment(10_000L, "k", "o");
        p.changeStatus(CAPTURED);
        assertThat(p.getStatus()).isEqualTo(CAPTURED);
    }

    // ==========================
    // 승인 검증
    // ==========================
    @Test
    @DisplayName("validateApproval: AUTHORIZED 상태 + 키/주문ID/금액 일치 시 통과")
    void validateApproval_success() {
        var p = newAuthorizedPayment(12_345L, "pgKey", "ORD-9");
        // 앞뒤 공백/대소문자 등의 실수를 줄이기 위한 trim 확인
        p.validateApproval("  pgKey  ", "  ORD-9  ", 12_345L);
        assertThat(p.getStatus()).isEqualTo(AUTHORIZED); // 상태 변화 없음(검증만)
    }

    @Test
    @DisplayName("validateApproval: AUTHORIZED가 아니면 예외")
    void validateApproval_invalidStatus() {
        var p = newAuthorizedPayment(10_000L, "k", "o");
        p.changeStatus(CAPTURED); // 승인 전용 검증이므로 실패해야 함
        assertThatThrownBy(() ->
            p.validateApproval("k", "o", 10_000L)
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("validateApproval: PG 결제키 불일치 예외")
    void validateApproval_pgKeyMismatch() {
        var p = newAuthorizedPayment(10_000L, "expected", "ORD-1");
        assertThatThrownBy(() ->
            p.validateApproval("wrong", "ORD-1", 10_000L)
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("validateApproval: PG 주문ID 불일치 예외")
    void validateApproval_pgOrderIdMismatch() {
        var p = newAuthorizedPayment(10_000L, "k", "ORD-1");
        assertThatThrownBy(() ->
            p.validateApproval("k", "ORD-2", 10_000L)
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("validateApproval: 결제 금액 불일치 예외")
    void validateApproval_amountMismatch() {
        var p = newAuthorizedPayment(10_000L, "k", "ORD-1");
        assertThatThrownBy(() ->
            p.validateApproval("k", "ORD-1", 9_999L)
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("validateApproval: 결제키 누락 예외(저장값 또는 요청값 null)")
    void validateApproval_missingPgKey() {
        var p = newAuthorizedPayment(10_000L, null, "ORD-1"); // 저장값 null
        assertThatThrownBy(() ->
            p.validateApproval(null, "ORD-1", 10_000L) // 요청값도 null
        ).isInstanceOf(BusinessException.class);
    }

    // ==========================
    // 취소 검증
    // ==========================
    @Test
    @DisplayName("checkCancelable: CAPTURED 상태 + 결제키 일치 시 통과")
    void checkCancelable_success() {
        var p = newAuthorizedPayment(10_000L, "pgKey", "ORD-1");
        p.changeStatus(CAPTURED);
        p.checkCancelable("  pgKey  "); // trim 허용 확인
        assertThat(p.getStatus()).isEqualTo(CAPTURED);
    }

    @Test
    @DisplayName("checkCancelable: CAPTURED가 아니면 예외")
    void checkCancelable_invalidStatus() {
        var p = newAuthorizedPayment(10_000L, "pgKey", "ORD-1");
        // 상태는 AUTHORIZED → 취소 불가
        assertThatThrownBy(() ->
            p.checkCancelable("pgKey")
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("checkCancelable: 결제키 누락/불일치 예외")
    void checkCancelable_keyMismatchOrMissing() {
        var p = newAuthorizedPayment(10_000L, "expected", "ORD-1");
        p.changeStatus(CAPTURED);
        // 불일치
        assertThatThrownBy(() ->
            p.checkCancelable("wrong")
        ).isInstanceOf(BusinessException.class);

        // 저장 키 null
        var q = newAuthorizedPayment(10_000L, null, "ORD-1");
        q.changeStatus(CAPTURED);
        assertThatThrownBy(() ->
            q.checkCancelable("anything")
        ).isInstanceOf(BusinessException.class);
    }

    // ==========================
    // 보조 메서드 단독 검증
    // ==========================
    @Test
    @DisplayName("validatePgPaymentKey: 동일 키면 통과, 다르면 예외")
    void validatePgPaymentKey_only() {
        var p = newAuthorizedPayment(10_000L, "K", "ORD");
        // 동일(대소문자 그대로 비교하므로 동일 문자열만 허용, 공백은 trim)
        p.validatePgPaymentKey("  K  ");

        // null 또는 다른 값이면 예외
        var x = newAuthorizedPayment(10_000L, "K", "ORD");
        assertThatThrownBy(() -> x.validatePgPaymentKey(null))
            .isInstanceOf(BusinessException.class);

        var y = newAuthorizedPayment(10_000L, "K", "ORD");
        assertThatThrownBy(() -> y.validatePgPaymentKey("DIFF"))
            .isInstanceOf(BusinessException.class);
    }
}

