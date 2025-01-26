package com.pedalgenie.pedalgenieback.domain.available.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AvailableDatePriceResponse { // 날짜들과 가격 정보

    private Long rentPricePerDay;
    private Long fee; // 수수료
    private Long totalPrice; // 총금액
    private List<AvailableDateResponse> availableDates;


    public AvailableDatePriceResponse(List<AvailableDateResponse> availableDates, BigDecimal rentPricePerDay) {
        BigDecimal feeAmount = rentPricePerDay.multiply(BigDecimal.valueOf(0.1)); // 수수료 계산
        BigDecimal totalAmount = rentPricePerDay.add(feeAmount); // 총 금액 계산

        this.rentPricePerDay = rentPricePerDay.longValue();
        this.fee = feeAmount.longValue();
        this.totalPrice = totalAmount.longValue();
        this.availableDates = availableDates;

    }
}

