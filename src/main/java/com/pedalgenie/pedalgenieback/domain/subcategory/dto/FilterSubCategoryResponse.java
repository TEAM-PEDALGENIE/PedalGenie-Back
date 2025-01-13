package com.pedalgenie.pedalgenieback.domain.subcategory.dto;

import com.pedalgenie.pedalgenieback.domain.subcategory.entity.SubCategory;

import java.util.List;

public record FilterSubCategoryResponse(
        List<SubCategoryResponse> subCategories
) {
    public static FilterSubCategoryResponse of(List<SubCategory> subCategories){
        return new FilterSubCategoryResponse(
                subCategories.stream().map(SubCategoryResponse::from).toList()
        );
    }

    public record SubCategoryResponse(Long id, String name){
        public static SubCategoryResponse from(SubCategory subCategory){
            return new SubCategoryResponse(subCategory.getId(),
                    subCategory.getName());
        }
    }
}
