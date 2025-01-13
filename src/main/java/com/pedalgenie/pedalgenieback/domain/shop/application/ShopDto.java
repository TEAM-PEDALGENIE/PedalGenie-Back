package com.pedalgenie.pedalgenieback.domain.shop.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드 제외
public record ShopDto(
        Long shopId,
        String shopname,
        String imageUrl,
        Boolean isLiked
) {
    public static ShopDto from(final Shop shop, Boolean isLiked){

        return new ShopDto(
                shop.getId(),
                shop.getShopname(),
                shop.getImageUrl(),
                isLiked != null ? isLiked : null
        );
    }
}
