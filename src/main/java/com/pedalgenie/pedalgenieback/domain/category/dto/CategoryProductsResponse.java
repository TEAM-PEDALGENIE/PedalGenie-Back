package com.pedalgenie.pedalgenieback.domain.category.dto;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;

public record CategoryProductsResponse(
        String name,
        Double rentPricePerDay,
        String shopName
) {
    public static CategoryProductsResponse from(final Product product){
        return new CategoryProductsResponse(
                product.getName(),
                product.getRentPricePerDay(),
                product.getShop().getShopname()
        );
    }
}
