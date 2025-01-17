package com.pedalgenie.pedalgenieback.domain.available.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class AvailableTimeSlotResponse {

    private LocalTime time;
    private String status;
    private Long availableDateTimeId;
}
