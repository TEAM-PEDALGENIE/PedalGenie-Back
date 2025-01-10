package com.pedalgenie.pedalgenieback.domain.shop.dto.response;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.productImage.application.dto.ProductImageDto;

// 특정 매장이 보유한 상품 정보
public record ShopProductResponse(
        Long id,
        String name,
        Double rentPricePerDay,
        String thumbnailImage

) {

    public static ShopProductResponse from(final Product product, ProductImageDto productImage){
        return new ShopProductResponse(
                product.getId(),
                product.getName(),
                product.getRentPricePerDay(),
                productImage != null ? productImage.imageUrl() : null
        );
    }
}
