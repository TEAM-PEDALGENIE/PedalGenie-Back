package com.pedalgenie.pedalgenieback.domain.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class DemoPatchResponseDto {
    private Long demoId;
    private String demoStatus;

    @JsonFormat(pattern = "yyyy.MM.dd a h:mm")
    private LocalDateTime demoDate;

    @JsonFormat(pattern = "yyyy.MM.dd a h:mm")
    private LocalDateTime editedDate;
}
