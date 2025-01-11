package com.pedalgenie.pedalgenieback.global.time.application;

import com.pedalgenie.pedalgenieback.global.time.entity.DayType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TimeService {
    private final HolidayService holidayService;

    // 날짜에 해당하는 DayType 반환 메서드
    public DayType getDayType(LocalDate date) {
        if (holidayService.isHoliday(date)) {
            return DayType.HOLIDAY;
        } else if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return DayType.WEEKEND;
        } else {
            return DayType.WEEKDAY;
        }
    }


}
