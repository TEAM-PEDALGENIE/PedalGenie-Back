package com.pedalgenie.pedalgenieback.domain.article.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArticleProductResponseDto(
        Long id,
        String shopName,
        String name,
        Double rentPricePerDay,
        String thumbnailImage,
        Boolean isLiked
) {
    public static ArticleProductResponseDto from(final Product product, String thumbnailImage, Boolean isLiked) {
        return new ArticleProductResponseDto(
                product.getId(),
                product.getShop().getShopname(),
                product.getName(),
                product.getRentPricePerDay(),
                thumbnailImage,
                isLiked
        );
    }
}