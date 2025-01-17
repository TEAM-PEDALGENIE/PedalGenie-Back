package com.pedalgenie.pedalgenieback.domain.available.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class AvailableTimeSlotResponse { // 대여 가능(픽업) 시간대 응답 dto

    private LocalTime time;
    private String status;
    private Long availableDateTimeId;
}
