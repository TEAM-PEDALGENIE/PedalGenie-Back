package com.pedalgenie.pedalgenieback.domain.rent.dto.request;


import java.time.LocalDateTime;


public record RentRequest(
        Long productId,
        Long availableDateTimeId,
        LocalDateTime rentStartDateTime,
        LocalDateTime rentEndDateTime

) {
}
