package com.pedalgenie.pedalgenieback.domain.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;

// 쿼리 결과 전용
public record GetProductQueryResponse(

        Long id,
        String name,
        String shopName,
        Double rentPricePerDay,
        Boolean isRentable,
        Boolean isPurchasable,
        Boolean isDemoable,
        String thumbnailImageUrl // 여기 추가

) {
    @QueryProjection
    public GetProductQueryResponse{

    }
}
