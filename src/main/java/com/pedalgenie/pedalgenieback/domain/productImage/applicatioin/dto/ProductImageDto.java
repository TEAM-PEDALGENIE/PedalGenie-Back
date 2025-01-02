package com.pedalgenie.pedalgenieback.domain.productImage.applicatioin.dto;

import com.pedalgenie.pedalgenieback.domain.productImage.ProductImage;

public record ProductImageDto(
        String imageUrl
) {
    public static ProductImageDto fromEntity(ProductImage productImage){
        return new ProductImageDto(
                productImage.getImageUrl()
        );
    }
}
