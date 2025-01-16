package com.pedalgenie.pedalgenieback.domain.available.dto;

import com.pedalgenie.pedalgenieback.domain.available.entity.AvailableDateTime;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class AvailableTimeResponse {
    private Long productId;
    private LocalDate localDate;
    private String rentStatus;

    // AvailableDateTime -> AvailableTimeResponse 변환 메서드
    public static List<AvailableTimeResponse> from(List<AvailableDateTime> availableDateTimes) {
        return availableDateTimes.stream()
                .map(availableTime -> AvailableTimeResponse.builder()
                        .productId(availableTime.getProductId())
                        .localDate(availableTime.getLocalDate())
                        .rentStatus(availableTime.getRentStatus().name())
                        .build())
                .toList();
    }
}


