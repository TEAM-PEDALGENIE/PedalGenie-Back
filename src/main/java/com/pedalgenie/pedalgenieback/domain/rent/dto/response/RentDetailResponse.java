package com.pedalgenie.pedalgenieback.domain.rent.dto.response;

import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 대여 상세 조회
public record RentDetailResponse(
        Long rentId,
        String rentStatus,
        String productName,
        String productImage,
        String shopName,
        String shopDetailAddress,
        LocalDate rentStartDate,
        LocalDate rentEndDate,
        Long rentDuration,
        BigDecimal rentPricePerDay,
        LocalDateTime rentStartDateTime, // 픽업시간
        String memberName,
        LocalDate paymentDate

) {

    public static RentDetailResponse from(Rent rent, String productImageUrl) {


        Long rentDuration = java.time.Duration.between(
                rent.getRentStartTime(),
                rent.getRentEndTime()
        ).toDays(); // 대여 기간 계산


        return new RentDetailResponse(
                rent.getId(),
                rent.getRentStatusType().name(),
                rent.getProduct().getName(),
                productImageUrl,
                rent.getProduct().getShop().getShopname(),
                rent.getProduct().getShop().getDetailAddress(),
                rent.getRentStartTime().toLocalDate(), // 날짜만 저장
                rent.getRentEndTime().toLocalDate(),   // 날짜만 저장
                rentDuration,
                rent.getProduct().getRentPricePerDay(),
                rent.getRentStartTime(),
                rent.getMember().getNickname(),
                LocalDate.now()
        );
    }

}
