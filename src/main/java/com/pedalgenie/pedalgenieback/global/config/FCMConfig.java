package com.pedalgenie.pedalgenieback.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FCMConfig {

    private final FCMProperties fcmProperties;

    @Bean
    public FirebaseMessaging firebaseMessaging(){
        try{
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(fcmProperties.getConfig().getInputStream())
                    .createScoped(List.of(fcmProperties.getScope()));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(googleCredentials)
                    .build();

            // 중복 초기화 방지
            if(FirebaseApp.getApps().isEmpty()){
                FirebaseApp.initializeApp(options);
                log.info("FCM 설정 성공");
            }

            FirebaseApp firebaseApp = FirebaseApp.getInstance();
            return FirebaseMessaging.getInstance(firebaseApp);

        }catch (IOException e){
            throw new CustomException(ErrorCode.FIREBASE_INITIALIZATION_FAILED);
        }
    }
}
