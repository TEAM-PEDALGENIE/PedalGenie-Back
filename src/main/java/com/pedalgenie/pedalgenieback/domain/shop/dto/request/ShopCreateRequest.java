package com.pedalgenie.pedalgenieback.domain.shop.dto.request;

import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;


public record ShopCreateRequest(

        String shopname,
        String address,
        String contactNumber,
        Integer demoQuantityPerDay,
        String businessHours,
        String detailAddress,
        String description,
        Integer instrumentCount

) {
    public Shop toEntity(String url){
        return Shop.builder()
                .name(shopname)
                .address(address)
                .contactNumber(contactNumber)
                .demoQuantityPerDay(demoQuantityPerDay)
                .businessHours(businessHours)
                .imageUrl(url)
                .detailAddress(detailAddress)
                .description(description)
                .instrumentCount(instrumentCount)
                .build();


    }
}
