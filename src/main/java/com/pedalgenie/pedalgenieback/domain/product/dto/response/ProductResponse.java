package com.pedalgenie.pedalgenieback.domain.product.dto.response;

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
        String thumbnailImage
) {

    public static ProductResponse from(final GetProductQueryResponse product, ProductImageDto productImage){
        return new ProductResponse(
                product.id(),
                product.shopName(),
                product.name(),
                product.rentPricePerDay(),
                product.isRentable(),
                product.isPurchasable(),
                product.isDemoable(),
                productImage != null ? productImage.imageUrl() : null
        );

    }

}
