package com.pedalgenie.pedalgenieback.domain.shop.dto.response;

import com.pedalgenie.pedalgenieback.domain.product.dto.response.ProductResponse;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;

import java.util.List;


// 매장 상세 조회 dto
public record GetShopResponse (
        Long shopId,
        String shopname,
        String address,
        String contactNumber,
        String businessHours,
        String imageUrl,
        Boolean isLiked,
        List<ProductResponse> products// 해당 매장의 상품 목록
) {
    public static GetShopResponse from(Shop shop, Boolean isLiked, List<ProductResponse> products){

        return new GetShopResponse(
                shop.getId(),
                shop.getShopname(),
                shop.getAddress(),
                shop.getContactNumber(),
                shop.getBusinessHours(),
                shop.getImageUrl(),
                isLiked != null ? isLiked : null,
                products
        );
    }
}
