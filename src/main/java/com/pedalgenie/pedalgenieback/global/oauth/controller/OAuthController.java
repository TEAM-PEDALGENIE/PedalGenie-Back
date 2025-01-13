package com.pedalgenie.pedalgenieback.global.oauth.controller;

import com.pedalgenie.pedalgenieback.domain.member.dto.MemberLoginResponseDto;
import com.pedalgenie.pedalgenieback.domain.member.dto.MemberResponseDto;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.jwt.TokenDto;
import com.pedalgenie.pedalgenieback.global.oauth.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Kakao Login", description = "카카오 로그인 기능을 포함합니다.")
public class OAuthController {
    private final OAuthService oAuthService;

    // 카카오 로그인
    @Operation(summary="카카오 로그인")
    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
    @GetMapping("/kakao/callback")
    public ResponseEntity<ResponseTemplate<Object>> loginKakao(@RequestParam("code") String authorizationCode, HttpServletResponse response) {
        // 카카오 로그인
        MemberLoginResponseDto responseDto = oAuthService.loginKakao(authorizationCode);
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
}
