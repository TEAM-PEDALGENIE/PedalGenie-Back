package com.pedalgenie.pedalgenieback.domain.product.dto.response;

import java.util.List;

public record FilterMetadataResponse(
        List<String> keywords,
        FilterResponse filters
) {
    public static FilterMetadataResponse of(FilterResponse filterResponse){
        return new FilterMetadataResponse(
                List.of("대여 가능", "시연 가능", "구매 가능"),
                filterResponse
        );
    }
}
