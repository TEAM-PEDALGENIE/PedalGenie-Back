package com.pedalgenie.pedalgenieback.domain.available.presentation;

import com.pedalgenie.pedalgenieback.domain.available.application.AvailableTimeService;
import com.pedalgenie.pedalgenieback.domain.available.dto.AvailableDatePriceResponse;
import com.pedalgenie.pedalgenieback.domain.available.dto.AvailableDateResponse;
import com.pedalgenie.pedalgenieback.domain.available.dto.AvailableTimeSlotResponse;
import com.pedalgenie.pedalgenieback.domain.available.repository.AvailableTimeRepository;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AvailableTimeController {
    private final AvailableTimeService availableTimeService;
    private final AvailableTimeRepository availableTimeRepository;

    @GetMapping("/dates/{productId}")
    public ResponseEntity<ResponseTemplate<AvailableDatePriceResponse>> getAvailableDates(@PathVariable Long productId) {
        AvailableDatePriceResponse availableDate = availableTimeService.findAvailableDatesWithStatus(productId);

        return ResponseTemplate.createTemplate(HttpStatus.CREATED,true,"대여 가능 날짜 조회 성공", availableDate);
    }

    @GetMapping("/times/{productId}")
    public ResponseEntity<ResponseTemplate<List<AvailableTimeSlotResponse>>> getAvailableTimes(
            @PathVariable Long productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {

        List<AvailableTimeSlotResponse> availableTimes =
                availableTimeService.findAvailableTimesForDate(productId, targetDate);

        return ResponseTemplate.createTemplate(HttpStatus.CREATED,true,"대여 가능 시간 조회 성공",availableTimes);


    }

}
