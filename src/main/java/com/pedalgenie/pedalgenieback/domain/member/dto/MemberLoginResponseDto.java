package com.pedalgenie.pedalgenieback.domain.member.dto;

import com.pedalgenie.pedalgenieback.global.jwt.TokenDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberLoginResponseDto {
    private MemberResponseDto memberResponseDto;
    private TokenDto tokenDto;
}
