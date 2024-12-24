package com.pedalgenie.pedalgenieback.domain.product.dto.response;

public record ProductResponse( // api 응답 전용
                               String name,
                               Double rentPricePerDay,
                               String shopName
) {

    public static ProductResponse from(final GetProductQueryResponse product){
        return new ProductResponse(
                product.name(),
                product.rentPricePerDay(),
                product.shopName()
        );

    }

}
