package com.pedalgenie.pedalgenieback.domain.product.repository;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductQueryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface ProductQueryRepositoryCustom {


    Page<GetProductQueryResponse> findPagingProducts(
            Category category,
            FilterRequest request,
            Long memberId,
            Pageable pageable);
}
