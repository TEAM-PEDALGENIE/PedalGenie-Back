package com.pedalgenie.pedalgenieback.global.jwt.refresh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@RedisHash("refresh_tokens")
public class RefreshToken {
    @Id
    private Long memberId;

    @Indexed
    private String refreshToken;

}
