package com.pedalgenie.pedalgenieback.domain.shop.dto.response;

import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;

import java.util.List;
import java.util.Map;

// 매장 목록 조회 dto
public record GetShopsResponses(
        List<ShopResponse> shops
) {
    public static GetShopsResponses from(List<Shop> shops, Map<Shop, List<ShopProductResponse>> shopProductMap){
        List<ShopResponse> shopResponses = shops.stream()
                .map(shop -> {
                    List<ShopProductResponse> products = shopProductMap.get(shop); // 매장에 해당하는 상품 리스트 가져오기
                    return ShopResponse.from(shop, products); //상품 리스트와 함께 ShopResponse 생성
                })
                .toList();

        return new GetShopsResponses(shopResponses);
    }
}
