package com.pedalgenie.pedalgenieback.domain.product.dto.request;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.subcategory.entity.SubCategory;
import lombok.Builder;

@Builder
public record ProductCreateRequestDto(String name, String description, Double rentPricePerDay
, Integer rentQuantityPerDay, Double price){

    public Product toEntity(Shop shop, SubCategory subCategory){
        return Product.builder()
                .name(name)
                .rentPricePerDay(rentPricePerDay)
                .rentQuantityPerDay(rentQuantityPerDay)
                .price(price)
                .shop(shop)
                .subCategory(subCategory)
                .build();

    }

}
