package com.pedalgenie.pedalgenieback.domain.shop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드 제외
public record ShopResponse(
        Long shopId, // shopId 추가
        String shopname,
        String shopImageUrl,

        Boolean isLiked,
        // 해당 매장이 보유한 상품 리스트
        List<ShopProductResponse> products
) {
    public static ShopResponse from(final Shop shop, Boolean isLiked, List<ShopProductResponse> products){

        return new ShopResponse(
                shop.getId(),
                shop.getShopname(),
                shop.getImageUrl(),
                isLiked != null ? isLiked : null,
                products
        );
    }
}
