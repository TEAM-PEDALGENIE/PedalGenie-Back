package com.pedalgenie.pedalgenieback.domain.fcm.dto;


public record FcmSendRequest(
        String token,
        String title,
        String content,
        String notificationType) {
}
