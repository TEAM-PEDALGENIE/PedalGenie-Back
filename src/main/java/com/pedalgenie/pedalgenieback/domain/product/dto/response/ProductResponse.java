package com.pedalgenie.pedalgenieback.domain.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.productImage.application.dto.ProductImageDto;

import java.math.BigDecimal;

// api 응답 전용
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드 제외
public record ProductResponse(
        Long id,
        String name,
        Long shopId,
        String shopName,
        BigDecimal rentPricePerDay,
        Boolean isRentable,
        Boolean isPurchasable,
        Boolean isDemoable,
        String imageUrl,
        Boolean isLiked
) {

    public static ProductResponse from(final Product product, ProductImageDto productImage, Boolean isLiked){
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getShop().getId(),
                product.getShop().getShopname(),
                product.getRentPricePerDay(),
                product.getIsRentable(),
                product.getIsPurchasable(),
                product.getIsDemoable(),
                productImage != null ? productImage.imageUrl() : null,
                isLiked !=null ? isLiked: null
        );

    }

}
