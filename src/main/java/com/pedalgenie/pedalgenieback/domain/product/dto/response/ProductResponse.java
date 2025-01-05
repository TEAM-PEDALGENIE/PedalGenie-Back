package com.pedalgenie.pedalgenieback.domain.product.dto.response;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.productImage.application.dto.ProductImageDto;

// api 응답 전용
public record ProductResponse(
        Long id,
        String shopName,
        String name,
        Double rentPricePerDay,
        Boolean isRentable,
        Boolean isPurchasable,
        Boolean isDemoable,
        String thumbnailImage // 여기 추가
) {

    public static ProductResponse from(final Product product, ProductImageDto productImage){
        return new ProductResponse(
                product.getId(),
                product.getShop().getShopname(),
                product.getName(),
                product.getRentPricePerDay(),
                product.getIsRentable(),
                product.getIsPurchasable(),
                product.getIsDemoable(),
                productImage != null ? productImage.imageUrl() : null
        );

    }

}
