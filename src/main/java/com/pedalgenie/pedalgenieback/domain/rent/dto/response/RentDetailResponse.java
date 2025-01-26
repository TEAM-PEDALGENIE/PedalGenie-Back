package com.pedalgenie.pedalgenieback.domain.rent.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        LocalDate rentStartDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        LocalDate rentEndDate,
        Long rentDuration,
        Long rentPricePerDay,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd'T'HH:mm")
        LocalDateTime rentStartDateTime, // 픽업시간
        String memberName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        LocalDate paymentDate

) {

    public static RentDetailResponse from(Rent rent, String productImageUrl) {


        Long rentDuration = java.time.Duration.between(
                rent.getRentStartTime(),
                rent.getRentEndTime()
        ).toDays(); // 대여 기간 계산

        Long rentPricePerDayLong = rent.getProduct().getRentPricePerDay().longValue();


        return new RentDetailResponse(
                rent.getId(),
                rent.getRentStatusType().name(),
                rent.getProduct().getName(),
                productImageUrl,
                rent.getProduct().getShop().getShopname(),
                rent.getProduct().getShop().getDetailAddress(),
                rent.getRentStartTime().toLocalDate(),
                rent.getRentEndTime().toLocalDate(),
                rentDuration,
                rentPricePerDayLong,
                rent.getRentStartTime(),
                rent.getMember().getNickname(),
                LocalDate.now()
        );
    }

}
