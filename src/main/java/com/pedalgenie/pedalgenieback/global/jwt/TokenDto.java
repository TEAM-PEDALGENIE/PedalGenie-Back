package com.pedalgenie.pedalgenieback.global.jwt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenDto {
    private String accessToken;
    private String refreshToken;
}
