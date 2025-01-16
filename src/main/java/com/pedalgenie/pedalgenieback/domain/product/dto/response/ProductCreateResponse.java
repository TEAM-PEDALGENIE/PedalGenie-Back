package com.pedalgenie.pedalgenieback.domain.product.dto.response;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;

public record ProductCreateResponse(
        Long id,
        String name,
        Double rentPricePerDay,
        Integer rentQuantityPerDay,
        Double price,
        Boolean isRentable,
        Boolean isPurchasable,
        Boolean isDemoable,
        String shopName,
        String subCategoryName,
        String descriptionUrl
) {
    public static ProductCreateResponse from(Product product) {
        return new ProductCreateResponse(
                product.getId(),
                product.getName(),
                product.getRentPricePerDay(),
                product.getRentQuantity(),
                product.getPrice(),
                product.getIsRentable(),
                product.getIsPurchasable(),
                product.getIsDemoable(),
                product.getShop().getShopname(),
                product.getSubCategory().getName(),
                product.getDescriptionUrl()
        );
    }
}
