package com.pedalgenie.pedalgenieback.domain.product.dto.response;

import java.util.List;

public record GetProductsResponse( // 전체 응답 포장
        List<ProductResponse> products
) {
    public static GetProductsResponse from(List<GetProductQueryResponse> products){
        List<ProductResponse> productResponses = products.stream()
                .map(ProductResponse::from)
                .toList();

        return new GetProductsResponse(productResponses);
    }
}
