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

}
