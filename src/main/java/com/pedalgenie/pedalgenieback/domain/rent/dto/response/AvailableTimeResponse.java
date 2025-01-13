package com.pedalgenie.pedalgenieback.domain.rent.dto.response;

import com.pedalgenie.pedalgenieback.domain.rent.entity.availableTime.AvailableDateTime;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AvailableTimeResponse {
    private Long productId;
    private LocalDateTime localDateTime;
    private String rentStatus;

    // AvailableDateTime -> AvailableTimeResponse 변환 메서드
    public static List<AvailableTimeResponse> from(List<AvailableDateTime> availableDateTimes) {
        return availableDateTimes.stream()
                .map(availableTime -> AvailableTimeResponse.builder()
                        .productId(availableTime.getProductId())
                        .localDateTime(availableTime.getLocalDateTime())
                        .rentStatus(availableTime.getRentStatus().name())
                        .build())
                .toList();
    }
}


