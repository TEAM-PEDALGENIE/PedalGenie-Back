package com.pedalgenie.pedalgenieback.global.oauth.controller;

import com.pedalgenie.pedalgenieback.domain.member.dto.MemberLoginResponseDto;
import com.pedalgenie.pedalgenieback.domain.member.dto.MemberResponseDto;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.jwt.TokenDto;
import com.pedalgenie.pedalgenieback.global.oauth.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Kakao Login", description = "카카오 로그인 기능을 포함합니다.")
public class OAuthController {
    private final OAuthService oAuthService;

    @Value("${domain}")
    private String domain;

    // 카카오 로그인
    @Operation(summary="카카오 로그인")
//    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
    @GetMapping("/kakao/callback")
    public ResponseEntity<ResponseTemplate<Object>> loginKakao(@RequestParam("code") String authorizationCode, HttpServletResponse response) {
        // 카카오 로그인
        MemberLoginResponseDto responseDto = oAuthService.loginKakao(authorizationCode);
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
}
