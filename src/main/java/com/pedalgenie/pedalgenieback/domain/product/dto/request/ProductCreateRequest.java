package com.pedalgenie.pedalgenieback.domain.product.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.web.multipart.MultipartFile;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductCreateRequest(
        String name,
        Double rentPricePerDay,
        Integer rentQuantity,
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
            Integer rentQuantity,
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
                rentQuantity,
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

