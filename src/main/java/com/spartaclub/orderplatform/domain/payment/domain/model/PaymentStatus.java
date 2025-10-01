package com.spartaclub.orderplatform.domain.payment.domain.model;

public enum PaymentStatus {
    PAYMENT_PENDING, // 결제 시도 중/미완료
    AUTHORIZED, // 승인만, 캡처 전 – 카드/해외 PG에 자주 있음
    CAPTURED, // 정식 매출 확정 = 결제완료
    CANCELED, // 승인 후 캡처 전 취소
    FAILED, // PG 오류/한도초과 등 실패
    REFUNDED // 전액 환불

    /* 실무에서 보편적으로 쓰이는 상태 값을 일단 넣어둠.
    추후 필요 없는 값들은 삭제 필요 */
}
