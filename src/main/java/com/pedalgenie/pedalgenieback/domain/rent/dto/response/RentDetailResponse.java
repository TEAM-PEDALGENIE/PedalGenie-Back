package com.pedalgenie.pedalgenieback.domain.rent.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RentDetailResponse(
        Long rentId,
        LocalDateTime rentStartTime,
        LocalDateTime rentEndTime,
        Double price,
        String memberName,
        LocalDate paymentDate,
        String rentStatus,
        String productName,
        String shopName

) {

}
