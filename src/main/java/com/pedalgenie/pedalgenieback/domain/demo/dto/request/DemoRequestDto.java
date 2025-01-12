package com.pedalgenie.pedalgenieback.domain.demo.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DemoRequestDto {
    private LocalDateTime demoDate;
    private Long productId;
}
