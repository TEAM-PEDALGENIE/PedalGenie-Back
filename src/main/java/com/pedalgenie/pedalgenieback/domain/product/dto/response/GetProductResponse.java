package com.pedalgenie.pedalgenieback.domain.product.dto.response;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;

// 상품 상세 조회 dto
public record GetProductResponse(
        String name,
        String shopName,
        String businessHours,
        Double price,
        Double rentPricePerDay,
        Boolean isRentable,
        Boolean isPurchasable,
        Boolean isDemoable
) {
    public static GetProductResponse of(Product product){
        return new GetProductResponse(
                product.getName(),
                product.getShop().getShopname(),
                product.getShop().getBusinessHours(),
                product.getPrice(),
                product.getRentPricePerDay(),
                product.getIsRentable(),
                product.getIsPurchasable(),
                product.getIsDemoable()
        );
    }
}
