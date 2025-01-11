package com.pedalgenie.pedalgenieback.domain.demo.repository;

import com.pedalgenie.pedalgenieback.domain.demo.entity.DemoSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DemoSlotRepository extends JpaRepository<DemoSlot, Long> {
    // 특정 날짜와 가게에 대해 예약 가능한 슬롯 개수 조회
    @Query("SELECT COUNT(ds) FROM DemoSlot ds WHERE ds.shopId = :shopId AND ds.demoDate = :demoDate AND ds.isAvailable = true")
    long countAvailableSlots(Long shopId, LocalDate demoDate);

    // 특정 날짜와 가게에 대한 슬롯 리스트 조회
    List<DemoSlot> findByShopIdAndDemoDate(Long shopId, LocalDate demoDate);

    // 특정 시간과 가게에 대한 슬롯 조회
    Optional<DemoSlot> findByShopIdAndDemoDateAndTimeSlot(Long shopId, LocalDate demoDate, LocalTime timeSlot);

    // 특정 가게의 슬롯 갯수 조회
    long countByShopId(Long shopId);

    // 특정 일자 슬롯 삭제
    int deleteByDemoDate(LocalDate demoDate);
}
