package com.pedalgenie.pedalgenieback.domain.demo.presentation;

import com.pedalgenie.pedalgenieback.domain.demo.application.DemoService;
import com.pedalgenie.pedalgenieback.domain.demo.dto.request.DemoRequestDto;
import com.pedalgenie.pedalgenieback.domain.demo.dto.response.DemoPatchResponseDto;
import com.pedalgenie.pedalgenieback.domain.demo.dto.response.DemoResponseDto;
import com.pedalgenie.pedalgenieback.domain.member.entity.MemberRole;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import com.pedalgenie.pedalgenieback.global.jwt.AuthUtils;
import com.pedalgenie.pedalgenieback.global.time.dto.DateDto;
import com.pedalgenie.pedalgenieback.global.time.dto.TimeSlotDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Demo api", description = "시연 신청, 시간, 상세, 목록 조회, 상태 수정 기능을 포함합니다.")
public class DemoController {
    private final DemoService demoService;

    // 4주간 예약 가능한 일자 조회
    @GetMapping("/api/demos/dates/{productId}")
    public ResponseEntity<ResponseTemplate<List<DateDto>>> getAvailableDateList(
            @PathVariable Long productId) {
        List<DateDto> availableDateList = demoService.getAvailableDateList(productId);

        // 결과 반환
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "시연 가능일 목록 조회 성공", availableDateList);
    }


    // 특정 날짜의 예약 가능한 시간대 조회
    @GetMapping("/api/demos/times/{productId}")
    public ResponseEntity<ResponseTemplate<List<TimeSlotDto>>> getAvailableSlots(
            @PathVariable Long productId,
            @RequestParam LocalDate date) {
        List<TimeSlotDto> availableSlots = demoService.generateDemoAvailableSlots(productId, date);

        if (availableSlots.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_SHOP_HOURS);
        }

        // 결과 반환
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "시연 예약 가능 시간대 조회 성공", availableSlots);
    }

    // 시연 예약 생성
    @PostMapping("/demos")
    public ResponseEntity<ResponseTemplate<DemoResponseDto>> createDemo(@RequestBody DemoRequestDto demoRequestDto) {
        Long memberId = AuthUtils.getCurrentMemberId();
        DemoResponseDto response = demoService.createDemo(demoRequestDto, memberId);
        return ResponseTemplate.createTemplate(HttpStatus.CREATED, true, "시연 생성 성공", response);
    }

    // 시연 상세 조회
    @GetMapping("/demos/{demoId}")
    public ResponseEntity<ResponseTemplate<DemoResponseDto>> getDemo(@PathVariable Long demoId) {
        Long memberId = AuthUtils.getCurrentMemberId();
        DemoResponseDto response = demoService.getDemo(demoId, memberId);
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "시연 상세 조회 성공", response);
    }

    // 시연 목록 조회
    @GetMapping("/demos")
    public ResponseEntity<ResponseTemplate<List<DemoResponseDto>>> getDemoList() {
        Long memberId = AuthUtils.getCurrentMemberId();
        List<DemoResponseDto> demoList = demoService.getDemoList(memberId);
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "시연 목록 조회 성공", demoList);
    }

    // 시연 상태 수정
    @PatchMapping("/demos/{demoId}")
    public ResponseEntity<ResponseTemplate<DemoPatchResponseDto>> updateDemoStatus(@PathVariable Long demoId){
    Long memberId = AuthUtils.getCurrentMemberId();
        DemoPatchResponseDto response = demoService.updateDemoStatus(demoId, memberId);
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "시연 상태 수정 성공", response);
    }

    // 어드민 - 시연 상세 조회
    @GetMapping("/admin/demos/{demoId}")
    public ResponseEntity<ResponseTemplate<DemoResponseDto>> getAdminDemo(@PathVariable Long demoId) {
        // 어드민이 아닐 때 예외처리
        if (!AuthUtils.getCurrentRole().equals(MemberRole.ADMIN.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ROLE);
        }
        DemoResponseDto response = demoService.getAdminDemo(demoId);
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "어드민 시연 상세 조회 성공", response);
    }

    // 어드민 - 시연 목록 조회
    @GetMapping("/admin/demos")
    public ResponseEntity<ResponseTemplate<List<DemoResponseDto>>> getAdminDemoList() {
        // 어드민이 아닐 때 예외처리
        if (!AuthUtils.getCurrentRole().equals(MemberRole.ADMIN.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ROLE);
        }

        List<DemoResponseDto> demoList = demoService.getAdminDemoList();
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "어드민 시연 목록 조회 성공", demoList);
    }


    // 어드민 - 시연 상태 수정
    @PatchMapping("/admin/demos/{demoId}")
    public ResponseEntity<ResponseTemplate<DemoPatchResponseDto>> updateAdminDemoStatus(@PathVariable Long demoId,
                                                                                        @RequestParam("status")String status){
        // 어드민이 아닐 때 예외처리
        if (!AuthUtils.getCurrentRole().equals(MemberRole.ADMIN.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ROLE);
        }
        DemoPatchResponseDto response = demoService.updateAdminDemoStatus(demoId, status);
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "어드민 시연 상태 수정 성공", response);
    }


}
