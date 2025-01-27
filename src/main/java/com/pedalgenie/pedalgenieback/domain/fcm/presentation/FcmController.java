package com.pedalgenie.pedalgenieback.domain.fcm.presentation;

import com.pedalgenie.pedalgenieback.domain.fcm.application.FcmPushService;
import com.pedalgenie.pedalgenieback.domain.fcm.dto.FcmSendRequest;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class FcmController {

    private final FcmPushService fcmPushService;
    private final TokenProvider tokenProvider;

    // 토큰 저장 api
    @PostMapping("/token")
    public ResponseEntity<ResponseTemplate<String>> saveToken(@RequestBody String token, HttpServletRequest request){

        Long memberId = null;
        // 쿠키에서 리프레시 토큰 추출 및 유효성 검증
        String refreshToken = tokenProvider.getRefreshTokenFromRequest(request);
        Boolean isValid = tokenProvider.isVaildRefreshToken(refreshToken);

        // 리프레시 토큰이 유효할 때 memberId 추출
        if(isValid) {
            memberId = tokenProvider.getMemberIdFromToken(refreshToken);
        }

        String response = fcmPushService.saveToken(memberId, token);

        return ResponseTemplate.createTemplate(HttpStatus.CREATED, true,"FCM 토큰 저장 성공", response);

    }


    // 메시지 전송 api
    @PostMapping("/api/fcm")
    public ResponseEntity<ResponseTemplate<String>> sendMessage(
            @Valid @RequestBody FcmSendRequest request
    ){
        try {
            fcmPushService.sendMessage(request);
            return ResponseTemplate.createTemplate(HttpStatus.OK, true, "FCM 메시지 전송 성공", null);
        } catch (CustomException e) {
            return ResponseTemplate.createTemplate(HttpStatus.BAD_REQUEST, false, "FCM 메시지 전송 실패", e.getMessage());
        }
    }
}
