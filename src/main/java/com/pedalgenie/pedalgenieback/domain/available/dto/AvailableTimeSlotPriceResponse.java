package com.pedalgenie.pedalgenieback.domain.available.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimeSlotPriceResponse {

    private Long rentPricePerDay;
    private Long fee;
    private Long totalPrice;
    private List<AvailableTimeSlotResponse> availableTimeSlots;


    public AvailableTimeSlotPriceResponse(List<AvailableTimeSlotResponse> availableTimeSlots, BigDecimal rentPricePerDay) {

        BigDecimal feeAmount = rentPricePerDay.multiply(BigDecimal.valueOf(0.1)); // 수수료 계산
        BigDecimal totalAmount = rentPricePerDay.add(feeAmount); // 총 금액 계산

        this.rentPricePerDay = rentPricePerDay.longValue();
        this.fee = feeAmount.longValue();
        this.totalPrice = totalAmount.longValue();
        this.availableTimeSlots = availableTimeSlots;
    }

}
