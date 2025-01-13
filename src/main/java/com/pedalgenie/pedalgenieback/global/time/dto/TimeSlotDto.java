package com.pedalgenie.pedalgenieback.global.time.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class TimeSlotDto {
    @JsonFormat(pattern = "HH:mm")
    private LocalTime slotTime; // 시간대 9:00
    private boolean isAvailable; // 예약 가능 여부
}
