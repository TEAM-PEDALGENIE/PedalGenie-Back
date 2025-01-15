package com.pedalgenie.pedalgenieback.domain.rent.presentation;

import com.pedalgenie.pedalgenieback.domain.rent.application.RentService;
import com.pedalgenie.pedalgenieback.domain.rent.dto.request.RentRequest;
import com.pedalgenie.pedalgenieback.domain.available.dto.AvailableTimeResponse;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.RentResponse;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.jwt.AuthUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
