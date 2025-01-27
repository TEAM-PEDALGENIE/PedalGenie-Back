package com.pedalgenie.pedalgenieback.domain.fcm.application;

import com.google.firebase.messaging.*;

import com.pedalgenie.pedalgenieback.domain.fcm.Fcm;
import com.pedalgenie.pedalgenieback.domain.fcm.repository.FcmRepository;
import com.pedalgenie.pedalgenieback.domain.fcm.dto.FcmSendRequest;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmPushService {

    private final String NOTIFICATION_TITLE = "musai";

    private final FcmRepository fcmRepository;

    @Transactional
    public String saveToken(Long memberId, String token){
        Fcm fcm = fcmRepository.findByMember_MemberId(memberId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));

        fcm.updateToken(token);

        fcmRepository.save(fcm);

        return fcm.getToken();
    }


    @Async
    public void sendMessage(FcmSendRequest request){ // 1:1 단건 발송
        try {
            Message message = getMessage(request);
            FirebaseMessaging.getInstance().sendAsync(message).get();
        } catch (ExecutionException | InterruptedException e){
            log.error("FCM 메시지 전송 실패: ", e);
//            throw new CustomException(ErrorCode.FAILED_SEND_MESSAGE);
        }
    }

    private Message getMessage(FcmSendRequest request){

        WebpushNotification webpushNotification = WebpushNotification.builder()
                .setTitle(NOTIFICATION_TITLE)
                .setBody(request.content())
                .build();

        // 알림에 추가 데이터 첨부
        WebpushConfig webpushConfig = WebpushConfig.builder()
                .setNotification(webpushNotification)
                .putData("title", request.title())
                .putData("content", request.content())
                .putData("actionType", request.notificationType())
                .build();


        return Message.builder()
                .setToken(request.token())
                .setWebpushConfig(webpushConfig)
                .build();

    }
}
