package com.pedalgenie.pedalgenieback.domain.demo.application;

import com.pedalgenie.pedalgenieback.domain.demo.entity.DemoSlot;
import com.pedalgenie.pedalgenieback.domain.demo.repository.DemoSlotRepository;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.repository.ShopRepository;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;
import com.pedalgenie.pedalgenieback.domain.shop.repository.ShopHoursRepository;
import com.pedalgenie.pedalgenieback.global.time.entity.DayType;
import com.pedalgenie.pedalgenieback.global.time.application.TimeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@EnableScheduling
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DemoSlotService {
    private final DemoSlotRepository demoSlotRepository;
    private final ShopRepository shopRepository;
    private final ShopHoursRepository shopHoursRepository;
    private final TimeService timeService;

    @Transactional
    public void createDemoSlotsForDay(Long shopId, LocalDate demoDate) {
        DayType dayType = timeService.getDayType(demoDate);

        // 해당 날짜의 ShopHours 조회
        ShopHours shopHours = shopHoursRepository.findByShopIdAndDayType(shopId, dayType)
                .orElse(null); // 운영시간이 없으면 null 반환

        // 운영 시간이 없으면 시연 슬롯을 생성하지 않음
        if (shopHours == null) {
            return;
        }

        // 가게 운영 시간 조회
        LocalTime startTime = shopHours.getOpenTime();
        LocalTime endTime = shopHours.getCloseTime();
        LocalTime breakStartTime = shopHours.getBreakStartTime();
        LocalTime breakEndTime = shopHours.getBreakEndTime();

        while (!startTime.isAfter(endTime)) {
            // 점심시간 사이는 예약 불가로 상태 생성
            boolean isBreakTime = !startTime.isBefore(breakStartTime) && startTime.isBefore(breakEndTime);
            boolean isAvailable = !isBreakTime;

            // DemoSlot 생성
            DemoSlot demoSlot = DemoSlot.builder()
                    .shopId(shopId)
                    .demoDate(demoDate)
                    .timeSlot(startTime)
                    .bookedQuantity(0)  // 초기에는 예약된 수가 0
                    .isAvailable(isAvailable)
                    .build();

            demoSlotRepository.save(demoSlot);
            startTime = startTime.plusMinutes(30);  // 30분 단위로 타임슬롯 생성
        }
    }

    // 주어진 shopId와 date에 해당하는 DemoSlot을 조회하는 메서드
    public List<DemoSlot> getDemoSlotsByShopAndDate(Long shopId, LocalDate demoDate) {
        return demoSlotRepository.findByShopIdAndDemoDate(shopId, demoDate);
    }

    // 초기 4주치 데이터 생성, 애플리케이션 실행시 수행
    @PostConstruct
    public void initializeDemoSlots() {
        // 초기 데이터가 이미 존재하는지 확인
        List<Shop> shops = shopRepository.findAll();
        for (Shop shop : shops) {
            // 해당 shopId에 대한 DemoSlot이 존재하지 않으면 초기화
            if (demoSlotRepository.countByShopId(shop.getId()) == 0) {
                createInitialDemoSlots(shop);
            }
        }
    }

    private void createInitialDemoSlots(Shop shop) {
        LocalDate startDate = LocalDate.now();
        for (int i = 1; i <= 28; i++) {
            LocalDate targetDate = startDate.plusDays(i);
            createDemoSlotsForDay(shop.getId(), targetDate);
        }
    }

    // 매일 자정 전날 데이터 삭제 및 새 데이터 생성
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void manageDailyDemoSlots() {
        LocalDate yesterday = LocalDate.now().minusDays(1); // 전날
        LocalDate nextDay = LocalDate.now().plusDays(28); // 28일 후의 날짜

        // 전날의 DemoSlot 삭제
        demoSlotRepository.deleteByDemoDate(yesterday);

        // 28일 후의 새로운 DemoSlot 생성
        List<Shop> shops = shopRepository.findAll();
        for (Shop shop : shops) {
            createDemoSlotsForDay(shop.getId(), nextDay);
        }
    }

}
