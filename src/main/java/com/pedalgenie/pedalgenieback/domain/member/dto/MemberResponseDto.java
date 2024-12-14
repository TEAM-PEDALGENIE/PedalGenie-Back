package com.pedalgenie.pedalgenieback.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드 json에서 제외
public class MemberResponseDto {
    private String email;
    private String nickname;
    private String role;
    // 추후 제거 필요
    private String accessToken;
    private String refreshToken;
}
