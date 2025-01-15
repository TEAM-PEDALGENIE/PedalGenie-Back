package com.pedalgenie.pedalgenieback.domain.rent.presentation;

import com.pedalgenie.pedalgenieback.domain.rent.application.RentQueryService;
import com.pedalgenie.pedalgenieback.domain.rent.application.RentService;
import com.pedalgenie.pedalgenieback.domain.rent.dto.request.RentRequest;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.RentDetailResponse;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.RentResponse;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.jwt.AuthUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@AllArgsConstructor
@RequestMapping("/rents")
public class RentController {

    private final RentService rentService;
    private final RentQueryService rentQueryService;
    // 대여 생성
    @PostMapping
    public ResponseEntity<ResponseTemplate<RentResponse>> createRent(@RequestBody RentRequest request){

        Long memberId = AuthUtils.getCurrentMemberId();
        RentResponse response = rentService.create(memberId,request);

        return ResponseTemplate.createTemplate(HttpStatus.CREATED,true,"대여 생성 성공", response);
    }

    @GetMapping("/{rentId}")
    public ResponseEntity<ResponseTemplate<RentDetailResponse>> getRentDetail(@PathVariable Long rentId) {
        RentDetailResponse response = rentQueryService.getRentDetail(rentId);
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "대여 상세 조회 성공", response);
    }


}
