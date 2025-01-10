package com.pedalgenie.pedalgenieback.domain.product.dto.response;

public record ProductDescriptionUrlResponse(
        String descriptionUrl
) {
    public static ProductDescriptionUrlResponse from(String descriptionUrl){
        return new ProductDescriptionUrlResponse(descriptionUrl);
    }
}
