package com.pedalgenie.pedalgenieback.domain.shop.dto.response;

import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;

public record ShopCreateResponse(
        Long id,
        String name,
        String address,
        String contactNumber,
        String businessHours,
        String imageUrl,
        String detailAddress,
        String description,
        Integer demoQuantityPerDay,
        Integer instrumentCount
) {
    public ShopCreateResponse(Shop shop) {
        this(
                shop.getId(),
                shop.getShopname(),
                shop.getAddress(),
                shop.getContactNumber(),
                shop.getBusinessHours(),
                shop.getImageUrl(),
                shop.getDetailAddress(),
                shop.getDescription(),
                shop.getDemoQuantityPerDay(),
                shop.getInstrumentCount()
        );
    }
}
