package com.pedalgenie.pedalgenieback.domain.shop.dto.request;

import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;
import com.pedalgenie.pedalgenieback.global.time.entity.DayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShopHoursDto{ // ModelAttribute 전달 시 데이터 바인딩을 위해 setter 와 생성자가 필요. 반드시 class 로 할 것
        private DayType dayType;
        private LocalTime openTime;
        private LocalTime closeTime;
        private LocalTime breakStartTime;
        private LocalTime breakEndTime;


    public static ShopHoursDto from(ShopHours shopHours) {
        return new ShopHoursDto(
                shopHours.getDayType(),
                shopHours.getOpenTime(),
                shopHours.getCloseTime(),
                shopHours.getBreakStartTime(),
                shopHours.getBreakEndTime()
        );
    }

    public static ShopHours toEntity(ShopHoursDto dto, Shop shop) {
        return ShopHours.builder()
                .shop(shop)
                .dayType(dto.getDayType())
                .openTime(dto.getOpenTime())
                .closeTime(dto.getCloseTime())
                .breakStartTime(dto.getBreakStartTime())
                .breakEndTime(dto.getBreakEndTime())
                .build();
    }
}

