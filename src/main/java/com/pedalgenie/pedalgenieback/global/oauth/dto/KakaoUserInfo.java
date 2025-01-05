package com.pedalgenie.pedalgenieback.global.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoUserInfo {
    private final Long oauthId;
    private final String email;
    private final String nickname;
}
