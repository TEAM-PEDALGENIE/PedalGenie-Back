package com.pedalgenie.pedalgenieback.domain.product.repository;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductQueryResponse;


import java.util.List;

public interface ProductQueryRepositoryCustom {


    List<GetProductQueryResponse> findPagingProducts(Category category, FilterRequest request);

}
