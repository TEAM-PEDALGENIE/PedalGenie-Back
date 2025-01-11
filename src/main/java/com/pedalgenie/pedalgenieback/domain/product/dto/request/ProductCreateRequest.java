package com.pedalgenie.pedalgenieback.domain.product.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record ProductCreateRequest(
        String name,
        Double rentPricePerDay,
        Integer rentQuantityPerDay,
        Double price,
        Boolean isRentable,
        Boolean isPurchasable,
        Boolean isDemoable,
        String shopName,
        Long subCategoryId,
        MultipartFile descriptionFile
) {
    public static ProductCreateRequest of(
            String name,
            Double rentPricePerDay,
            Integer rentQuantityPerDay,
            Double price,
            Boolean isRentable,
            Boolean isPurchasable,
            Boolean isDemoable,
            String shopName,
            Long subCategoryId,
            MultipartFile descriptionFile) {
        return new ProductCreateRequest(
                name,
                rentPricePerDay,
                rentQuantityPerDay,
                price,
                isRentable,
                isPurchasable,
                isDemoable,
                shopName,
                subCategoryId,
                descriptionFile
        );
    }
}

