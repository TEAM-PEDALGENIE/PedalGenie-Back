package com.pedalgenie.pedalgenieback.domain.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.productImage.application.dto.ProductImageDto;

import java.util.List;

// 상품 상세 조회 dto
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드 제외
public record GetProductResponse(
        String name,
        String shopName,
        String businessHours,
        Double price,
        Double rentPricePerDay,
        Boolean isRentable,
        Boolean isPurchasable,
        Boolean isDemoable,

        // 상품 설명 텍스트, 이미지 추가
        List<ProductImageDto> productImage,
        Boolean isLiked
) {
    public static GetProductResponse of(Product product, List<ProductImageDto> productImage, Boolean isLiked){
        return new GetProductResponse(
                product.getName(),
                product.getShop().getShopname(),
                product.getShop().getBusinessHours(),
                product.getPrice(),
                product.getRentPricePerDay(),
                product.getIsRentable(),
                product.getIsPurchasable(),
                product.getIsDemoable(),
                productImage,
                isLiked != null ? isLiked : null
        );
    }
}
