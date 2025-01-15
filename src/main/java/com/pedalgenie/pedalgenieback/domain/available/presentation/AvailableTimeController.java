package com.pedalgenie.pedalgenieback.domain.available.presentation;

import com.pedalgenie.pedalgenieback.domain.available.application.AvailableTimeService;
import com.pedalgenie.pedalgenieback.domain.available.dto.AvailableTimeResponse;
import com.pedalgenie.pedalgenieback.domain.available.repository.AvailableTimeRepository;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/available-times")
public class AvailableTimeController {
    private final AvailableTimeService availableTimeService;
    private final AvailableTimeRepository availableTimeRepository;

    @GetMapping("/{productId}")
    public ResponseEntity<ResponseTemplate<List<AvailableTimeResponse>>> getAvailableTimes(@PathVariable Long productId) {
        List<AvailableTimeResponse> availableTimes = availableTimeService.findAvailableDatesWithStatus(productId);

        return ResponseTemplate.createTemplate(HttpStatus.CREATED,true,"대여 가능 시간 조회 성공",availableTimes);
    }
}
