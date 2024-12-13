package com.pedalgenie.pedalgenieback.domain.member.controller;

import com.pedalgenie.pedalgenieback.domain.member.dto.MemberLoginRequestDto;
import com.pedalgenie.pedalgenieback.domain.member.dto.MemberLoginResponseDto;
import com.pedalgenie.pedalgenieback.domain.member.dto.MemberRegisterRequestDto;
import com.pedalgenie.pedalgenieback.domain.member.dto.MemberResponseDto;
import com.pedalgenie.pedalgenieback.domain.member.service.MemberService;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import com.pedalgenie.pedalgenieback.global.jwt.CustomUserDetails;
import com.pedalgenie.pedalgenieback.global.jwt.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth api", description = "어드민, 사장님 유저의 자체 로그인 기능을 포함합니다.")
public class MemberController {
    private final MemberService memberService;

    // 회원 가입
    @Operation(summary="자체 회원가입")
    @PostMapping("/auth/register")
    public ResponseEntity<ResponseTemplate<Object>> register(@Valid @RequestBody MemberRegisterRequestDto requestDto,
                                                             BindingResult bindingResult){
        // 필드 검증
        if(bindingResult.hasErrors()){
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        // 회원 가입
        MemberResponseDto responseDto = memberService.register(requestDto);

        return ResponseTemplate.createTemplate(HttpStatus.CREATED, true, "회원가입 성공", responseDto);
    }
    // 로그인
    @Operation(summary="자체 로그인")
    @GetMapping("/auth/login")
    public ResponseEntity<ResponseTemplate<Object>> login(@Valid @RequestBody MemberLoginRequestDto requestDto,
                                                          BindingResult bindingResult, HttpServletResponse response) {
        // 필드 검증
        if (bindingResult.hasErrors()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        // 로그인 처리
        MemberLoginResponseDto responseDto = memberService.login(requestDto);
        MemberResponseDto memberResponseDto = responseDto.getMemberResponseDto();

        // 엑세스 토큰 헤더에 추가
        TokenDto tokenDto = responseDto.getTokenDto();
        response.setHeader("Authorization", "Bearer " + tokenDto.getAccessToken());

        // 리프레시 토큰 쿠키에 추가
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true); // javascript로 접근 불가
//        refreshTokenCookie.setSecure(true); https only 나중에 도메인 붙이고 처리
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 30일
        response.addCookie(refreshTokenCookie);

        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "로그인 성공", memberResponseDto);
    }


    // 회원 조회
    @Operation(summary="회원 조회")
    @GetMapping("/members")
    public ResponseEntity<ResponseTemplate<MemberResponseDto>> getMemberInfo() {
        // SecurityContext에서 사용자 정보 가져오기
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long memberId = userDetails.getMemberId();

        // 회원 조회
        MemberResponseDto responseDto = memberService.getMemberInfo(memberId);

        return ResponseTemplate.createTemplate(HttpStatus.OK,true, "회원 조회 성공", responseDto);
    }
}
