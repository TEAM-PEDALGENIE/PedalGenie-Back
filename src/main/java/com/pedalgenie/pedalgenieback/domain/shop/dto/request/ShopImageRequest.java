package com.pedalgenie.pedalgenieback.domain.shop.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record ShopImageRequest(
        String shopname,
        String address,
        String contactNumber,
        Integer demoQuantityPerDay,
        String businessHours,
        MultipartFile imageUrl,
        String detailAddress,
        String description,
        Integer instrumentCount
) {
    // ShopCreateRequest로 변환
    public ShopCreateRequest toCreateRequest() {
        return new ShopCreateRequest(
                shopname,
                address,
                contactNumber,
                demoQuantityPerDay,
                businessHours,
                detailAddress,
                description,
                instrumentCount
        );
    }
}


