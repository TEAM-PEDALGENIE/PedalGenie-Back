package com.pedalgenie.pedalgenieback.domain.rent.entity;

public enum RentStatusType {
    ORDER_PENDING, // 주문 확인 중
    PICKUP_SCHEDULED, // 픽업 예정
    RENTED, // 대여 중
    RETURNED, // 반납 완료
    CANCEL_REQUESTED, // 취소 접수
    CANCEL_COMPLETED // 취소 완료

}
