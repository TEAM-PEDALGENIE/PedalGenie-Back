package com.pedalgenie.pedalgenieback.domain.subcategory.dto;

public record FilterSubCategoryMetaResponse(
        String keywords,
        FilterSubCategoryResponse categories
) {
    public static FilterSubCategoryMetaResponse of(FilterSubCategoryResponse categoryResponse){
        return new FilterSubCategoryMetaResponse(
                "카테고리",
                categoryResponse
        );
    }
}
