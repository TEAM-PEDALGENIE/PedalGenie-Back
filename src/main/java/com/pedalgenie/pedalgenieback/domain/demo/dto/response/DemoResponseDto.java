package com.pedalgenie.pedalgenieback.domain.demo.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.demo.entity.DemoStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DemoResponseDto {
    private Long demoId;
    private String demoStatus;

    @JsonFormat(pattern = "yyyy.MM.dd a h:mm")
    private LocalDateTime demoDate;

    @JsonFormat(pattern = "yyyy.MM.dd")
    private LocalDate reservedDate;

    private String productName;
    private String productThumbnailImageUrl;

    private String shopName;
    private String shopAddress;

    private Long memberId;
    private String memberNickName;
}