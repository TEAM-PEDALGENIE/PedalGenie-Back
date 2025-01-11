package com.pedalgenie.pedalgenieback.global.time.application;

import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;
import com.pedalgenie.pedalgenieback.domain.shop.repository.ShopHoursRepository;
import com.pedalgenie.pedalgenieback.global.time.entity.DayType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TimeService {
    private final HolidayService holidayService;
    private final ShopHoursRepository shopHoursRepository;

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

    // 가게 운영 정보에 따른 가게 운영시간 조회
    public ShopHours determineShopHours(Long shopId, LocalDate targetDate) {
        boolean isHoliday = holidayService.isHoliday(targetDate);
        // 공휴일
        if (isHoliday) {
            return shopHoursRepository.findByShopIdAndDayType(shopId, DayType.HOLIDAY).orElse(null);
        }
        // 주말
        else if (targetDate.getDayOfWeek().getValue() >= 6) {
            return shopHoursRepository.findByShopIdAndDayType(shopId, DayType.WEEKEND).orElse(null);
        }
        // 평일
        else {
            return shopHoursRepository.findByShopIdAndDayType(shopId, DayType.WEEKDAY).orElse(null);
        }
    }


}
