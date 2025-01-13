package com.pedalgenie.pedalgenieback.domain.rent.presentation;

import com.pedalgenie.pedalgenieback.domain.rent.application.RentService;
import com.pedalgenie.pedalgenieback.domain.rent.dto.request.RentRequest;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.AvailableTimeResponse;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.RentResponse;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.ScheduleResponse;
import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;
import com.pedalgenie.pedalgenieback.domain.rent.entity.availableTime.AvailableDateTime;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.jwt.AuthUtils;
import com.pedalgenie.pedalgenieback.global.time.dto.DateDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/rents")
public class RentController {

    private final RentService rentService;

    // 대여 생성
    @PostMapping
    public ResponseEntity<ResponseTemplate<RentResponse>> createRent(@RequestBody RentRequest request){

        Long memberId = AuthUtils.getCurrentMemberId();
        RentResponse response = rentService.create(memberId,request);

        return ResponseTemplate.createTemplate(HttpStatus.CREATED,true,"대여 생성 성공", response);
    }

    // 4주간 대여 가능한 일자 조회
    @GetMapping("/dates/{productId}")
    public ResponseEntity<ResponseTemplate<List<AvailableTimeResponse>>> getAvailableDateList(
            @PathVariable Long productId) {

        List<AvailableTimeResponse> availableDateList = rentService.findAvailableDateTimes(productId);

        // 결과 반환
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "대여 가능일 목록 조회 성공", availableDateList);
    }
}
