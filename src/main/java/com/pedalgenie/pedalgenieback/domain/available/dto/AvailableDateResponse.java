package com.pedalgenie.pedalgenieback.domain.available.dto;

import com.pedalgenie.pedalgenieback.domain.available.entity.AvailableDateTime;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class AvailableDateResponse { // 대여 가능 날짜 응답 dto
    private Long productId;
    private LocalDate localDate;
    private String rentStatus;


    public static List<AvailableDateResponse> from(List<AvailableDateTime> availableDateTimes) {
        return availableDateTimes.stream()
                .map(availableTime -> AvailableDateResponse.builder()
                        .productId(availableTime.getProductId())
                        .localDate(availableTime.getLocalDate())
                        .rentStatus(availableTime.getRentStatus().name())
                        .build())
                .toList();
    }
}


