package com.pedalgenie.pedalgenieback.domain.article.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArticleProductResponseDto(
        Long id,
        String name,
        Long shopId,
        String shopName,
        BigDecimal rentPricePerDay,
        String imageUrl,
        Boolean isLiked
) {
    public static ArticleProductResponseDto from(final Product product, String thumbnailImage, Boolean isLiked) {
        return new ArticleProductResponseDto(
                product.getId(),
                product.getName(),
                product.getShop().getId(),
                product.getShop().getShopname(),
                product.getRentPricePerDay(),
                thumbnailImage,
                isLiked
        );
    }
}