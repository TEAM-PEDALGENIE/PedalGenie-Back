package com.pedalgenie.pedalgenieback.domain.product.dto.response;

public record GetProductQueryResponse( // 쿼리 결과 전용

        String name,
        String shopName,
        Double rentPricePerDay
) {
    public GetProductQueryResponse{

    }
}
