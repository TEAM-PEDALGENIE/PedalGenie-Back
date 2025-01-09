package com.pedalgenie.pedalgenieback.domain.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;

// 쿼리 결과 전용
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetProductQueryResponse(

        Long id,
        String name,
        Long shopId,
        String shopName,
        Double rentPricePerDay,
        Boolean isRentable,
        Boolean isPurchasable,
        Boolean isDemoable,
        String imageUrl,
        Boolean isLiked

) {
    @QueryProjection
    public GetProductQueryResponse{

    }
}
