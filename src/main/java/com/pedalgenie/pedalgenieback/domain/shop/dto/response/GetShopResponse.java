package com.pedalgenie.pedalgenieback.domain.shop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.ProductResponse;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;


// 매장 상세 조회 dto
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetShopResponse (
        Long shopId,
        String shopname,
        String address,
        String contactNumber,
        Integer instrumentCount, // 보유 상품 개수 추가
        String imageUrl,
        Boolean isLiked,
        List<ProductResponse> products,
        List<ShopHours> shopHours
) {
    public static GetShopResponse from(Shop shop, Boolean isLiked, List<ProductResponse> products){

        return new GetShopResponse(
                shop.getId(),
                shop.getShopname(),
                shop.getAddress(),
                shop.getContactNumber(),
                shop.getInstrumentCount(),
                shop.getImageUrl(),
                isLiked != null ? isLiked : null,
                products,
                shop.getShopHours()
        );
    }
}
