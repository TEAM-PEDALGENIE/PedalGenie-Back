package com.pedalgenie.pedalgenieback.domain.rent.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;
import com.pedalgenie.pedalgenieback.domain.rent.entity.RentStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentResponse { // 예약 생성 이후 조회

    private Long rentId;
    private RentStatusType rentStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime rentStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime rentEndTime;

    private LocalTime pickUpTime;


    private Long productId;
    private String productName;
    private Long price;
    private Long fee;
    private Long totalPrice;

    private Long availableDateTimeId;


    public static RentResponse from(final Rent rent){

        BigDecimal price = rent.getProduct().getPrice();
        BigDecimal fee = price.multiply(BigDecimal.valueOf(0.1)); // 수수료 계산
        BigDecimal totalPrice = price.add(fee); // 총 금액 계산

        Long priceLong = price.longValue();
        Long feeLong = fee.longValue();
        Long totalPriceLong = totalPrice.longValue();



        return RentResponse.builder()
                .rentId(rent.getId())
                .rentStatus(rent.getRentStatusType())
                .rentStartTime(rent.getAvailableDateTime().getLocalDate().atTime(rent.getAvailableDateTime().getLocalTime()))
                .rentEndTime(rent.getRentEndTime())
                .pickUpTime(rent.getAvailableDateTime().getLocalTime())
                .productId(rent.getProduct().getId())
                .productName(rent.getProduct().getName())
                .price(priceLong)
                .fee(feeLong)
                .totalPrice(totalPriceLong)
                .availableDateTimeId(rent.getAvailableDateTime().getId())
                .build();
    }
}
