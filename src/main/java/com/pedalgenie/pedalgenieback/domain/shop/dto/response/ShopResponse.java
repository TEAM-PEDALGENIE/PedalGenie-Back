package com.pedalgenie.pedalgenieback.domain.shop.dto.response;

import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;

import java.util.List;

public record ShopResponse(
        String shopname,
        // 해당 매장이 보유한 상품 리스트
        List<ShopProductResponse> products
) {
    public static ShopResponse from(final Shop shop, List<ShopProductResponse> products){

        return new ShopResponse(
                shop.getShopname(),
                products // 이미 변환된 리스트

        );
    }
}
