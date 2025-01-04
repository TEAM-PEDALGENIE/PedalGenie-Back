package com.pedalgenie.pedalgenieback.domain.product.dto.response;

import java.util.List;

public record GetProductsResponse( // 전체 응답 포장 (상품 목록을 포함)
        List<ProductResponse> products
) {
    public static GetProductsResponse from(List<ProductResponse> products){
        return new GetProductsResponse(products);
    }
}
