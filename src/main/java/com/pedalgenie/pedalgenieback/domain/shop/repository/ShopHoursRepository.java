package com.pedalgenie.pedalgenieback.domain.shop.repository;

import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;
import com.pedalgenie.pedalgenieback.global.time.entity.DayType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopHoursRepository extends JpaRepository<ShopHours, Long> {
    // shopId로 날짜 타입에 해당하는 shopHours 조회 메서드
    Optional<ShopHours> findByShopIdAndDayType(Long shopId, DayType dayType);

}