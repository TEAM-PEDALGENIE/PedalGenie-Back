package com.pedalgenie.pedalgenieback.global.time.dto;

import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DateDto {
    private LocalDate date;
    private boolean isAvailable;

}
