package com.pedalgenie.pedalgenieback.domain.product.dto.request;

import jakarta.validation.constraints.Size;

import java.util.List;

public record FilterRequest(
        Boolean isRentable,
        Boolean isPurchasable,
        Boolean isDemoable,

        @Size(max = 10, message = "카테고리는 최대 10개까지 선택할 수 있습니다.")
        List<Long> subCategoryIds
) {
    public static FilterRequest of(
            Boolean isRentable,
            Boolean isPurchasable,
            Boolean isDemoable,
            List<Long> subCategoryIds
    ){
        return new FilterRequest(isRentable,isPurchasable,isDemoable, subCategoryIds);
    }
}
