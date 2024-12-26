package com.pedalgenie.pedalgenieback.domain.product.repository;

import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductQueryResponse;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductQueryRepositoryCustom {

    @Query("SELECT p FROM Product p WHERE p.isRentable = :isRentable " +
            "AND p.isPurchasable = :isPurchasable " +
            "AND p.isDemoable = :isDemoable " +
            "AND p.subCategory.id IN :subCategoryIds")
    List<GetProductQueryResponse> findPagingProducts(
            Boolean isRentable,
            Boolean isPurchasable,
            Boolean isDemoable,
            List<Long> subCategoryIds
    );
}
