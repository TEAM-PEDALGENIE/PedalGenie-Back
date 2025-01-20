package com.pedalgenie.pedalgenieback.domain.rent.dto.response;


import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record RentListResponse(

        Long rentId,
        String rentStatus,
        LocalDateTime rentStartTime,
        LocalDateTime rentEndTime,
        LocalTime pickUpTime,
        Long productId,
        String productName,
        String shopName,
        String shopDetailAddress,
        String productImageUrl

){
    public static RentListResponse from(Rent rent, String productImageUrl) {
        return new RentListResponse(
                rent.getId(),
                rent.getRentStatusType().name(),
                rent.getAvailableDateTime().getLocalDate().atTime(rent.getAvailableDateTime().getLocalTime()),
                rent.getRentEndTime(),
                rent.getAvailableDateTime().getLocalTime(),
                rent.getProduct().getId(),
                rent.getProduct().getName(),
                rent.getProduct().getShop().getShopname(),
                rent.getProduct().getShop().getDetailAddress(),
                productImageUrl
        );
    }
}
