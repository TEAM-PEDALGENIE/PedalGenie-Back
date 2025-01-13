package com.pedalgenie.pedalgenieback.global.time.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DateDto {
    private LocalDate date;
    private boolean isAvailable;
}
