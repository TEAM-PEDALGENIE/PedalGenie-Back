package com.pedalgenie.pedalgenieback.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberLoginRequestDto {
    @NotBlank(message = "이메일은 필수 입니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입니다.")
    private String password;
}
