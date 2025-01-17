package com.pedalgenie.pedalgenieback.domain.shop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.ProductResponse;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;

import java.util.List;


// 매장 상세 조회 dto
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetShopResponse (
        Long shopId,
        String shopname,
        String description,
        List<ShopHours> shopHours,
        String contactNumber,
        String address,
        Integer instrumentCount, // 보유 상품 개수 추가
        String shopImageUrl,
        Boolean isLiked,
        List<ProductResponse> products
) {
    public static GetShopResponse from(
            Shop shop,
            Boolean isLiked,
            List<ProductResponse> products,
            Integer instrumentCount){


        return new GetShopResponse(
                shop.getId(),
                shop.getShopname(),
                shop.getDescription(),
                shop.getShopHours(),
                shop.getContactNumber(),
                shop.getAddress(),
                instrumentCount,
                shop.getImageUrl(),
                isLiked != null ? isLiked : null,
                products
        );
    }
}
