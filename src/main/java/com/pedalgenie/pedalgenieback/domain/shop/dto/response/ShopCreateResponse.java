package com.pedalgenie.pedalgenieback.domain.shop.dto.response;

import com.pedalgenie.pedalgenieback.domain.shop.dto.request.ShopHoursDto;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;

import java.util.List;

public record ShopCreateResponse(
        Long id,
        String name,
        String address,
        String contactNumber,
        String imageUrl,
        String detailAddress,
        String description,
        Integer demoQuantityPerDay,
        Integer instrumentCount,
        List<ShopHoursDto> shopHours
) {
    public ShopCreateResponse(Shop shop) {
        this(
                shop.getId(),
                shop.getShopname(),
                shop.getAddress(),
                shop.getContactNumber(),
                shop.getImageUrl(),
                shop.getDetailAddress(),
                shop.getDescription(),
                shop.getDemoQuantityPerDay(),
                shop.getInstrumentCount(),
                shop.getShopHours().stream()
                        .map(ShopHoursDto::from)
                        .toList()
        );
    }
}
