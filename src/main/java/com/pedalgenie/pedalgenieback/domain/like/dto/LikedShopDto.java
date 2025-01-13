package com.pedalgenie.pedalgenieback.domain.like.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikedShopDto {
    private Long shopId;
    private String shopName;
    private String thumbNailUrl;
}
