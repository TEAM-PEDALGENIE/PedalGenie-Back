package com.pedalgenie.pedalgenieback.domain.search.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.ProductResponse;
import com.pedalgenie.pedalgenieback.domain.shop.application.ShopDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드 제외
public record SearchResponse(
        List<ProductResponse> products,
        List<ShopDto> shops,
        int totalProducts,
        int totalShops
) {
    public static SearchResponse of(List<ProductResponse> products, List<ShopDto> shops){
        return new SearchResponse(
                products,
                shops,
                products.size(),
                shops.size()
        );
    }
}
