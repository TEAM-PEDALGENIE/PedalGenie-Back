package com.pedalgenie.pedalgenieback.global.time.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimeSlotDto {
    private String slotTime; // 시간대 9:00
    private boolean isAvailable; // 예약 가능 여부
}
