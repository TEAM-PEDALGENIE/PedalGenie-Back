package com.pedalgenie.pedalgenieback.domain.member.controller;

import com.pedalgenie.pedalgenieback.domain.member.dto.MemberLoginRequestDto;
import com.pedalgenie.pedalgenieback.domain.member.dto.MemberLoginResponseDto;
import com.pedalgenie.pedalgenieback.domain.member.dto.MemberRegisterRequestDto;
import com.pedalgenie.pedalgenieback.domain.member.dto.MemberResponseDto;
import com.pedalgenie.pedalgenieback.domain.member.service.MemberService;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import com.pedalgenie.pedalgenieback.global.jwt.AuthUtils;
import com.pedalgenie.pedalgenieback.global.jwt.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth api", description = "어드민, 사장님 유저의 자체 로그인 기능을 포함합니다.")
public class MemberController {
    private final MemberService memberService;

    @Value("${domain}")
    private String domain;

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
    @PostMapping("/auth/login")
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

        // 리프레시 토큰 쿠키 생성
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
                .sameSite("None") // 다른 도메인간 허용
                .domain(domain) // 도메인 정보 추가
                .httpOnly(true) // javascript로 접근 불가
                .secure(true) // https only
                .path("/") // 쿠키 경로 설정
                .maxAge(30 * 24 * 60 * 60) // 30일 (단위: 초)
                .build();

        // 쿠키 헤더에 추가
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "로그인 성공", memberResponseDto);
    }


    // 회원 조회
    @Operation(summary="회원 조회")
    @GetMapping("/members")
    public ResponseEntity<ResponseTemplate<MemberResponseDto>> getMemberInfo() {
        // 로그인한 memberId 가져오기
        Long memberId = AuthUtils.getCurrentMemberId();

        // 회원 조회
        MemberResponseDto responseDto = memberService.getMemberInfo(memberId);

        return ResponseTemplate.createTemplate(HttpStatus.OK,true, "회원 조회 성공", responseDto);
    }

    // 로그아웃
    @Operation(summary="로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ResponseTemplate<Object>> logout(HttpServletResponse response){
        // 로그인한 memberId 가져오기
        Long memberId = AuthUtils.getCurrentMemberId();

        // 로그아웃 처리
        memberService.logout(memberId);

        // 쿠키에서 리프레시 토큰 삭제
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", null)
                .path("/") // 쿠키 경로 설정
                .httpOnly(true) // javascript로 접근 불가
                .secure(true) // https only
                .maxAge(0) // 즉시 만료
                .build();

        // 쿠키 삭제 설정 헤더에 추가
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseTemplate.createTemplate(HttpStatus.OK,true, "로그아웃 성공", null);
    }

    // 회원 탈퇴
    @Operation(summary="회원 탈퇴")
    @DeleteMapping("/withdraw")
    public ResponseEntity<ResponseTemplate<MemberResponseDto>> withdraw(HttpServletResponse response){
        // 로그인한 memberId 가져오기
        Long memberId = AuthUtils.getCurrentMemberId();
        String role = AuthUtils.getCurrentRole();

        // 회원 탈퇴
        memberService.withdraw(memberId, role);

        // 쿠키에서 리프레시 토큰 삭제
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", null)
                .path("/") // 쿠키 경로 설정
                .httpOnly(true) // javascript로 접근 불가
                .secure(true) // https only
                .maxAge(0) // 즉시 만료
                .build();

        // 쿠키 삭제 설정 헤더에 추가
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseTemplate.createTemplate(HttpStatus.OK,true, "회원 탈퇴 성공", null);
    }

    // 엑세스 토큰 재발급
    @Operation(summary="엑세스 토큰 재발급")
//    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
    @GetMapping("/api/reissue")
    public ResponseEntity<ResponseTemplate<TokenDto>> reissue(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = Optional.ofNullable(request.getCookies())
                .orElseThrow(() -> new CustomException(ErrorCode.COOKIE_NOT_FOUND));

        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        // 토큰 재발급
        TokenDto tokenDto = memberService.reissue(refreshToken);

        // 엑세스 토큰 헤더에 추가
        response.setHeader("Authorization", "Bearer " + tokenDto.getAccessToken());

        return ResponseTemplate.createTemplate(HttpStatus.OK,true, "엑세스 토큰 재발급 성공", tokenDto);
    }
}
