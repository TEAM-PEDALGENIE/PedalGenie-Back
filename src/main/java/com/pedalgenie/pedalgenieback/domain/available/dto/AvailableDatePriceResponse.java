package com.pedalgenie.pedalgenieback.domain.available.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AvailableDatePriceResponse { // 날짜들과 가격 정보

    private Long price;
    private Long fee; // 수수료
    private Long totalPrice; // 총금액
    private List<AvailableDateResponse> availableDates;


    public AvailableDatePriceResponse(List<AvailableDateResponse> availableDates, BigDecimal price) {
        BigDecimal feeAmount = price.multiply(BigDecimal.valueOf(0.1)); // 수수료 계산
        BigDecimal totalAmount = price.add(feeAmount); // 총 금액 계산

        this.price = price.longValue();
        this.fee = feeAmount.longValue();
        this.totalPrice = totalAmount.longValue();
        this.availableDates = availableDates;

    }
}

