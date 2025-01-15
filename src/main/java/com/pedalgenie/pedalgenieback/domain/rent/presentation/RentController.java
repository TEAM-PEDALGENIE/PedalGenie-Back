package com.pedalgenie.pedalgenieback.domain.rent.presentation;

import com.pedalgenie.pedalgenieback.domain.rent.application.RentQueryService;
import com.pedalgenie.pedalgenieback.domain.rent.application.RentService;
import com.pedalgenie.pedalgenieback.domain.rent.dto.request.RentRequest;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.RentDetailResponse;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.RentListResponse;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.RentResponse;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.jwt.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
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
    private final RentQueryService rentQueryService;

    // 대여 생성
    @PostMapping("/rents")
    @Operation(summary = "대여 생성")
    public ResponseEntity<ResponseTemplate<RentResponse>> createRent(@RequestBody RentRequest request){

        Long memberId = AuthUtils.getCurrentMemberId();
        RentResponse response = rentService.create(memberId,request);

        return ResponseTemplate.createTemplate(HttpStatus.CREATED,true,"대여 생성 성공", response);
    }

    // 대여 상세 조회
    @GetMapping("/rents/{rentId}")
    @Operation(summary = "대여 상세 조회")
    public ResponseEntity<ResponseTemplate<RentDetailResponse>> getRentDetail(@PathVariable Long rentId) {
        RentDetailResponse response = rentQueryService.getRentDetail(rentId);
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "대여 상세 조회 성공", response);
    }

    // 대여 목록 조회
    @GetMapping("/rents/list")
    @Operation(summary = "대여 목록 조회")
    public ResponseEntity<ResponseTemplate<List<RentListResponse>>> getRentList() {

        Long memberId = AuthUtils.getCurrentMemberId();

        List<RentListResponse> rentList = rentQueryService.getRentList(memberId);

        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "대여 목록 조회 성공", rentList);
    }

    // 어드민용, 대여 상태 픽업으로 변경
    @PutMapping("/admin/rents/{rentId}/pickup")
    @Operation(summary = "대여 상태를 픽업 예정으로 변경")
    public ResponseEntity<ResponseTemplate<RentDetailResponse>> updateRentStatusToPickup(
            @PathVariable Long rentId) {
        RentDetailResponse response = rentQueryService.updateRentStatusToPickUp(rentId);

        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "픽업 예정으로 상태 변경 성공", response);
    }

    // 직원 확인용, 대여 상태 사용 중으로 변경
    @PutMapping("/rents/{rentId}/in-use")
    @Operation(summary = "대여 상태를 사용 중으로 변경")
    public ResponseEntity<ResponseTemplate<RentDetailResponse>> updateRentStatusToInUse(
            @PathVariable Long rentId) {
        RentDetailResponse response = rentQueryService.updateRentStatusToInUse(rentId);

        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "사용 중으로 상태 변경 성공", response);
    }

    // 직원 확인용, 대여 상태 반납완료로 변경
    @PutMapping("/rents/{rentId}/returned")
    @Operation(summary = "대여 상태를 반납 완료로 변경")
    public ResponseEntity<ResponseTemplate<RentDetailResponse>> updateRentStatusToReturned(
            @PathVariable Long rentId) {
        RentDetailResponse response = rentQueryService.updateRentStatusToReturned(rentId);

        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "반납 완료로 상태 변경 성공", response);
    }

    // 어드민용, 취소로 변경
    @PutMapping("/rents/{rentId}/cancel")
    @Operation(summary = "대여 취소 ")
    public ResponseEntity<ResponseTemplate<RentDetailResponse>> cancelRent(
            @PathVariable Long rentId) {
        RentDetailResponse response = rentQueryService.cancelRent(rentId);

        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "대여 취소 성공", response);
    }


}
