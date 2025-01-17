package com.pedalgenie.pedalgenieback.domain.available.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AvailableDatePriceResponse {

    private List<AvailableDateResponse> availableDates;
    private BigDecimal price;
    private BigDecimal fee; // 수수료
    private BigDecimal totalPrice; // 총금액

    public AvailableDatePriceResponse(List<AvailableDateResponse> availableDates, BigDecimal price) {
        this.availableDates = availableDates;
        this.price = price;
        this.fee = price.multiply(BigDecimal.valueOf(0.1));
        this.totalPrice = price.add(fee);
    }
}

