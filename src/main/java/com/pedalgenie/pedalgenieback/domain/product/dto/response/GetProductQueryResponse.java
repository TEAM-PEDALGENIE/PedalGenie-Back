package com.pedalgenie.pedalgenieback.domain.product.dto.response;

// 쿼리 결과 전용
public record GetProductQueryResponse(

        Long id,
        String name,
        String shopName,
        Double rentPricePerDay,
        Boolean isRentable,
        Boolean isPurchasable,
        Boolean isDemoable

) {
    public GetProductQueryResponse{

    }
}
