package com.pedalgenie.pedalgenieback.domain.available.application;

import com.pedalgenie.pedalgenieback.domain.available.dto.AvailableTimeResponse;
import com.pedalgenie.pedalgenieback.domain.available.dto.AvailableTimeSlotResponse;
import com.pedalgenie.pedalgenieback.domain.available.entity.AvailableDateTime;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.available.entity.TimeSlot;
import com.pedalgenie.pedalgenieback.domain.available.repository.AvailableTimeRepository;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import com.pedalgenie.pedalgenieback.global.time.application.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.TreeMap;


import static com.pedalgenie.pedalgenieback.domain.available.entity.AvailableStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AvailableTimeService {

    private final ProductRepository productRepository;
    private final AvailableTimeRepository availableTimeRepository;
    private final TimeService timeService;

    // 매일 자정에 실행: 기존 데이터 삭제 + 새 데이터 생성
    public void refreshAvailableTimes() {
        // 기존 데이터 삭제
        availableTimeRepository.deleteAll();

        // 대여 가능한 상품 조회
        List<Product> rentableProducts = productRepository.findAllByIsRentableTrue();

        // DELETED 상태 유지
        List<AvailableDateTime> deletedSlots = findDeletedSlots();
        for (AvailableDateTime deletedSlot : deletedSlots) {
            // DELETED 상태를 유지하도록 복원
            availableTimeRepository.save(deletedSlot);
        }

        // 각 상품에 대한 예약 데이터 생성
        for (Product product : rentableProducts) {
            generateAvailableTimes(product.getId());
        }

    }

    // DELETED 상태의 예약 데이터를 조회하는 메서드
    public List<AvailableDateTime> findDeletedSlots() {
        return availableTimeRepository.findByRentStatus(DELETED);
    }

    // 일자 저장
    public void generateAvailableTimes(Long productId) {

        Product product = getProduct(productId);
        Long shopId = product.getShop().getId();
        LocalDate today = LocalDate.now();

        // 기존 데이터 조회
        List<AvailableDateTime> existingAvailableTimes = availableTimeRepository.findByProductId(productId);
        List<AvailableDateTime> availableTimes = new ArrayList<>();

        // 28일간 예약 데이터 생성
        for (int i = 1; i <= 28; i++) {
            LocalDate targetDate = today.plusDays(i);

            // ShopHours 조회
            ShopHours shopHours = timeService.determineShopHours(shopId, targetDate);

            if (shopHours != null) {

                // 해당 날짜의 DELETED 상태는 필터링
                List<AvailableDateTime> existingSlotsForDate = existingAvailableTimes.stream()
                        .filter(slot -> slot.getLocalDate().equals(targetDate)
                                && slot.getRentStatus() != DELETED)
                        .toList();

                // 타임 슬롯
                availableTimes.addAll(generateAvailableTimeSlots(
                        productId, targetDate,
                        shopHours.getOpenTime(),
                        shopHours.getCloseTime(),
                        shopHours.getBreakStartTime(),
                        shopHours.getBreakEndTime(),
                        existingSlotsForDate
                ));
            }
        }
        log.info("Saving available times: {}", availableTimes.size());
        availableTimeRepository.saveAll(availableTimes);
        log.info("Saved available times successfully");


    }

    private List<AvailableDateTime> generateAvailableTimeSlots(
            Long productId,
            LocalDate targetDate,
            LocalTime openTime,
            LocalTime closeTime,
            LocalTime breakStartTime,
            LocalTime breakEndTime,
            List<AvailableDateTime> existingSlotsForDate) {

        // 타임 슬롯 생성
        List<TimeSlot> timeSlots = TimeSlot.generateTimeSlots(openTime, closeTime);

        // 점심시간 예외 처리
        List<TimeSlot> validTimeSlots = TimeSlot.exceptTime(timeSlots, breakStartTime, breakEndTime);

        List<AvailableDateTime> availableDateTimes = new ArrayList<>();
        for (TimeSlot timeSlot : validTimeSlots) {

            // 기존의 예약 시간이 없다면 새로 생성
            boolean slotExists = existingSlotsForDate.stream()
                    .anyMatch(existingSlot -> existingSlot.getLocalTime()
                            .equals(timeSlot.getStartTime()));

            if (!slotExists) {
                AvailableDateTime availableTime = AvailableDateTime.builder()
                        .productId(productId)
                        .localDate(targetDate)
                        .localTime(timeSlot.getStartTime())
                        .rentStatus(OPEN) // 초기: 대여 가능 상태
                        .build();
                availableDateTimes.add(availableTime);
            }
        }
        return availableDateTimes;
    }

    // 상품의 대여 가능 날짜 조회
    public List<AvailableTimeResponse> findAvailableDatesWithStatus(Long productId) {

        List<AvailableDateTime> availableTimes = availableTimeRepository.findByProductId(productId);


        return availableTimes.stream()
                .collect(Collectors.groupingBy(
                        AvailableDateTime::getLocalDate,
                        TreeMap::new, // 날짜를 오름차순으로 정렬
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<AvailableDateTime> slotsForDate = entry.getValue();

                    // 상태 결정: 모든 슬롯이 DELETED인지 확인
                    boolean allDeleted = slotsForDate.stream().allMatch(slot -> slot.getRentStatus() == DELETED);
                    boolean hasOpen = slotsForDate.stream().anyMatch(slot -> slot.getRentStatus() == OPEN);

                    String dateStatus = allDeleted ? DELETED.name()
                            : (hasOpen ? OPEN.name() : USED.name());

                    return AvailableTimeResponse.builder()
                            .productId(productId)
                            .localDate(date)
                            .rentStatus(dateStatus)
                            .build();
                })
                .toList();
    }



    // 픽업 가능한 시간대 조회
    public List<AvailableTimeSlotResponse> findAvailableTimesForDate(Long productId, LocalDate targetDate) {

//        // DELETED 상태를 제외하고 조회
//        List<AvailableDateTime> availableTimes =
//                availableTimeRepository.findByProductIdAndLocalDateAndStatus(productId, targetDate, DELETED);

        List<AvailableDateTime> availableTimes =
                availableTimeRepository.findByProductIdAndLocalDate(productId,targetDate);

        return availableTimes.stream()
                .map(slot ->AvailableTimeSlotResponse.builder()
                        .time(slot.getLocalTime())
                        .status(slot.getRentStatus().name())
                        .build())
                .toList();
    }

    private Product getProduct(final Long productId){
        return productRepository.findById(productId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
    }

}