package com.pedalgenie.pedalgenieback;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedalgenie.pedalgenieback.domain.fcm.application.FcmPushService;
import com.pedalgenie.pedalgenieback.domain.fcm.dto.FcmSendRequest;
import com.pedalgenie.pedalgenieback.global.jwt.JwtFilter;
import com.pedalgenie.pedalgenieback.global.jwt.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class FcmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private FcmPushService fcmPushService;


    @Test
    void FCM_토큰을_저장한다() throws Exception {
        // given
        String token = "valid-fcm-token"; // 테스트용 FCM 토큰
        String refreshToken = "valid-refresh-token"; // 테스트용 리프레시 토큰
        String authorizationToken = "valid-authorization-token"; // 테스트용 Authorization 토큰 (예: Bearer <token>)

        // 쿠키 생성 및 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

        // TokenProvider 메서드 모킹
        when(tokenProvider.getRefreshTokenFromRequest(any(HttpServletRequest.class))).thenReturn(refreshToken);
        when(tokenProvider.isVaildRefreshToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getMemberIdFromToken(refreshToken)).thenReturn(1L);  // memberId는 1L로 설정

        // FCM 토큰 저장 서비스 모킹
        when(fcmPushService.saveToken(1L, token)).thenReturn("success");

        // JwtFilter에서 memberId를 mock으로 반환하도록 설정 (필터의 동작을 우회)
        // 여기서 Authorization 헤더와 관련된 부분을 Mock으로 처리
        when(tokenProvider.getMemberIdFromToken(anyString())).thenReturn(1L);  // JWT 토큰에서 추출된 memberId가 1L

        // when & then
        mockMvc.perform(post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(token) // 요청 본문에 FCM 토큰, 문자열로 보내야 함
                        .cookie(refreshTokenCookie) // 쿠키에 리프레시 토큰 설정
                        .header("Authorization", "Bearer " + authorizationToken)) // Authorization 헤더 추가
                .andExpect(status().isCreated()) // HTTP 201 Created
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("FCM 토큰 저장 성공"))
                .andExpect(jsonPath("$.data").value("success"));  // data에 'success'가 포함된 값으로 검증

        // FcmPushService의 saveToken 호출 검증
        verify(fcmPushService, times(1)).saveToken(1L, token); // 저장된 멤버 ID와 FCM 토큰
    }




    @Test
    void 유효한_요청으로_메시지를_전송한다() throws Exception {
        // given
        FcmSendRequest request = new FcmSendRequest(
                "mock-fcm-token",  // 임의의 토큰
                "Test Title",      // 알림 제목
                "Test Content",    // 알림 내용
                "ACTION_TYPE"      // 알림 타입
        );

        // Service Mock 설정: 예외 없이 정상 실행
        doNothing().when(fcmPushService).sendMessage(request);

        // when & then
        mockMvc.perform(post("/api/fcm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request))) // Request를 JSON으로 변환
                .andExpect(status().isOk()) // HTTP 201 Created
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("FCM 메시지 전송 성공"))
                .andExpect(jsonPath("$.data").doesNotExist());  // data 필드가 존재하지 않음을 확인

        verify(fcmPushService, times(1)).sendMessage(request); // sendMessage 호출 검증
    }

    private static String asJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

