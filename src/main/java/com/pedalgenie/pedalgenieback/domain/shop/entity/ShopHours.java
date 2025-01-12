package com.pedalgenie.pedalgenieback.domain.shop.entity;

import com.pedalgenie.pedalgenieback.global.time.entity.DayType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shopHoursId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayType dayType; // WEEKDAY, WEEKEND, HOLIDAY

    @Column(nullable = false)
    private LocalTime openTime; // 오픈 시간

    @Column(nullable = false)
    private LocalTime closeTime; // 마감 시간

    private LocalTime breakStartTime; // 점심시간 시작

    private LocalTime breakEndTime; // 점심시간 종료

    @Builder
    public ShopHours(Long shopHoursId, Shop shop, DayType dayType, LocalTime openTime, LocalTime closeTime,
                     LocalTime breakStartTime, LocalTime breakEndTime) {
        this.shopHoursId = shopHoursId;
        this.shop = shop;
        this.dayType = dayType;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
    }
}
